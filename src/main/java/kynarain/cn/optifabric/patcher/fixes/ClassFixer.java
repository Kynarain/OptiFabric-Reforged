/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 and 1.21.11 / Fabric Loader 0.19.x.
 */

package kynarain.cn.optifabric.patcher.fixes;


import org.objectweb.asm.tree.ClassNode;

public interface ClassFixer {
	/**
	 * Fixes one class OptiFine patched.
	 *
	 * @param optifine the class as OptiFine's own compilation produced it; a fixer may change it in place
	 * @param minecraft the game's version of the same class, which is <b>shared and cached</b> for every class
	 *                  ({@code OptifineInjector.GAME_CLASSES}) and must be treated as immutable - every current
	 *                  fixer only reads it, and the ones that read it more than once rely on that
	 */
	void fix(ClassNode optifine, ClassNode minecraft);
}
