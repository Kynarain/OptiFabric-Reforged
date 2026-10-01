/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 and 1.21.11 / Fabric Loader 0.19.x.
 */

package kynarain.cn.optifabric.mixin;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import kynarain.cn.optifabric.mod.OptifabricError;
import kynarain.cn.optifabric.mod.OptifabricSetup;
import kynarain.cn.optifabric.mod.OptifineVersion;

@Mixin(CrashReport.class)
abstract class CrashReportMixin {
	private static CrashReportSection makeSection(CrashReport crash, String name) {
		try {
			return ConstructorUtils.invokeExactConstructor(CrashReportSection.class, name);
		} catch (ReflectiveOperationException e) {
			return new CrashReportSection(name);
		}
	}

	@Unique
	private final CrashReportSection optifine = makeSection((CrashReport) (Object) this, "OptiFabric")
			.add("OptiFine jar designed for", OptifineVersion.minecraftVersion)
			.add("OptiFine jar version", OptifineVersion.version)
			.add("OptiFine jar status", () -> {
				if (OptifineVersion.jarType != null) {
					switch (OptifineVersion.jarType) {
					case MISSING:
						return "Not found";

					case CORRUPT_ZIP:
						return "Found corrupt jar whilst searching";

					case INCOMPATIBLE:
						return "Incompatible with the current OptiFabric";

					case INTERNAL_ERROR:
						return "Error whilst searching";

					case DUPLICATED:
						return "Multiple valid jars found";

					case OPTIFINE_INSTALLER:
						return "Valid OptiFine installer";

					case OPTIFINE_MOD:
						return "Valid OptiFine mod";

					case SOMETHING_ELSE:
					default:
						return "Unexpected state: " + OptifineVersion.jarType;
					}
				} else {
					return "Unsearched";
				}
			})
			.add("OptiFine remapped jar", Optional.ofNullable(OptifabricSetup.optifineRuntimeJar).map(jar -> jar.toString().replace(File.separatorChar, '/')).orElse(null))
			.add("OptiFabric error", () -> {
				if (OptifabricError.hasError()) {
					return OptifabricError.getError();
				} else {
					return "<None>";
				}
			})
		;

	/*
	 * "OptiFine could not initialise a game class" is the one crash this mod is asked about most, and its report
	 * names nothing useful. OptiFine's crash reporter reads its own version through Reflector, that read loads a
	 * game class, and the class can no longer finish loading because a mod's mixin failed to apply to it - so the
	 * report ends at Reflector instead of at the mixin. Mixin only prints the failing mod with -Dmixin.debug=true.
	 *
	 * The three messages below are what that failure looks like from the outside; the report gets a short section
	 * of its own when one of them is anywhere in the cause chain, and nothing at all when none of them is.
	 */
	@Unique
	private static boolean optifineMixinConflict(CrashReport crash) {
		for (Throwable cause = crash.getCause(); cause != null; cause = cause.getCause()) {
			String message = cause.getMessage();

			if (cause instanceof NoClassDefFoundError && message != null
					&& message.contains("net.optifine.reflect.Reflector")) {
				return true; //OptiFine's own bootstrap gave up on a game class
			}

			if (message != null && (message.contains("Mixin transformation of") || message.contains("Mixin apply for mod"))) {
				return true;
			}
		}

		return false;
	}

	@Unique
	private CrashReportSection optifineMixinConflictSection(CrashReport crash) {
		if (!optifineMixinConflict(crash)) return null;

		return makeSection(crash, "OptiFabric: OptiFine / mixin conflict")
				.add("What happened", "OptiFine could not load a game class, because a mod's mixin did not apply to a class OptiFine patches")
				.add("The mod at fault", "Mixin names it, but only with -Dmixin.debug=true: add that to the JVM arguments and run again")
				.add("Known families", "mods that capture or modify a method's local variables or arguments in a class OptiFine rewrites, mods that expect an exact number of call sites in such a method, and mods that inject at a call site OptiFine replaced with one of its own")
				.add("What to do", "the named mod's injection does not match the class as OptiFine left it; remove that mod, or play without OptiFine");
	}

	// 1.20.6 called this addStackTrace; 1.21.11 renamed it to addDetails. The intermediary name (method_555)
	// and the descriptor are unchanged, so only the mapped name differs.
	@Inject(method = "addDetails", at = @At("RETURN"))
	private void addStackTrace(StringBuilder builder, CallbackInfo info) {
		optifine.addStackTrace(builder.append("\n\n"));

		try {
			CrashReportSection conflict = optifineMixinConflictSection((CrashReport) (Object) this);

			if (conflict != null) {
				conflict.addStackTrace(builder.append("\n\n"));
			}
		} catch (Throwable t) {
			//A diagnostic that throws would end up in place of the report it was meant to explain
			builder.append("[OptiFabric] Could not check this crash for the OptiFine/mixin conflict: ").append(t).append('\n');
		}
	}
}
