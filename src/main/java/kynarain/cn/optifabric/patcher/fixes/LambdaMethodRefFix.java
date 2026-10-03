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
 * code that really runs. It also matters for correctness here: OptiFine's lambda for method_17224 has the
 * same body as the game's but with two parameters swapped, which this fixer undoes (see below), while
 * RestoreVanillaMethodsFix would have left OptiFine's differently-shaped method in place.
 *
 * Because this runs before RestoreVanillaMethodsFix in the registration order, the names are occupied by
 * the time that fixer looks, so it leaves them alone instead of adding a second method under the same name
 * - which is what keeps the class's own bootstrap handles pointing at a method that exists.
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
 *   * the descriptor is either identical or a permutation of the vanilla one (see permute below).
 */
package kynarain.cn.optifabric.patcher.fixes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class LambdaMethodRefFix implements ClassFixer {
	/** how many of this run's renames also had to put a reordered parameter list back */
	private int reshaped;

	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		if (minecraft == null) return;

		int renamed = 0;
		reshaped = 0;
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
				+ (reshaped > 0 ? ", " + reshaped + " of them with a reordered parameter list put back" : "")
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
		// lambda declares them. OptiFine recompiled some of these with two parameters swapped, which puts the
		// method out of reach of a mod that asks for the game's signature - putting the order back is part of
		// restoring the member, and the body's parameter slots have to follow it.
		boolean permuted = !patchedHandle.getDesc().equals(vanillaHandle.getDesc());
		int[] permutation = null;
		if (permuted) {
			permutation = parameterPermutation(vanillaHandle.getDesc(), patchedHandle.getDesc());
			if (permutation == null) {
				System.out.println("[OptiFabric] " + optifine.name + '.' + patchedHandle.getName()
						+ " is registered as " + patchedHandle.getDesc() + " where the game declares "
						+ vanillaHandle.getDesc() + ", which is not a reordering of the same parameters - left alone");
				return false;
			}
		}

		List<AbstractInsnNode> reorder = null;
		InsnList registrationSite = null;
		if (permutation != null) {
			registrationSite = registrationSite(optifine, patchedSite);
			reorder = registrationSite == null ? null : capturedArguments(optifine, patchedSite, permutation);
			if (reorder == null) {
				System.out.println("[OptiFabric] " + optifine.name + '.' + patchedHandle.getName()
						+ " is registered with its parameters reordered, but the arguments at its registration site ("
						+ patchedSite.name + patchedSite.desc + ") are not a plain list of loads - left alone rather"
						+ " than risk feeding the method's body the wrong values");
				return false;
			}
		}

		//Everything that names the registration has to move together: the lambda itself, the handle that
		//registers it, and - when the parameter list is being put back in the game's order - the invokedynamic's
		//own descriptor and the arguments it pushes, or the call site no longer type checks.
		String oldDesc = lambda.desc;
		String oldName = lambda.name;
		String newDesc = vanillaHandle.getDesc();
		String newSiteDesc = null;

		if (permutation != null) {
			Type[] hostParams = Type.getArgumentTypes(oldDesc);
			Type[] reordered = new Type[hostParams.length];

			for (int i = 0; i < hostParams.length; i++) reordered[permutation[i]] = hostParams[i];

			//the return type is the lambda's, so the two descriptors differ in exactly the parameter list
			newSiteDesc = Type.getMethodDescriptor(Type.getReturnType(patchedSite.desc), reordered);
		}

		List<VarInsnNode> moved = permutation == null ? null : new ArrayList<>();
		for (MethodNode method : optifine.methods) {
			for (AbstractInsnNode insn : method.instructions.toArray()) {
				if (method == lambda && permutation != null && insn instanceof VarInsnNode variable) {
					moved.add(variable);
				} else if (insn instanceof MethodInsnNode call && call.owner.equals(optifine.name)
						&& call.name.equals(oldName) && call.desc.equals(oldDesc)) {
					call.name = vanillaHandle.getName();
					if (permutation != null) call.desc = newDesc;
				} else if (insn instanceof InvokeDynamicInsnNode dynamic) {
					for (int i = 0; i < dynamic.bsmArgs.length; i++) {
						if (!(dynamic.bsmArgs[i] instanceof Handle handle)) continue;
						if (!handle.getOwner().equals(optifine.name) || !handle.getName().equals(oldName)
								|| !handle.getDesc().equals(oldDesc)) continue;
						dynamic.bsmArgs[i] = new Handle(handle.getTag(), handle.getOwner(), vanillaHandle.getName(),
								permutation != null ? newDesc : handle.getDesc(), handle.isInterface());
					}

					if (dynamic == patchedSite && newSiteDesc != null) dynamic.desc = newSiteDesc;
				}
			}
		}

		if (permutation != null) {
			Type[] patchedParams = Type.getArgumentTypes(patchedHandle.getDesc());
			int[] slots = new int[patchedParams.length];
			int slot = 1; //slot 0 is this

			for (int i = 0; i < patchedParams.length; i++) {
				slots[i] = slot;
				slot += patchedParams[i].getSize();
			}

			//The new slots are worked out before anything is written: rewriting a slot in place would make a
			//later access match the value it has just taken over rather than the parameter it was compiled for.
			Map<Integer, Integer> newSlot = new HashMap<>();

			for (int i = 0; i < slots.length; i++) {
				newSlot.put(slots[i], slots[permutation[i]]);
			}

			for (VarInsnNode variable : moved) {
				Integer replacement = newSlot.get(variable.var);

				if (replacement != null) variable.var = replacement;
			}
		}

		lambda.name = vanillaHandle.getName();
		lambda.access &= ~Opcodes.ACC_SYNTHETIC; //it is a method the game itself declares now

		//The arguments the registration site pushes have to follow the parameter list into the game's order,
		//or the lambda is constructed with the values in the wrong slots. The list is a plain run of loads
		//directly before the invokedynamic, so its order is what carries the meaning: the originals are taken
		//out and the same instructions are put back in the order the game's parameter list asks for.
		if (reorder != null) {
			for (AbstractInsnNode insn : reorder) registrationSite.remove(insn);

			for (int i = reorder.size() - 1; i >= 0; i--) {
				registrationSite.insertBefore(patchedSite, reorder.get(i));
			}
		}

		if (permutation != null) {
			lambda.desc = vanillaHandle.getDesc();
			lambda.signature = null;
			reshaped++;
		}

		System.out.println("[OptiFabric] " + optifine.name + '.' + patchedHandle.getName() + " renamed to "
				+ vanillaHandle.getName() + (permuted ? " with its parameters put back in the game's order ("
						+ patchedHandle.getDesc() + " -> " + vanillaHandle.getDesc() + ")" : "")
				+ " so the method a mod asks for exists");

		return true;
	}

	/**
	 * The reordering that turns {@code patched} into {@code vanilla}, as "for each patched parameter, which
	 * vanilla parameter is it": {@code result[i] == j} means the patched parameter {@code i} is the game's
	 * parameter {@code j}, and therefore that the game's parameter {@code j} is where the patched slot of
	 * parameter {@code i} has to be read from. Null when the two are not a permutation of the same parameter
	 * types, or when the reordering is not unique (two parameters of the same type), because a wrong
	 * reordering would silently feed the body the wrong values.
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

	/**
	 * The instructions that push the arguments of a registration, in the order the game's parameter list
	 * wants them, or null when the registration site is not the plain "receiver load, then one load per
	 * argument" shape this can reorder safely.
	 *
	 * <p>The invokedynamic's own descriptor lists the captured arguments, so the last {@code n} instructions
	 * before it are the loads for them, preceded by the load of the receiver. Every one of those loads has to
	 * be a plain load with no label, line number or frame in between - a branch target inside the argument
	 * list would make reordering it change control flow.
	 */
	private static List<AbstractInsnNode> capturedArguments(ClassNode owner, InvokeDynamicInsnNode site,
			int[] permutation) {
		Type[] captured = Type.getArgumentTypes(site.desc);
		if (captured.length == 0) return null;

		MethodNode host = hostOf(owner, site);
		if (host == null) return null;

		List<AbstractInsnNode> run = new ArrayList<>();
		AbstractInsnNode insn = site.getPrevious();

		for (int i = 0; i <= captured.length && insn != null; i++, insn = insn.getPrevious()) {
			if (insn.getOpcode() < 0) return null; //a label, line number or frame inside the argument list
			run.add(0, insn);
		}

		if (run.size() != captured.length + 1) return null;

		//The receiver is the first value; the captured arguments are the rest, and each load must be able to
		//produce the value at that position.
		AbstractInsnNode receiver = run.get(0);
		if (!pushesType(receiver, owner.name)) return null;

		List<AbstractInsnNode> arguments = new ArrayList<>(run.subList(1, run.size()));
		for (int i = 0; i < captured.length; i++) {
			if (!pushesType(arguments.get(i), captured[i])) return null;
		}

		//The list has to end up in the game's parameter order: the value for the game's parameter j is the one
		//the patched list carries at the position that the permutation maps to j.
		AbstractInsnNode[] ordered = new AbstractInsnNode[arguments.size() + 1];
		ordered[0] = receiver;

		for (int i = 0; i < arguments.size(); i++) {
			ordered[permutation[i] + 1] = arguments.get(i);
		}

		return new ArrayList<>(java.util.Arrays.asList(ordered));
	}

	/** The instruction list a registration lives in, or null when the site is not in this class. */
	private static InsnList registrationSite(ClassNode owner, InvokeDynamicInsnNode site) {
		MethodNode host = hostOf(owner, site);
		return host == null ? null : host.instructions;
	}

	private static MethodNode hostOf(ClassNode owner, InvokeDynamicInsnNode site) {
		for (MethodNode method : owner.methods) {
			for (AbstractInsnNode insn : method.instructions) {
				if (insn == site) return method;
			}
		}

		return null;
	}

	/** Whether an instruction is a plain load of the given type - a class name, or a {@link Type}. */
	private static boolean pushesType(AbstractInsnNode insn, Object expected) {
		String type = expected instanceof Type t ? t.getDescriptor() : "L" + expected + ";";

		switch (insn.getOpcode()) {
			case Opcodes.ALOAD:
				return type.startsWith("L") || type.startsWith("[");
			case Opcodes.ILOAD:
				return type.equals("I") || type.equals("Z") || type.equals("B") || type.equals("C") || type.equals("S");
			case Opcodes.LLOAD:
				return type.equals("J");
			case Opcodes.FLOAD:
				return type.equals("F");
			case Opcodes.DLOAD:
				return type.equals("D");
			default:
				return false;
		}
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
