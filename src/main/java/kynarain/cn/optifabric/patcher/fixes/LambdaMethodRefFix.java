/*
 * New in this 1.20.6 port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart; it is the 1.20.6 form of the fixer the 1.21.x line registers as LambdaMethodRefFix.
 *
 * OptiFine recompiles the classes it patches, and javac gives the lambda bodies it emits its own names
 * ("lambda$<enclosingMethod>$<n>") where Minecraft's own class declares them under an obfuscated name the
 * mappings translate. The body is registered through the class's own LambdaMetafactory bootstrap method
 * handle, so the two classes agree on everything but the name - and a mod that asks for the game's name
 * finds nothing:
 *
 *     game       BootstrapMethods #47 -> class_3898.method_17252(Lclass_3193;Ljava/lang/Runnable;)V
 *     OptiFine   BootstrapMethods #47 -> class_3898.lambda$protoChunkToFullChunk$36(...)
 *
 * c2me's c2me-opts-scheduling @Overwrites method_17252 (and method_19487, method_20579), and
 * c2me-threading-worldgen @Injects into method_17224; Mixin's defaultRequire then fails the whole target
 * class the moment a world starts, which in game is "Mixin transformation of net.minecraft.class_3898
 * failed" on the integrated server thread.
 *
 * The lambda is therefore renamed to the name the game gives it, and the handle that registers it is
 * repointed. Renaming rather than copying the vanilla method in next to OptiFine's matters: the vanilla
 * body is what the class's own handle would then run, while OptiFine's is what actually executes today -
 * renaming keeps OptiFine's body on the executed path, so a mod that overwrites the method replaces the
 * code that really runs.
 *
 * Because this runs before RestoreVanillaMethodsFix in the registration order, the names are occupied by
 * the time that fixer looks, so it leaves them alone instead of adding a second method under the same name
 * - which is what keeps the class's own bootstrap handles pointing at a method that exists.
 *
 * A registration whose parameters OptiFine recompiled into a different order keeps that order and is only
 * renamed. That is deliberate, and it is the one thing this fixer cannot repair. For class_3898's
 * method_17224 the three things that have to agree do not: OptiFine's patched class registers the lambda
 * with an argument list of (class_9259, class_3898, class_1923, class_2806, class_3193, Executor), the
 * lambda declares (class_1923, class_2806, class_3193, Executor, class_9259) and its body reads slot 2 as
 * the status and slot 3 as the chunk holder, while the game declares (class_1923, class_3193, class_2806,
 * Executor, class_9259). The body, the descriptor and the value list therefore already disagree in
 * OptiFine's own bytes, so there is no reordering that makes all three consistent *and* present the game's
 * signature: correcting the body's slots changes what it acts on, and correcting the descriptor without
 * the slots is a member that exists but lies - worse for c2me, whose @ModifyReturnValue would then read
 * the wrong values, than one that does not exist. Such a registration is reported and left renamed only.
 *
 * Two alignments are used, both taken from the two class files' own bytes:
 *
 *   1. Per host method. For every method the two classes have under the same name and descriptor, the
 *      LambdaMetafactory invokedynamic instructions inside it are aligned in order. This is the 1.21.x
 *      rule and it is exact: javac emits the registrations of a method in source order.
 *   2. Per bootstrap index. For registrations that the first pass could not reach - the host method's
 *      invokedynamic list changed shape - the two BootstrapMethods tables are walked together. The index
 *      is javac's, assigned in source order, and OptiFine's recompile preserves it as long as it neither
 *      adds nor removes a registration; the call-site shape (the invokedynamic's name and descriptor) is
 *      required to match at that index, and a registration that actually shifted shows up as a
 *      disagreement which is skipped rather than guessed.
 *
 * A pair is only renamed when all of the following hold, so a wrong edit cannot be made silently:
 *   * the registered handle has the same owner and tag on both sides;
 *   * the patched handle names a javac lambda of this class, the vanilla handle does not;
 *   * the vanilla name is free in the patched class (two methods cannot share a name and descriptor);
 *   * the lambda body exists under that name and descriptor in the patched class;
 *   * where the descriptors differ, they are a reordering of the same parameter types.
 */
package kynarain.cn.optifabric.patcher.fixes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LambdaMethodRefFix implements ClassFixer {
	/** how many of this run's renames had to leave a recompiled parameter list in place */
	private int leftAsIs;

	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		if (minecraft == null) return;

		int renamed = 0;
		leftAsIs = 0;
		int refused = 0;

		//The registrations pass 1 has already handled, by the name and descriptor the lambda had when it was
		//found: pass 2 must not look at them twice, and after a rename the handle would no longer match.
		Set<String> claimed = new HashSet<>();

		// ---- pass 1: align the registrations of each host method by position ---------------------------------
		for (MethodNode method : optifine.methods) {
			MethodNode vanilla = find(minecraft, method.name, method.desc);
			if (vanilla == null) continue;

			List<InvokeDynamicInsnNode> here = lambdaFactories(method);
			List<InvokeDynamicInsnNode> there = lambdaFactories(vanilla);

			// A different number means OptiFine added or removed a registration in this method; that is the
			// case pass 2 exists for, so this method is left to it rather than aligned by guesswork.
			if (here.size() != there.size()) continue;

			for (int i = 0; i < here.size(); i++) {
				Handle held = handleOf(here.get(i));

				if (claim(optifine, minecraft, there.get(i), here.get(i))) {
					if (held != null) claimed.add(held.getName() + held.getDesc());
					renamed++;
				}
			}
		}

		// ---- pass 2: align what pass 1 did not reach, by bootstrap index --------------------------------------
		List<InvokeDynamicInsnNode> vanillaSites = lambdaFactories(minecraft);
		List<InvokeDynamicInsnNode> patchedSites = lambdaFactories(optifine);

		if (vanillaSites.size() == patchedSites.size()) {
			for (int i = 0; i < vanillaSites.size(); i++) {
				InvokeDynamicInsnNode there = vanillaSites.get(i);
				InvokeDynamicInsnNode here = patchedSites.get(i);

				Handle patchedHandle = handleOf(here);
				Handle vanillaHandle = handleOf(there);
				if (patchedHandle == null || vanillaHandle == null) continue;

				// An index whose call site moved belongs to a registration OptiFine added or removed; the rest
				// of the table is then off by one and this pass must not touch it.
				if (!here.name.equals(there.name) || !here.desc.equals(there.desc)) continue;
				if (!patchedHandle.getDesc().equals(vanillaHandle.getDesc())) continue;
				if (claimed.contains(patchedHandle.getName() + patchedHandle.getDesc())) continue;

				if (claim(optifine, minecraft, there, here)) renamed++;
			}
		}

		for (MethodNode method : optifine.methods) {
			if (method.name.startsWith("lambda$") && find(minecraft, method.name, method.desc) == null) refused++;
		}

		if (renamed == 0 && refused == 0) return;

		System.out.println("[OptiFabric] " + optifine.name + ": " + renamed + " method reference(s) restored"
				+ (leftAsIs > 0 ? ", " + leftAsIs + " of them keeping a recompiled parameter list" : "")
				+ (refused > 0 ? " (" + refused + " javac lambda name(s) still unmatched)" : ""));
	}

	/**
	 * Renames one registration's lambda to the name the game gives it and repoints the handle, if the pair
	 * really is a name change and nothing else. Returns whether the rename was made.
	 */
	private boolean claim(ClassNode optifine, ClassNode minecraft, InvokeDynamicInsnNode vanillaSite,
			InvokeDynamicInsnNode patchedSite) {
		Handle patchedHandle = handleOf(patchedSite);
		Handle vanillaHandle = handleOf(vanillaSite);
		if (patchedHandle == null || vanillaHandle == null) return false;

		if (!patchedHandle.getOwner().equals(optifine.name) || !vanillaHandle.getOwner().equals(optifine.name)) return false;
		if (patchedHandle.getTag() != vanillaHandle.getTag()) return false;

		// The name that is already right needs nothing, and a lambda standing in for another lambda is not a
		// game method at all.
		if (patchedHandle.getName().equals(vanillaHandle.getName())) return false;
		if (!patchedHandle.getName().startsWith("lambda$")) return false;
		if (vanillaHandle.getName().startsWith("lambda$")) return false;

		MethodNode lambda = find(optifine, patchedHandle.getName(), patchedHandle.getDesc());
		if (lambda == null) return false;

		// Two methods cannot share a name and descriptor, and taking one away could break its own callers.
		if (find(optifine, vanillaHandle.getName(), vanillaHandle.getDesc()) != null) {
			System.out.println("[OptiFabric] " + optifine.name + " already has " + vanillaHandle.getName()
					+ vanillaHandle.getDesc() + ", so " + patchedHandle.getName() + " cannot take its place");
			return false;
		}

		// The registered descriptor is the arguments the class's own invokedynamic supplies, in the order the
		// lambda declares them. OptiFine recompiled some of these with two parameters swapped, so the method
		// sits under the game's name but with OptiFine's signature.
		boolean permuted = !patchedHandle.getDesc().equals(vanillaHandle.getDesc());

		// A recompiled parameter list is deliberately left alone. See the note on the class: OptiFine's copy of
		// the registration for method_17224 hands the lambda its arguments in an order that neither its own
		// descriptor nor the game's matches, so renaming it would put a member in place whose signature is
		// right but whose values are not - and c2me's @ModifyReturnValue would then read the wrong ones. A
		// member that exists but lies is worse than one that does not exist, so this fixer does not create it.
		if (permuted && parameterPermutation(vanillaHandle.getDesc(), patchedHandle.getDesc()) == null) {
			System.out.println("[OptiFabric] " + optifine.name + '.' + patchedHandle.getName()
					+ " is registered as " + patchedHandle.getDesc() + " where the game declares "
					+ vanillaHandle.getDesc() + ", which is not a reordering of the same parameters - left alone");
			return false;
		}

		//Everything that names the registration has to move together: the lambda itself, the handle that
		//registers it, and the invokedynamic that carries it.
		String oldDesc = lambda.desc;
		String oldName = lambda.name;
		String newDesc = vanillaHandle.getDesc();

		for (MethodNode method : optifine.methods) {
			for (AbstractInsnNode insn : method.instructions.toArray()) {
				if (insn instanceof MethodInsnNode call && call.owner.equals(optifine.name)
						&& call.name.equals(oldName) && call.desc.equals(oldDesc)) {
					call.name = vanillaHandle.getName();
				} else if (insn instanceof InvokeDynamicInsnNode dynamic) {
					for (int i = 0; i < dynamic.bsmArgs.length; i++) {
						if (!(dynamic.bsmArgs[i] instanceof Handle handle)) continue;
						if (!handle.getOwner().equals(optifine.name) || !handle.getName().equals(oldName)
								|| !handle.getDesc().equals(oldDesc)) continue;
						dynamic.bsmArgs[i] = new Handle(handle.getTag(), handle.getOwner(), vanillaHandle.getName(),
								handle.getDesc(), handle.isInterface());
					}
				}
			}
		}

		lambda.name = vanillaHandle.getName();
		lambda.access &= ~Opcodes.ACC_SYNTHETIC; //it is a method the game itself declares now

		//A lambda whose parameter list OptiFine recompiled keeps its own descriptor and its own body: the values
		//at the registration site stay where they are, so whatever the body reads does not change. The result is
		//internally consistent - it is the game's name on OptiFine's shape, not the game's shape.
		System.out.println("[OptiFabric] " + optifine.name + '.' + patchedHandle.getName() + " renamed to "
				+ vanillaHandle.getName() + (permuted ? " but keeps its recompiled parameter list ("
						+ oldDesc + "), because the registration site feeds it those values in that order" : "")
				+ " so the method a mod asks for exists");

		if (permuted) leftAsIs++;
		return true;
	}

	/**
	 * The reordering that turns {@code patched} into {@code vanilla}, as "for each patched parameter, which
	 * vanilla parameter is it": {@code result[i] == j} means the patched parameter {@code i} is the game's
	 * parameter {@code j}. Null when the two are not a permutation of the same parameter types, or when the
	 * reordering is not unique (two parameters of the same type).
	 *
	 * <p>Only used to decide whether a recompiled parameter list is a reordering at all, and to say so in the
	 * log; it is deliberately not used to put the order back, because doing that correctly needs the
	 * registration site's values as well and those do not always agree with the descriptor (see the note on
	 * the class).
	 */
	static int[] parameterPermutation(String vanillaDesc, String patchedDesc) {
		Type vanillaReturn = Type.getReturnType(vanillaDesc);
		Type patchedReturn = Type.getReturnType(patchedDesc);
		if (!vanillaReturn.equals(patchedReturn)) return null;

		Type[] vanillaParams = Type.getArgumentTypes(vanillaDesc);
		Type[] patchedParams = Type.getArgumentTypes(patchedDesc);
		if (vanillaParams.length != patchedParams.length) return null;

		//vanilla index -> which patched parameter carries that argument
		int[] vanillaToPatched = new int[vanillaParams.length];
		boolean[] used = new boolean[patchedParams.length];

		for (int i = 0; i < vanillaParams.length; i++) {
			int found = -1;
			int matches = 0;

			for (int j = 0; j < patchedParams.length; j++) {
				if (used[j] || !patchedParams[j].equals(vanillaParams[i])) continue;
				found = j;
				matches++;
			}

			if (matches != 1) return null; //not a parameter of the game's method, or ambiguous
			vanillaToPatched[i] = found;
			used[found] = true;
		}

		//invert, so that indexing is by the patched parameter the caller has in hand
		int[] patchedToVanilla = new int[patchedParams.length];

		for (int i = 0; i < vanillaToPatched.length; i++) {
			patchedToVanilla[vanillaToPatched[i]] = i;
		}

		return patchedToVanilla;
	}

	/** The invokedynamic instructions that a class's own bootstrap methods build, in order. */
	private static List<InvokeDynamicInsnNode> lambdaFactories(ClassNode owner) {
		List<InvokeDynamicInsnNode> found = new ArrayList<>();
		for (MethodNode method : owner.methods) found.addAll(lambdaFactories(method));
		return found;
	}

	/** The invokedynamic instructions that a method's own bootstrap methods build, in order. */
	private static List<InvokeDynamicInsnNode> lambdaFactories(MethodNode method) {
		List<InvokeDynamicInsnNode> found = new ArrayList<>();

		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (!(insn instanceof InvokeDynamicInsnNode dynamic)) continue;

			Handle bootstrap = dynamic.bsm;
			if (bootstrap != null && "java/lang/invoke/LambdaMetafactory".equals(bootstrap.getOwner())) found.add(dynamic);
		}

		return found;
	}

	/** The method the lambda implements - what the class's bootstrap method handle registers. */
	private static Handle handleOf(InvokeDynamicInsnNode dynamic) {
		return dynamic.bsmArgs.length > 1 && dynamic.bsmArgs[1] instanceof Handle handle ? handle : null;
	}

	private static MethodNode find(ClassNode owner, String name, String desc) {
		for (MethodNode method : owner.methods) {
			if (method.name.equals(name) && method.desc.equals(desc)) return method;
		}

		return null;
	}
}
