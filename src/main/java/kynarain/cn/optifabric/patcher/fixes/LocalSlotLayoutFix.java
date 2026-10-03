/*
 * New in the 1.21.x series of this port (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 *
 * Puts the game's own local variables back into the slots the game declares them in, so that a mixin handler
 * compiled against the game's layout finds them where it expects.
 *
 * Mixin reads a method's locals in two ways and OptiFine's recompilation breaks both of them:
 *
 *   1. BY SLOT ORDER. CallbackInjector builds the descriptor it expects from `getFirstNonArgLocalIndex(args)`
 *      upwards, taking the first `extraArgs` non-null locals in slot order, and fails the class with
 *
 *        InjectionError: LVT in net/minecraft/class_757::method_3192(Lnet/minecraft/class_9779;Z)V has incompatible
 *        changes at opcode 601 in callback architectury.mixins.json:client.MixinGameRenderer
 *          Expected: [Z, I, I, Lnet/minecraft/class_1041;, Lorg/joml/Matrix4f;, Lorg/joml/Matrix4fStack;,
 *                     Lnet/minecraft/class_332;]
 *             Found: [Z, I, I, Lnet/minecraft/class_1041;, F, Lorg/joml/Matrix4f;, Lorg/joml/Matrix4fStack;]
 *
 *      whenever the order does not line up. OptiFine recompiles the methods it patches, and its javac puts the
 *      locals it added where they appear in the source - GameRenderer.render is the clearest case, where two
 *      floats (guiFarPlane, guiOffsetZ) land in the middle and push the game's Matrix4f, Matrix4fStack and
 *      GuiGraphics one or two slots up.
 *
 *   2. BY ABSOLUTE SLOT. MixinExtras' @Local(index = N) resolves against Locals.getLocalsAt(...), which returns
 *      the locals live at the injection point in an array indexed by slot; the explicit branch of
 *      LocalVariableDiscriminator.findLocal then returns N only when `locals[N]` is non-null and of the type the
 *      handler declares. Porting Lib's client.LevelRendererMixin captures
 *      `@Local(index = 24) Lnet/minecraft/class_4587;` at the DebugRenderer.render call in
 *      class_761.method_22710, and OptiFine's copy of that method keeps the game's PoseStack in slot 27 with a
 *      boolean in 24, so the sugar cannot build the callback at all:
 *
 *        SugarApplicationException: Failed to validate sugar @Local(index = 24) class_4587 on method
 *        port_lib$renderEntityOutline ... in target method net/minecraft/class_761::method_22710(...)
 *        Caused by: SugarApplicationException: Unable to find matching local!
 *
 *      and the injection then reports `expected 1 invocation(s) but 0 succeeded`, which fails the class.
 *
 * WHAT WAS WRONG WITH THE OLD PLAN
 *
 * The previous version of this fixer decided per SLOT NUMBER whether a slot was one OptiFine added, and moved
 * the slots it decided were extras to the end of the local range. Two things were wrong with that. It never moved
 * a game local that OptiFine had pushed up back down - which is exactly what @Local(index = 24) needs, and is why
 * class_761.method_22710 was left alone - and its decision was made by walking the two layouts side by side by
 * position, which cannot tell a slot that OptiFine added from a slot the game's own method reuses somewhere else.
 *
 * THE PAIRING IS PER SCOPE; THE SLOT IS MOVED AS A WHOLE
 *
 * A slot number is not a value: it is a place a value is kept while its scope lasts, and a compiler hands the
 * same number to unrelated locals whose scopes do not overlap. So which slot a value belongs in has to be decided
 * per local-variable-table entry - and that is what the pairing below does. Moving is still done a whole slot at
 * a time, and deliberately so:
 *
 *   1. LINE THE TWO TABLES UP. Instruction indices are not comparable between the two methods (OptiFine's
 *      compilation inserts its own code, so 1636 instructions become 2673 on 1.21.1) and neither are local names
 *      (the game's jar carries Mojang's obfuscated lv/lv2/..., OptiFine's carries the names of the tree it was
 *      built from). What IS comparable is the sequence of descriptors in slot order: javac hands slots out while
 *      it walks the source, so the game's sequence is a subsequence of OptiFine's, and the entries a longest
 *      common subsequence cannot pair up are the locals OptiFine added. Each entry is then paired with the single
 *      game entry it is the counterpart of, which is what "by scope" means here.
 *
 *   2. DECIDE PER SLOT FROM THOSE PAIRS. A slot whose entries are the game's own goes back to the slot the game
 *      declares them in - that is what moves class_761.method_22710's PoseStack from 27 back to 24, and what
 *      restores the ascending live-slot order the by-order capture reads. A slot none of whose entries could be
 *      paired is OptiFine's own and goes to a fresh slot past the end of both methods' local ranges, so it can
 *      never be one of the first locals a handler sees.
 *
 *   3. WHY A SLOT IS NOT SPLIT. Two entries of one slot whose table scopes do not overlap look as if they could be
 *      sent to two different slots - and they cannot. OptiFine's class_761.method_22710 has slot 36 holding
 *      `class_4597 multibuffersource` over instructions 1079..1098 and again over 1130..1167, and the GOTO at
 *      instruction 1097 jumps straight to 1130, past the store at 1129 that starts the second scope. The second
 *      entry is therefore read at 1165 on a path that never wrote it, and only works because the first entry's
 *      value is still in the same slot. Sending the two entries to different slots leaves that path reading an
 *      uninitialised local - the JVM's own verifier happened to accept the class, ASM's data flow verifier
 *      rejects it at instruction 1165 with "Expected an object reference, but found .". Nothing in the local
 *      variable table says where control flow can come from, so a slot is moved as a whole and no entry is ever
 *      moved out from under another that shares it.
 *
 *   4. NEVER MOVE TWO LIVE VALUES INTO ONE SLOT. Moving whole slots keeps every reference to a slot consistent by
 *      construction, but two different slots can still be sent to the same number. That is only allowed when the
 *      entries of those slots are never alive at the same time; otherwise the later one takes a fresh slot. A
 *      slot whose reads or writes no table entry describes - javac does not describe every temporary it
 *      allocates, and OptiFine's class_761.method_22710 writes slot 34 at fifteen offsets its two entries for
 *      that slot do not cover - has no scope at all, so it counts as alive over the whole method and gets a slot
 *      to itself. The receiver and the arguments, everything below the first local slot, never move.
 *
 * Nothing of OptiFine's code is removed and no instruction is added: the moved values are still written by the
 * same instructions, only at a different slot. The local variable table is rewritten to match - that table is
 * what Mixin reads the locals from, and neither of the pipeline's writers regenerates it - and the stack map
 * frames are recomputed by the frame computing writer every class a fixer changed goes through (see
 * OptifineInjector).
 *
 * WHERE THERE IS NO TABLE
 *
 * Fabric's runtime remapping of the game's classes and debug information are not guaranteed, so a method can
 * arrive with no local variable table at all, on either side. There is then no scope to work with and nothing to
 * pair entries against, and the old slot-order plan is the most that can be said: it is kept below as
 * {@link #moveBySlot} and only runs on that path.
 */
package kynarain.cn.optifabric.patcher.fixes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class LocalSlotLayoutFix implements ClassFixer {
	/** How many locals the no-table fallback may move before it decides it does not understand the method. */
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

			realign(optifine, patched, game);
		}
	}

	/**
	 * Works out where every slot of the patched method should live - the game's own slots back where the game
	 * declares them, OptiFine's own past the end of both ranges - and rewrites every reference together.
	 *
	 * <p>Nothing is modified until the whole plan has been built, so every path below that gives up leaves the
	 * method exactly as OptiFine compiled it.
	 */
	private static void realign(ClassNode optifine, MethodNode patched, MethodNode game) {
		int frameSize = firstLocalIndex(patched);

		List<Slot> patchedSlots = bySlot(patched, frameSize);
		List<Slot> gameSlots = bySlot(game, frameSize);

		String layouts = layoutNote(patched, game, frameSize, patchedSlots.size(), gameSlots.size());

		if (patchedSlots.isEmpty() || gameSlots.isEmpty()) {
			System.out.println("[OptiFabric] " + optifine.name + '.' + patched.name + " has no local layout to align"
					+ " (" + layouts + ')');
			return;
		}

		if (!patchedSlots.get(0).fromTable || !gameSlots.get(0).fromTable) {
			//Neither side's scope is known, so there is nothing to pair entries against: fall back to what can be
			//said about the slot order alone.
			moveBySlot(optifine, patched, game, patchedSlots, gameSlots, layouts);
			return;
		}

		//1. which of the game's entries each of OptiFine's is the counterpart of. The pairing is what everything
		//below is decided from: it is the part of this that works per scope.
		int[] counterpart = counterpart(gameSlots, patchedSlots);
		int paired = 0;

		for (int index = 0; index < patchedSlots.size(); index++) {
			Slot entry = patchedSlots.get(index);

			if (counterpart[index] >= 0) {
				entry.wanted = gameSlots.get(counterpart[index]).slot;
				paired++;
			}
		}

		Map<Integer, List<Slot>> entriesBySlot = bySlot(patchedSlots);
		Map<Integer, Integer> widths = widths(patched, patchedSlots, frameSize);
		Set<Integer> used = new TreeSet<>(widths.keySet());

		//2. and 4. give every slot a target, refusing one that is already alive there.
		Map<Integer, Integer> targets = new LinkedHashMap<>();
		Map<Integer, List<Slot>> occupied = new HashMap<>();
		Set<Integer> wholeMethod = new LinkedHashSet<>();
		int base = Math.max(patched.maxLocals, game.maxLocals);
		int nextFree = base;

		for (Integer slot : used) {
			List<Slot> entries = entriesBySlot.get(slot);
			boolean blurs = entries == null || !scopedEverywhere(patched, patchedSlots, slot.intValue(), frameSize);
			int width = widths.get(slot).intValue();
			int wanted = wanted(slot.intValue(), entries);
			int target = wanted >= frameSize && fits(occupied, wholeMethod, wanted, entries, blurs, width)
					? wanted
					: nextFree;

			if (target == nextFree) nextFree += width;

			targets.put(slot, Integer.valueOf(target));
			claim(occupied, wholeMethod, target, entries, blurs, width);
		}

		//3. only whole slots move, so this is a rewrite of every reference to a slot, not of the ones a scope
		//happens to cover.
		Map<AbstractInsnNode, Integer> moves = new LinkedHashMap<>();

		for (int index = 0; index < patched.instructions.size(); index++) {
			AbstractInsnNode insn = patched.instructions.get(index);
			int slot = slotOf(insn);

			if (slot < 0 || slot < frameSize) continue;

			Integer target = targets.get(Integer.valueOf(slot));

			if (target != null && target.intValue() != slot) moves.put(insn, target);
		}

		if (moves.isEmpty()) {
			System.out.println("[OptiFabric] " + optifine.name + '.' + patched.name + " needs no local slots moved: the"
					+ " game's own locals are already in the slots the game declares them in (paired " + paired + " of "
					+ patchedSlots.size() + " of OptiFine's entries with " + gameSlots.size() + " of the game's; "
					+ layouts + ')');
			return;
		}

		for (Map.Entry<AbstractInsnNode, Integer> move : moves.entrySet()) {
			if (move.getKey() instanceof VarInsnNode var) {
				var.var = move.getValue().intValue();
			} else {
				((IincInsnNode) move.getKey()).var = move.getValue().intValue();
			}
		}

		for (Slot entry : patchedSlots) {
			if (entry.node != null) entry.node.index = targets.get(Integer.valueOf(entry.node.index)).intValue();
		}

		patched.maxLocals = Math.max(patched.maxLocals, nextFree);
		logMoves(optifine, patched, gameSlots, patchedSlots, targets, entriesBySlot, paired, base, layouts);
	}

	/**
	 * The game's local entries paired with OptiFine's, as a longest common subsequence of the two descriptor
	 * sequences in slot order: {@code result[patchedIndex]} is the game entry it is the counterpart of, or -1 when
	 * OptiFine has a local the game does not.
	 *
	 * <p>Descriptors are the only thing the two tables have in common. The pairing assumes OptiFine's javac
	 * allocated slots in the same order as the game's for everything the two sources share, which is what a
	 * recompile of the same source does. Where that does not hold the pairing is simply a guess about which game
	 * local an entry is, and the checks in {@link #realign} keep a wrong guess from making the method invalid - it
	 * only means the slot is not put back where the game declares it.
	 *
	 * <p>A longest common subsequence is deliberately not the same as "pair equal descriptors up greedily": the
	 * game's table has runs of one descriptor (Z, I, F, class_2338...) where only the subsequence that keeps the
	 * two orders consistent is meaningful.
	 */
	private static int[] counterpart(List<Slot> game, List<Slot> patched) {
		int gameCount = game.size(), patchedCount = patched.size();
		int[][] length = new int[gameCount + 1][patchedCount + 1];

		for (int i = gameCount - 1; i >= 0; i--) {
			for (int j = patchedCount - 1; j >= 0; j--) {
				length[i][j] = sameType(game.get(i), patched.get(j))
						? length[i + 1][j + 1] + 1
						: Math.max(length[i + 1][j], length[i][j + 1]);
			}
		}

		int[] result = new int[patchedCount];
		Arrays.fill(result, -1);

		int i = 0, j = 0;

		while (i < gameCount && j < patchedCount) {
			if (sameType(game.get(i), patched.get(j)) && length[i][j] == length[i + 1][j + 1] + 1) {
				result[j] = i;
				i++;
				j++;
			} else if (length[i + 1][j] >= length[i][j + 1]) {
				i++; //a game local OptiFine's copy has no counterpart for
			} else {
				j++; //a local OptiFine added
			}
		}

		return result;
	}

	/** The entries of each slot, so a slot can be decided from all of them at once. */
	private static Map<Integer, List<Slot>> bySlot(List<Slot> entries) {
		Map<Integer, List<Slot>> result = new LinkedHashMap<>();

		for (Slot entry : entries) {
			result.computeIfAbsent(Integer.valueOf(entry.slot), slot -> new ArrayList<>()).add(entry);
		}

		return result;
	}

		/**
	 * Whether every read or write of a slot belongs to a table entry.
	 *
	 * <p>javac does not put a debug entry in the table for every temporary it allocates - OptiFine's
	 * class_761.method_22710 writes slot 34 at fifteen offsets that its two table entries for that slot do not
	 * cover - and nothing is known about the value in such a slot: not its type, not its scope, not even whether
	 * the entries that do describe the slot are that value. Such a slot is moved like any other (every reference
	 * to it moves with it, so the code stays consistent) but it counts as alive over the whole method, so no
	 * other slot may be given the same number.
	 */
	private static boolean scopedEverywhere(MethodNode patched, List<Slot> entries, int slot, int frameSize) {
		for (int index = 0; index < patched.instructions.size(); index++) {
			AbstractInsnNode insn = patched.instructions.get(index);

			if (slotOf(insn) != slot) continue;
			if (ownerAt(entries, slot, index) == null) return false;
		}

		return true;
	}

	/**
	 * How many slots each slot needs, from the table and from the instructions: two for a long or a double, one
	 * otherwise. Read from the instructions as well because a slot can carry a wide value no table entry
	 * describes, and a wide value given one slot would let the next value start inside it.
	 */
	private static Map<Integer, Integer> widths(MethodNode patched, List<Slot> entries, int frameSize) {
		Map<Integer, Integer> widths = new LinkedHashMap<>();

		for (Slot entry : entries) {
			widths.merge(Integer.valueOf(entry.slot), Integer.valueOf(size(entry)), Math::max);
		}

		for (AbstractInsnNode insn : patched.instructions.toArray()) {
			int slot = slotOf(insn);

			if (slot < frameSize) continue;

			widths.merge(Integer.valueOf(slot), Integer.valueOf(isWide(insn) ? 2 : 1), Math::max);
		}

		return widths;
	}

	/** Whether the instruction loads or stores a long or a double, which takes two slots. */
	private static boolean isWide(AbstractInsnNode insn) {
		if (!(insn instanceof VarInsnNode var)) return false;

		int opcode = var.getOpcode();

		return opcode == Opcodes.LLOAD || opcode == Opcodes.DLOAD || opcode == Opcodes.LSTORE || opcode == Opcodes.DSTORE;
	}

	/** The slot the game declares the entries of this slot in, or -1 when none of them could be paired. */
	private static int wanted(int slot, List<Slot> entries) {
		if (entries == null) return -1;

		Map<Integer, Integer> votes = new LinkedHashMap<>();
		int best = -1, bestCount = 0;

		for (Slot entry : entries) {
			if (entry.wanted < 0) continue;

			int count = votes.merge(Integer.valueOf(entry.wanted), Integer.valueOf(1), (a, b) ->
					Integer.valueOf(a.intValue() + b.intValue())).intValue();

			if (count > bestCount || count == bestCount && entry.wanted < best) {
				best = entry.wanted;
				bestCount = count;
			}
		}

		return best;
	}

	/** Whether a slot may be given to these entries: nothing alive there already, and room for both halves. */
	private static boolean fits(Map<Integer, List<Slot>> occupied, Set<Integer> wholeMethod, int slot,
			List<Slot> entries, boolean blurs, int width) {
		for (int half = slot; half < slot + width; half++) {
			if (wholeMethod.contains(Integer.valueOf(half))) return false;

			List<Slot> taken = occupied.get(Integer.valueOf(half));

			if (taken == null) continue;
			if (blurs || entries == null) return false;

			for (Slot other : taken) {
				for (Slot entry : entries) {
					if (overlaps(entry, other)) return false;
				}
			}
		}

		return true;
	}

	/** Records that a slot is taken, so nothing alive at the same time can be given it too. */
	private static void claim(Map<Integer, List<Slot>> occupied, Set<Integer> wholeMethod, int slot,
			List<Slot> entries, boolean blurs, int width) {
		for (int half = slot; half < slot + width; half++) {
			if (blurs || entries == null) {
				wholeMethod.add(Integer.valueOf(half));
			} else {
				occupied.computeIfAbsent(Integer.valueOf(half), taken -> new ArrayList<>()).addAll(entries);
			}
		}
	}

	/** The slot a load, store or increment names, or -1 when the instruction is not one of those. */
	private static int slotOf(AbstractInsnNode insn) {
		if (insn instanceof VarInsnNode var) return var.var;
		if (insn instanceof IincInsnNode inc) return inc.var;

		return -1;
	}

	/**
	 * The entry a read or write of a slot belongs to.
	 *
	 * <p>Normally that is the entry whose scope covers the instruction. The one case that is not covered is the
	 * store that puts the value there in the first place: javac puts a local's scope in the table as starting at
	 * the label <em>after</em> the store, so the store itself sits one instruction before {@code start} - which is
	 * why this also claims the instruction immediately in front of an entry's scope. Without that, every local
	 * would look like a read or write nothing describes.
	 *
	 * <p>Returns null when no entry at that slot can be said to own the instruction - including when two entries
	 * cover it, which a well-formed table does not do.
	 */
	private static Slot ownerAt(List<Slot> entries, int slot, int index) {
		Slot found = null;

		for (Slot entry : entries) {
			if (entry.slot != slot) continue;
			if (index < entry.start || index >= entry.end) continue;
			if (found != null) return null; //two entries claim the same instruction: nothing can be said about it

			found = entry;
		}

		if (found != null) return found;

		for (Slot entry : entries) {
			if (entry.slot != slot) continue;
			if (entry.start != index + 1) continue;
			if (found != null) return null;

			found = entry;
		}

		return found;
	}

	private static int size(Slot entry) {
		return Type.getType(entry.desc).getSize();
	}

	/** What the plan did, slot by slot - this is the data the next attempt starts from. */
	private static void logMoves(ClassNode optifine, MethodNode patched, List<Slot> gameSlots, List<Slot> patchedSlots,
			Map<Integer, Integer> targets, Map<Integer, List<Slot>> entriesBySlot, int paired, int base,
			String layouts) {
		StringBuilder note = new StringBuilder();

		for (Map.Entry<Integer, Integer> move : targets.entrySet()) {
			if (move.getKey().intValue() == move.getValue().intValue()) continue;
			if (note.length() > 0) note.append(", ");
			note.append(move.getKey()).append("->").append(move.getValue());
		}

		System.out.println("[OptiFabric] Realigned the locals of " + optifine.name + '.' + patched.name + patched.desc
				+ " onto the slots the game declares them in: " + paired + " of OptiFine's " + patchedSlots.size()
				+ " table entries are the game's own (of " + gameSlots.size() + "), the other "
				+ (patchedSlots.size() - paired) + " are OptiFine's own or could not be paired (slots " + note + ") ("
				+ layouts + ')');

		for (Map.Entry<Integer, Integer> move : targets.entrySet()) {
			int slot = move.getKey().intValue(), target = move.getValue().intValue();

			if (slot == target) continue;

			List<Slot> entries = entriesBySlot.get(move.getKey());
			int matched = 0;

			if (entries != null) {
				for (Slot entry : entries) {
					if (entry.wanted >= 0) matched++;
				}
			}

			System.out.println("[OptiFabric]   slot " + slot + " ("
					+ (entries == null ? "no table entry" : entries.size() + " table entr"
							+ (entries.size() == 1 ? "y" : "ies") + ", " + matched + " of them the game's")
					+ ") moved to slot " + target
					+ (target >= base ? ", past the end of both methods' own local ranges" : ""));
		}
	}

	/**
	 * The previous, slot-order-only plan. It is kept for the case where one side has no local variable table, so
	 * no scope is known at all: it can then still move whole slot numbers out of the way, which is all a by-order
	 * capture needs, but it cannot put a game local back into the slot the game declares it in and it gives up
	 * rather than move a slot that a game local also uses.
	 */
	private static void moveBySlot(ClassNode optifine, MethodNode patched, MethodNode game, List<Slot> patchedLocals,
			List<Slot> gameLocals, String layouts) {
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

		Set<Integer> dropped = new LinkedHashSet<>(candidates.keySet());
		dropped.retainAll(keep);

		for (Integer slot : dropped) {
			System.out.println("[OptiFabric] " + optifine.name + '.' + patched.name + patched.desc
					+ " keeps the candidate " + placed(candidates.get(slot)) + " in place: slot " + slot
					+ " also holds " + entriesIn(patchedLocals, slot.intValue()) + ", and "
					+ keepReason(matches.get(slot)));
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

		for (AbstractInsnNode insn : patched.instructions.toArray()) {
			if (insn instanceof VarInsnNode var) {
				Integer to = moves.get(Integer.valueOf(var.var));
				if (to != null) var.var = to.intValue();
			} else if (insn instanceof IincInsnNode inc) {
				Integer to = moves.get(Integer.valueOf(inc.var));
				if (to != null) inc.var = to.intValue();
			}
		}

		if (patched.localVariables != null) {
			for (LocalVariableNode local : patched.localVariables) {
				Integer to = moves.get(Integer.valueOf(local.index));
				if (to != null) local.index = to.intValue();
			}
		}

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

	/**
	 * The method's locals in slot order, without the receiver and the arguments.
	 *
	 * <p>Read from the local variable table when the class has one, which it normally does: OptiFine's compiled
	 * classes carry one, and so does the game's own jar. If either side does not - Fabric's runtime remapping of
	 * the game's classes is the case to watch, and debug information is not guaranteed - the layout is rebuilt
	 * from the instructions instead, which cannot tell more than the locals' categories apart (see
	 * {@link #sameType}) and has no scope at all, so {@link #realign} falls back to {@link #moveBySlot}.
	 */
	private static List<Slot> bySlot(MethodNode method, int frameSize) {
		List<Slot> fromTable = new ArrayList<>();

		if (method.localVariables != null) {
			for (LocalVariableNode local : method.localVariables) {
				if (local.index >= frameSize) {
					//The scope is kept: the same slot can be reused by locals that never live at the same time.
					fromTable.add(new Slot(local, method.instructions.indexOf(local.start),
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
	 * Whether two locals are alive at the same time. It has no say in what the pairing does, but it is what keeps
	 * two live values out of one slot: the same slot number is handed to unrelated locals whose scopes do not
	 * overlap, and only entries that overlap may not share a target.
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

	/** Why the no-table fallback left a slot where it is: the match that did it, and whether it is the same local. */
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
		/** The table entry this was read from, so its slot can be rewritten with the rest of the plan. */
		final LocalVariableNode node;
		/** The slot the game declares this entry in, or -1 when it could not be paired with one of the game's. */
		int wanted = -1;

		Slot(LocalVariableNode node, int start, int end) {
			this.slot = node.index;
			this.desc = node.desc;
			this.fromTable = true;
			this.start = start;
			this.end = end;
			this.node = node;
		}

		Slot(int slot, String desc) {
			this.slot = slot;
			this.desc = desc;
			this.fromTable = false;
			this.start = -1;
			this.end = -1;
			this.node = null;
		}
	}
}
