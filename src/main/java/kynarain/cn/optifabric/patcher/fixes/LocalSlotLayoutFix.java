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

		//Which layout each side was read from, for the log lines below: the exact one from its local variable table,
		//or the category-only one rebuilt from its instructions.
		String layouts = layoutNote(patched, game, frameSize, patchedLocals.size(), gameLocals.size());

		if (patchedLocals.isEmpty() || gameLocals.isEmpty()) {
			System.out.println("[OptiFabric] " + optifine.name + '.' + patched.name + " has no local layout to align"
					+ " (" + layouts + ')');
			return;
		}

		// Walk both layouts in slot order: while the two describe the same type, the two methods agree. A patched
		// local the game's layout does not have at this point is one OptiFine added (or moved) - a candidate.
		Map<Integer, Slot> candidates = new LinkedHashMap<>();
		Set<Integer> keep = new LinkedHashSet<>();
		Map<Integer, Slot[]> matches = new LinkedHashMap<>();
		int p = 0, g = 0;

		while (p < patchedLocals.size() && g < gameLocals.size()) {
			Slot pl = patchedLocals.get(p);
			Slot gl = gameLocals.get(g);

			if (sameType(pl, gl)) {
				keep.add(Integer.valueOf(pl.slot));
				matches.putIfAbsent(Integer.valueOf(pl.slot), new Slot[] {pl, gl});
				p++;
				g++;
				continue;
			}

			candidates.putIfAbsent(Integer.valueOf(pl.slot), pl);
			p++;
		}

		// Everything left over is past the game's own locals; there is nothing to align it against.
		while (p < patchedLocals.size()) {
			keep.add(Integer.valueOf(patchedLocals.get(p).slot));
			p++;
		}

		//The keep test below is positional: it only asks whether some entry of a slot matched anywhere in the walk,
		//never whether that match is the local alive where the extra is. A slot number is reused by locals that live
		//in disjoint scopes, and OptiFine's own local can share one with a game local, so the test can keep a genuine
		//extra - and it does: 1.21.1's class_757.method_3192 has 21 locals against the game's 18, its slot 12 also
		//holds OptiFine's own Lnet/minecraft/class_425; rlpg (instructions 460..464) between the catch-block locals,
		//and the match that keeps the slot is a positional pairing of a different local. That is why 1.21.11 (18
		//against 17) reports no move although it has an extra too, and 1.21.4 (22 against 19) misses one of its
		//three. Reading the two methods' instruction indices as comparable scopes does not repair this, which is why
		//`overlaps` is only used for the log line below: OptiFine recompiles these methods (785 instructions against
		//the game's 600 on 1.21.1), so index 346 in one is not the scope at index 346 in the other. The remap is by
		//slot number and so all-or-nothing anyway - game locals sharing the slot move with the extra - so a correct
		//fix has to remap per scope instead of per slot. Until then the dropped candidates are logged entry by
		//entry, so the next attempt starts from the data rather than from a silent no-op.
		Set<Integer> dropped = new LinkedHashSet<>(candidates.keySet());
		dropped.retainAll(keep);

		if (!dropped.isEmpty()) {
			for (Integer slot : dropped) {
				System.out.println("[OptiFabric] " + optifine.name + '.' + patched.name + patched.desc
						+ " keeps the candidate " + placed(candidates.get(slot)) + " in place: slot " + slot
						+ " also holds " + entriesIn(patchedLocals, slot.intValue()) + ", and "
						+ keepReason(matches.get(slot)));
			}
		}

		int found = candidates.size();
		candidates.keySet().removeAll(keep); //a slot that is a real local elsewhere must stay where it is

		if (candidates.isEmpty()) {
			System.out.println("[OptiFabric] " + optifine.name + '.' + patched.name + " needs no local slots moved: "
					+ (found == 0 ? "both layouts already line up"
							: "the keep test held every candidate slot (" + found + ") in place")
					+ " (" + layouts + ')');
			return;
		}

		if (candidates.size() > MAX_MOVES) {
			System.out.println("[OptiFabric] " + optifine.name + '.' + patched.name + " would need " + candidates.size()
					+ " local slots moved, which is more than this fixer understands (" + MAX_MOVES + "); leaving the"
					+ " method alone (" + layouts + ')');
			return;
		}

		//The game's locals never reach a slot this high, so the moved ones cannot collide with anything.
		int next = patched.maxLocals;
		Map<Integer, Integer> moves = new LinkedHashMap<>();

		for (Integer slot : candidates.keySet()) {
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
				+ " slot order mixin handlers capture them in (" + layouts + ')');
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
	 * from the instructions instead, which cannot tell more than the locals' categories apart (see
	 * {@link #sameType}). The instruction-based layout keeps one entry per slot, which is all the ordering below
	 * needs.
	 */
	private static List<Slot> bySlot(MethodNode method, int frameSize) {
		List<Slot> fromTable = new ArrayList<>();

		if (method.localVariables != null) {
			for (LocalVariableNode local : method.localVariables) {
				if (local.index >= frameSize) {
					//The scope is kept: the same slot can be reused by locals that never live at the same time.
					fromTable.add(new Slot(local.index, local.desc, method.instructions.indexOf(local.start),
							method.instructions.indexOf(local.end)));
				}
			}
		}

		if (!fromTable.isEmpty()) {
			//Stable, so entries that share a slot (a slot reused in another scope) keep the order they had.
			fromTable.sort((a, b) -> Integer.compare(a.slot, b.slot));
			return fromTable;
		}

		//An approximation of what Mixin sees: Locals.getLocalVariableAt looks up the local that is in range at the
		//injection point, while this keeps only the first entry of every slot. Only the slot order is read from it.
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

	/** Whether two locals hold the same kind of value, as far as each side's representation can tell. */
	private static boolean sameType(Slot a, Slot b) {
		//Both tables carry real descriptors, so they can be compared exactly. An instruction-based side cannot
		//promise more than the category - every reference reads as Ljava/lang/Object; and Z/B/C/S/I all read as I -
		//and holding it to an exact descriptor would break the walk at the first byte or boolean. That would turn
		//every local behind it into a candidate, trip the guard above and leave the layout unfixed, which is the
		//injection failure this fixer exists to prevent.
		if (a.fromTable && b.fromTable) return Objects.equals(a.desc, b.desc);

		return category(a.desc) == category(b.desc);
	}

	/** The kind of value a descriptor names, at the level {@link #typeFromOpcode} can tell apart. */
	private static char category(String desc) {
		switch (desc.charAt(0)) {
			case 'Z':
			case 'B':
			case 'C':
			case 'S':
			case 'I': return 'I';
			case 'F': return 'F';
			case 'J': return 'J';
			case 'D': return 'D';
			default: return 'L'; //Objects and arrays
		}
	}

	/**
	 * Whether two locals are alive at the same time, for the log lines only - this deliberately has no say in what
	 * moves. The two methods are two separate compilations (OptiFine recompiles what it patches), so an instruction
	 * index in one is not the same place as that index in the other and the answer is a hint, not a fact. A layout
	 * rebuilt from instructions has no scope at all, so it counts as covering the whole method.
	 */
	private static boolean overlaps(Slot a, Slot b) {
		if (!a.fromTable || !b.fromTable) return true;
		if (a.start < 0 || a.end < 0 || b.start < 0 || b.end < 0) return true;

		//[start, end): a local that ends exactly where the next one begins is not alive with it.
		return a.start < b.end && b.start < a.end;
	}

	/** How each side's layout was read, for the log lines. */
	private static String layoutNote(MethodNode patched, MethodNode game, int frameSize, int patchedCount, int gameCount) {
		return "OptiFine's copy: " + patchedCount + " locals read from " + representation(patched, frameSize)
				+ ", the game's: " + gameCount + " read from " + representation(game, frameSize);
	}

	/** Whether {@link #bySlot} read this method's layout from its local variable table or from its instructions. */
	private static String representation(MethodNode method, int frameSize) {
		if (method.localVariables != null) {
			for (LocalVariableNode local : method.localVariables) {
				if (local.index >= frameSize) return "table";
			}
		}

		return "code";
	}

	/** A local as the log lines name it: its descriptor, its slot and the instructions its scope covers. */
	private static String placed(Slot slot) {
		return slot.desc + " at slot " + slot.slot + scope(slot);
	}

	/** The instruction range a local's scope covers, as the log lines name it. */
	private static String scope(Slot slot) {
		return " (instructions " + slot.start + ".." + slot.end + ')';
	}

	/** Every local the method has in one slot, as the log lines name them. */
	private static String entriesIn(List<Slot> slots, int slot) {
		StringBuilder note = new StringBuilder();

		for (Slot entry : slots) {
			if (entry.slot != slot) continue;
			if (note.length() > 0) note.append(", ");
			note.append(entry.desc).append(scope(entry));
		}

		return note.toString();
	}

	/** Why the keep test left a slot where it is: the match that did it, and whether it is the same local. */
	private static String keepReason(Slot[] match) {
		if (match == null) return "it is past the end of the game's own local layout";

		return "the entry " + placed(match[0]) + " matched the game's " + placed(match[1])
				+ (overlaps(match[0], match[1]) ? ", which is alive at the same time"
						: ", which is not alive at the same time");
	}

	/**
	 * The descriptor of the value the method keeps in a slot: the local variable table's own entry for the slot
	 * when there is one, and the type of the first load or store of it otherwise. {@code IINC} and {@code RET} are
	 * never consulted - an increment always names an int operand and a return address is not a value at all, so
	 * both would read as {@code I} for a slot that may hold a wide local, and a wide candidate given one slot
	 * would let the next one start inside it.
	 */
	private static String typeOfSlot(MethodNode method, int slot) {
		if (method.localVariables != null) {
			for (LocalVariableNode local : method.localVariables) {
				if (local.index == slot) return local.desc;
			}
		}

		for (AbstractInsnNode insn : method.instructions.toArray()) {
			if (!(insn instanceof VarInsnNode var)) continue;
			if (var.var != slot) continue;
			if (!isLoadOrStore(var.getOpcode())) continue;

			return typeFromOpcode(var.getOpcode());
		}

		return "I";
	}

	private static boolean isLoadOrStore(int opcode) {
		return (opcode >= Opcodes.ILOAD && opcode <= Opcodes.ALOAD)
				|| (opcode >= Opcodes.ISTORE && opcode <= Opcodes.ASTORE);
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

	/** One local variable of a method: the slot it lives in, the type it holds and the scope it covers. */
	private static final class Slot {
		final int slot;
		final String desc;
		/** Whether the local variable table described this entry, so {@link #desc} is exact and the scope is known. */
		final boolean fromTable;
		/** The instruction indices the local's scope covers when {@link #fromTable}; -1 when it is not known. */
		final int start;
		final int end;

		Slot(int slot, String desc, int start, int end) {
			this.slot = slot;
			this.desc = desc;
			this.fromTable = true;
			this.start = start;
			this.end = end;
		}

		Slot(int slot, String desc) {
			this.slot = slot;
			this.desc = desc;
			this.fromTable = false;
			this.start = -1;
			this.end = -1;
		}
	}
}
