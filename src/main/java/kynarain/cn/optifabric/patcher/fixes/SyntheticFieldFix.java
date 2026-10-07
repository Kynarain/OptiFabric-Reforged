/*
 * New in the 1.20.6 port of OptiFabric (which is MPL-2.0, see LICENSE.txt). Upstream solved the same class
 * of problem with hand written "contextual mapping" entries instead; this file is a rule driven replacement
 * written for this port.
 *
 * OptiFine recompiles the classes it patches, and javac names the fields it synthesises for inner classes
 * "this$0" / "this$1" (and captured locals "val$name"). Minecraft's own version of those fields carries an
 * obfuscated name that the mappings translate, so a plain member remap cannot connect the two - and mods do
 * shadow them: fabric-lifecycle-events-v1 shadows ClientWorld$ClientEntityHandler.field_27735,
 * fabric-model-loading-api-v1 shadows ModelLoader$BakerImpl.field_40571 and fabric-renderer-indigo shadows
 * ChunkBuilder$BuiltChunk$RebuildTask.field_20839, all of which then fail to apply.
 *
 * Upstream hard coded exactly these three as "contextual mappings". This does it by descriptor instead: a
 * synthetic field is renamed to the one vanilla field with the same type, so it works for any class.
 *
 * Renaming is the whole repair for the outer-instance and captured-local fields above, because the only code
 * that reads them is the class's own and it is renamed with them. It is not the whole repair for javac's enum
 * switch map (see keepGameNameAsWell): the game's own bytecode can read that one from outside the class, and
 * there the rename is what breaks the reference.
 */
package kynarain.cn.optifabric.patcher.fixes;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class SyntheticFieldFix implements ClassFixer {
	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		Set<String> claimed = new HashSet<>();

		for (int index = 0; index < optifine.fields.size(); index++) {
			FieldNode field = optifine.fields.get(index);
			if (!isSynthetic(field.name)) continue;

			FieldNode match = null;

			// A recompiled class keeps the declaration order of its fields, so the field at the same position is
			// the same field - and that is the only way to tell two captured locals of the same type apart
			// (ModelManager$1 has two SpriteLoader.Preparations fields, so the type alone is ambiguous).
			//
			// That order is only evidence while the two classes declare the same number of fields: one field
			// gained or lost shifts everything after it, and a shifted match would look exactly like a real one.
			// So the counts have to agree before a position is trusted, and two fields of the same type that
			// swapped places stay the one case this rule cannot see - their names are the only thing that would
			// tell them apart, which is the thing being reconstructed.
			if (optifine.fields.size() == minecraft.fields.size() && index < minecraft.fields.size()) {
				FieldNode positional = minecraft.fields.get(index);

				if (positional.desc.equals(field.desc) && !has(optifine, positional.name, positional.desc)
						&& !claimed.contains(positional.name)) {
					match = positional;
					System.out.println("[OptiFabric] Synthetic field " + optifine.name + '.' + field.name
							+ " matches the field at the same position");
				}
			}

			if (match == null) {
				int candidates = 0;

				for (FieldNode vanilla : minecraft.fields) {
					if (!vanilla.desc.equals(field.desc)) continue;
					if (has(optifine, vanilla.name, vanilla.desc)) continue; //already there under its real name
					if (claimed.contains(vanilla.name)) continue;

					match = vanilla;
					candidates++;
				}

				if (candidates > 1) match = null;
			}

			if (match == null) {
				System.err.println("[OptiFabric] Cannot resolve the synthetic field " + optifine.name + '.' + field.name
						+ field.desc + ": no unique field of that type is left in the game's class");

				continue;
			}

			claimed.add(match.name);

			// Read before the rename: everything this fixer does to the class body keys off the name OptiFine's
			// own copy carries, and after the next line that name is gone from the class.
			String synthetic = field.name;
			boolean keepGameNameAsWell = keepGameNameAsWell(field, match);
			int onlyWrite = keepGameNameAsWell ? writesOnlyFromInitialiser(optifine, synthetic) : -1;

			// A field that is written from somewhere other than the class initialiser cannot be kept as a second
			// copy: nothing would fill it. Leave it renamed only, which is the repair for every non-static case.
			if (onlyWrite == 0) {
				System.err.println("[OptiFabric] Not keeping " + optifine.name + '.' + synthetic + " as well: it is"
						+ " written from other than the class initialiser, where the copy could not be filled");

				keepGameNameAsWell = false;
			}

			field.name = match.name;
			field.signature = null; //the generic signature, if any, describes the synthetic declaration

			int references = 0;

			for (MethodNode method : optifine.methods) {
				for (AbstractInsnNode insn : method.instructions.toArray()) {
					if (insn instanceof FieldInsnNode access && access.owner.equals(optifine.name) && access.name.equals(synthetic)) {
						access.name = match.name;
						references++;
					}
				}
			}

			System.out.println("[OptiFabric] Renamed synthetic field " + optifine.name + '.' + synthetic + " to " + match.name
					+ " (" + references + " reference(s)) so mods can shadow it");

			if (!keepGameNameAsWell) continue;

			addAliasInitialiser(optifine, synthetic, match);
			optifine.fields.add(new FieldNode(match.access, synthetic, match.desc, null, null));
			System.out.println("[OptiFabric] Kept " + optifine.name + '.' + synthetic + match.desc
					+ " on the class as well, filled by the same class initialiser write: the game's own bytecode reads"
					+ " the field under that name from another class, so both names have to resolve");
		}
	}

	/**
	 * Whether the class has to carry the game's name in addition to the one OptiFine compiled against.
	 *
	 * <p>This is the {@code class_776$1} case. Its field is javac's enum switch map for the RenderShape switch in
	 * BlockRenderDispatcher.method_3353 (renderSingleBlock), and the game declares it as {@code field_4172} while
	 * OptiFine's recompile of the same class kept javac's own
	 * {@code $SwitchMap$net$minecraft$world$level$block$RenderShape} name for it, because OptiFine compiles
	 * against Mojang's official names and the obfuscator's name never reaches it. Both names end up in use at
	 * runtime: OptiFine's own {@code renderSingleBlock} reads its name, and {@code method_3353} - which the
	 * class_776 registration puts back from the game verbatim, because Porting Lib's {@code @WrapOperation} on the
	 * getRenderType call inside it would otherwise fail the class - reads the game's. Renaming resolves one and
	 * breaks the other, and the loser's {@code getstatic} throws
	 * {@code NoSuchFieldError: Class net.minecraft.class_776$1 does not have member field 'int[] field_4172'}
	 * on the first block entity a mod renders: measured in a world, on the Twilight Forest candelabra, standing
	 * where the save was left. The failure is lazy, which is why it appears long after entry.</p>
	 *
	 * <p>Only static synthetics are kept this way. The switch maps javac emits are static by construction and are
	 * written once in the class initialiser, so a second store next to the existing one cannot change behaviour.
	 * An outer-instance {@code this$0} is written by each constructor instead, and the four classes this fixer was
	 * written for have always been repaired by the rename alone - that stays exactly as it is.</p>
	 */
	private static boolean keepGameNameAsWell(FieldNode synthetic, FieldNode match) {
		if ((match.access & Opcodes.ACC_STATIC) == 0) return false;
		return match.desc.equals(synthetic.desc); //OptifineMappings retypes a field whose types differ instead
	}

	/**
	 * The number of places the class writes this field from its class initialiser, or 0 when anything else writes
	 * it - then the copy could not be filled and the caller leaves the field renamed only.
	 */
	private static int writesOnlyFromInitialiser(ClassNode optifine, String synthetic) {
		boolean initialiser = false;
		int writes = 0;

		for (MethodNode method : optifine.methods) {
			for (AbstractInsnNode insn : method.instructions.toArray()) {
				if (!(insn instanceof FieldInsnNode access)) continue;
				if (access.getOpcode() != Opcodes.PUTSTATIC) continue;
				if (!access.owner.equals(optifine.name) || !access.name.equals(synthetic)) continue;

				if (!"<clinit>".equals(method.name)) return 0;

				initialiser = true;
				writes++;
			}
		}

		return initialiser ? writes : 0;
	}

	/**
	 * Makes the class initialiser's write to the renamed field write the kept copy as well.
	 *
	 * <p>The value is already on the stack at the original store, so this is a {@code DUP} (or {@code DUP2} for a
	 * long or double) in front of it and the store itself behind it. Neither the original store nor the exception
	 * table around it changes, and the copy is only ever an extra name for the same object.</p>
	 */
	private static void addAliasInitialiser(ClassNode optifine, String synthetic, FieldNode renamed) {
		boolean wide = renamed.desc.equals("J") || renamed.desc.equals("D");
		AbstractInsnNode duplicate = new InsnNode(wide ? Opcodes.DUP2 : Opcodes.DUP);

		// Called after the rename, so the store to fill the copy is the one under the game's name: the declaration
		// was renamed and every reference with it. Only the store is duplicated - the reads are left as they are.
		for (MethodNode method : optifine.methods) {
			if (!"<clinit>".equals(method.name)) continue;

			for (AbstractInsnNode insn : method.instructions.toArray()) {
				if (!(insn instanceof FieldInsnNode access)) continue;
				if (access.getOpcode() != Opcodes.PUTSTATIC) continue;
				if (!access.owner.equals(optifine.name) || !access.name.equals(renamed.name)) continue;

				method.instructions.insertBefore(access, duplicate.clone(null));
				method.instructions.insert(access, new FieldInsnNode(Opcodes.PUTSTATIC, optifine.name, synthetic, renamed.desc));
			}
		}
	}

	/** Whether javac synthesised this field: an outer instance, a captured local, or an enum switch map. */
	private static boolean isSynthetic(String name) {
		return name.startsWith("this$") || name.startsWith("val$") || name.startsWith("$SwitchMap$");
	}

	private static boolean has(ClassNode node, String name, String desc) {
		for (FieldNode field : node.fields) {
			if (field.name.equals(name) && field.desc.equals(desc)) return true;
		}

		return false;
	}
}
