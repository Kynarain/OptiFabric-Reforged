/*
 * New in the 1.21.x series of this port (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 *
 * Puts OptiFine's own local variables at the END of the slot range instead of in the middle of it, so that
 * positional local capture still finds the game's locals where the mixin expects them.
 *
 * Mixin hands a method's locals to an @Inject handler BY SLOT ORDER: CallbackInjector builds the descriptor it
 * expects from `getFirstNonArgLocalIndex(args)` upwards, taking the first `extraArgs` non-null locals in slot
 * order, and fails the class with
 *
 *   InjectionError: LVT in net/minecraft/class_757::method_3192(Lnet/minecraft/class_9779;Z)V has incompatible
 *   changes at opcode 601 in callback architectury.mixins.json:client.MixinGameRenderer
 *     Expected: [Z, I, I, Lnet/minecraft/class_1041;, Lorg/joml/Matrix4f;, Lorg/joml/Matrix4fStack;,
 *                Lnet/minecraft/class_332;]
 *        Found: [Z, I, I, Lnet/minecraft/class_1041;, F, Lorg/joml/Matrix4f;, Lorg/joml/Matrix4fStack;]
 *
 * whenever the order does not line up. OptiFine recompiles the methods it patches, and its own javac puts the
 * locals it added where they appear in the source. GameRenderer.render is the clearest case: OptiFine adds two
 * floats (guiFarPlane, guiOffsetZ) in the middle of the method, which pushes the game's Matrix4f, Matrix4fStack
 * and GuiGraphics one or two slots up, so the first locals Mixin sees are
 *
 *   game:     [Z, I, I, window, Matrix4f, Matrix4fStack, guigraphics, ...]        <- what handlers declare
 *   OptiFine: [Z, I, I, window, F guiFarPlane, Matrix4f, Matrix4fStack, F guiOffsetZ, guigraphics, ...]
 *
 * This fixer walks the game's local layout and OptiFine's side by side and moves every local that has no
 * counterpart in the game's order (the two floats above) to a fresh slot past the end of the method's local range.
 * Slot numbers themselves are irrelevant to Mixin - only the order is - so the game's locals keep their relative
 * order and the handlers find what they were compiled against. A slot that also holds a game-equivalent local
 * somewhere else in the method is never moved, because that would take a real local with it.
 *
 * Nothing of OptiFine's code is removed: the moved locals are still the same values written by the same
 * instructions, only at a different slot. The local variable table is rewritten to match - that table is what
 * Mixin reads the locals from, and neither of the pipeline's writers regenerates it - and the stack map frames are
 * recomputed by the frame computing writer every class a fixer changed goes through (see OptifineInjector).
 */
package kynarain.cn.optifabric.patcher.fixes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class LocalSlotLayoutFix implements ClassFixer {
	/** How many locals may be moved before the fixer decides it does not understand the method and gives up. */
	private static final int MAX_MOVES = 8;

	private final String[] methods;
	private final String desc;

	/**
	 * @param desc the method's descriptor in the runtime namespace, or {@code null} to match the name alone. The
	 *             descriptor changes between Minecraft releases, and a fixer that hardcodes one silently stops
	 *             firing on the others - the method is then located by the descriptor OptiFine's class uses.
	 */
	public LocalSlotLayoutFix(String desc, String... methods) {
		this.desc = desc;
		this.methods = methods;
	}

	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		for (String name : methods) {
			MethodNode patched = find(optifine, name, desc);

			if (patched == null) continue;

			MethodNode game = find(minecraft, name, patched.desc);

			if (game == null) continue;

			move(optifine, patched, game);
		}
	}

	private static void move(ClassNode optifine, MethodNode patched, MethodNode game) {
		int frameSize = firstLocalIndex(patched);

		List<Slot> patchedLocals = bySlot(patched, frameSize);
		List<Slot> gameLocals = bySlot(game, frameSize);

		if (patchedLocals.isEmpty() || gameLocals.isEmpty()) {
			System.out.println("[OptiFabric] " + optifine.name + '.' + patched.name + " has no local layout to align"
					+ " (OptiFine's copy: " + patchedLocals.size() + " locals, the game's: " + gameLocals.size() + ')');
			return;
		}

		// Walk both layouts in slot order: while the two describe the same type, the two methods agree. A patched
		// local the game's layout does not have at this point is one OptiFine added (or moved) - a candidate.
		Set<Integer> candidates = new LinkedHashSet<>();
		Set<Integer> keep = new LinkedHashSet<>();
		int p = 0, g = 0;

		while (p < patchedLocals.size() && g < gameLocals.size()) {
			Slot pl = patchedLocals.get(p);
			Slot gl = gameLocals.get(g);

			if (Objects.equals(pl.desc, gl.desc)) {
				keep.add(Integer.valueOf(pl.slot));
				p++;
				g++;
				continue;
			}

			candidates.add(Integer.valueOf(pl.slot));
			p++;
		}

		// Everything left over is past the game's own locals; there is nothing to align it against.
		while (p < patchedLocals.size()) {
			keep.add(Integer.valueOf(patchedLocals.get(p).slot));
			p++;
		}

		candidates.removeAll(keep); //a slot that is a real local elsewhere must stay where it is

		if (candidates.isEmpty()) return;

		if (candidates.size() > MAX_MOVES) {
			System.out.println("[OptiFabric] " + optifine.name + '.' + patched.name + " would need " + candidates.size()
					+ " local slots moved, which is more than this fixer understands (" + MAX_MOVES + "); leaving the"
					+ " method alone");
			return;
		}

		//The game's locals never reach a slot this high, so the moved ones cannot collide with anything.
		int next = patched.maxLocals;
		Map<Integer, Integer> moves = new LinkedHashMap<>();

		for (Integer slot : candidates) {
			Type type = Type.getType(typeOfSlot(patched, slot.intValue()));
			moves.put(slot, Integer.valueOf(next));
			next += type.getSize();
		}

		remap(patched, moves);
		patched.maxLocals = Math.max(patched.maxLocals, next);

		StringBuilder note = new StringBuilder();

		for (Map.Entry<Integer, Integer> entry : moves.entrySet()) {
			if (note.length() > 0) note.append(", ");
			note.append(entry.getKey()).append("->").append(entry.getValue());
		}

		System.out.println("[OptiFabric] Moved OptiFine's own locals of " + optifine.name + '.' + patched.name
				+ patched.desc + " past the end of the local range (slots " + note + "), so the game's locals keep the"
				+ " slot order mixin handlers capture them in");
	}

	/** Rewrites every reference to the moved slots, and the local variable table entries that name them. */
	private static void remap(MethodNode method, Map<Integer, Integer> moves) {
		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (insn instanceof VarInsnNode var) {
				Integer to = moves.get(Integer.valueOf(var.var));
				if (to != null) var.var = to.intValue();
			} else if (insn instanceof IincInsnNode inc) {
				Integer to = moves.get(Integer.valueOf(inc.var));
				if (to != null) inc.var = to.intValue();
			}
		}

		if (method.localVariables != null) {
			for (LocalVariableNode local : method.localVariables) {
				Integer to = moves.get(Integer.valueOf(local.index));
				if (to != null) local.index = to.intValue();
			}
		}

		//Stack map frames are deliberately not touched: they describe the state of every local slot, so they would
		//have to be rebuilt - and the pipeline already does exactly that. A class any fixer changed is written
		//through OptifineInjector's FrameComputingWriter (COMPUTE_FRAMES | COMPUTE_MAXS), which ignores the frames
		//in the node and computes new ones from the instructions. DelegatingConstructorFix shifts slots the same way.
	}

	/**
	 * The method's locals in slot order, without the receiver and the arguments.
	 *
	 * <p>Read from the local variable table when the class has one, which it normally does: OptiFine's compiled
	 * classes carry one, and so does the game's own jar. If either side does not - Fabric's runtime remapping of
	 * the game's classes is the case to watch, and debug information is not guaranteed - the layout is rebuilt
	 * from the instructions instead, so both sides are read the same way and the walk below still lines up. The
	 * instruction-based layout keeps one entry per slot, which is all the ordering below needs.
	 */
	private static List<Slot> bySlot(MethodNode method, int frameSize) {
		List<Slot> fromTable = new ArrayList<>();

		if (method.localVariables != null) {
			for (LocalVariableNode local : method.localVariables) {
				if (local.index >= frameSize) fromTable.add(new Slot(local.index, local.desc));
			}
		}

		if (!fromTable.isEmpty()) {
			//Stable, so entries that share a slot (a slot reused in another scope) keep the order they had.
			fromTable.sort((a, b) -> Integer.compare(a.slot, b.slot));
			return fromTable;
		}

		List<Slot> fromCode = new ArrayList<>();
		Set<Integer> seen = new LinkedHashSet<>();

		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (!(insn instanceof VarInsnNode var)) continue;
			if (var.var < frameSize) continue;
			if (!seen.add(Integer.valueOf(var.var))) continue;

			fromCode.add(new Slot(var.var, typeFromOpcode(var.getOpcode())));
		}

		fromCode.sort((a, b) -> Integer.compare(a.slot, b.slot));
		return fromCode;
	}

	/** The descriptor of the value the method keeps in a slot, from the type of the first instruction using it. */
	private static String typeOfSlot(MethodNode method, int slot) {
		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (insn instanceof VarInsnNode var && var.var == slot) return typeFromOpcode(var.getOpcode());
		}

		return "I";
	}

	private static String typeFromOpcode(int opcode) {
		switch (opcode) {
			case Opcodes.LLOAD:
			case Opcodes.LSTORE: return "J";
			case Opcodes.DLOAD:
			case Opcodes.DSTORE: return "D";
			case Opcodes.FLOAD:
			case Opcodes.FSTORE: return "F";
			case Opcodes.ALOAD:
			case Opcodes.ASTORE: return "Ljava/lang/Object;";
			default: return "I";
		}
	}

	/** The first slot that is not the receiver or one of the method's arguments. */
	private static int firstLocalIndex(MethodNode method) {
		int index = (method.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1;

		for (Type argument : Type.getArgumentTypes(method.desc)) {
			index += argument.getSize();
		}

		return index;
	}

	private static MethodNode find(ClassNode owner, String name, String desc) {
		if (owner == null) return null;

		for (MethodNode method : owner.methods) {
			if (!method.name.equals(name)) continue;
			if (desc != null && !method.desc.equals(desc)) continue;

			return method;
		}

		return null;
	}

	/** One local variable of a method: the slot it lives in and the type it holds. */
	private static final class Slot {
		final int slot;
		final String desc;

		Slot(int slot, String desc) {
			this.slot = slot;
			this.desc = desc;
		}
	}
}
