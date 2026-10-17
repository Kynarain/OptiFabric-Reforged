/*
 * Regression tests for the two pure helpers behind the call re-creation. The P0 this pins was a wrong argument: the
 * slots of a re-created call were walked with the wrong width, so two parameters of the same type could be swapped.
 * Nothing here needs Minecraft - only ASM's view of a method descriptor.
 */

package kynarain.cn.optifabric.patcher.fixes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

class InjectionCallPointFixTest {
	private static MethodNode method(String desc, int access) {
		return new MethodNode(Opcodes.ASM9, access, "target", desc, null, null);
	}

	@Test
	void slotsOfAnInstanceMethodSkipTheReceiver() {
		MethodNode node = method("(IJLjava/lang/String;D)V", Opcodes.ACC_PUBLIC);

		assertEquals(Type.INT_TYPE, InjectionCallPointFix.slotType(node, "Owner", 1), "first parameter is in slot 1");
		assertEquals(Type.LONG_TYPE, InjectionCallPointFix.slotType(node, "Owner", 2), "a long takes two slots");
		assertEquals(Type.getObjectType("java/lang/String"), InjectionCallPointFix.slotType(node, "Owner", 4),
				"the string follows the two-slot long, not slot 3");
		assertEquals(Type.DOUBLE_TYPE, InjectionCallPointFix.slotType(node, "Owner", 5), "the double is last");
		assertEquals(Type.getObjectType("Owner"), InjectionCallPointFix.slotType(node, "Owner", 0), "slot 0 is this");
	}

	@Test
	void slotsOfAStaticMethodStartAtZero() {
		MethodNode node = method("(JI)V", Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);

		assertEquals(Type.LONG_TYPE, InjectionCallPointFix.slotType(node, "Owner", 0), "no receiver in a static method");
		assertEquals(Type.INT_TYPE, InjectionCallPointFix.slotType(node, "Owner", 2), "the int is after the long");
		assertNull(InjectionCallPointFix.slotType(node, "Owner", 3), "there is nothing in slot 3");
		assertNull(InjectionCallPointFix.slotType(node, "Owner", 0) == null ? null : InjectionCallPointFix.slotType(node, "Owner", 99),
				"well past the end is still null");
	}

	@Test
	void theSizeOfAnInstructionIsWhatItPushes() {
		assertEquals(1, InjectionCallPointFix.producedSize(new VarInsnNode(Opcodes.ILOAD, 0)), "int is one slot");
		assertEquals(2, InjectionCallPointFix.producedSize(new VarInsnNode(Opcodes.DLOAD, 0)), "double is two");
		assertEquals(1, InjectionCallPointFix.producedSize(new InsnNode(Opcodes.ICONST_0)), "a constant is one");
		assertEquals(2, InjectionCallPointFix.producedSize(new InsnNode(Opcodes.LCONST_0)), "a long constant is two");
		assertEquals(1, InjectionCallPointFix.producedSize(new LdcInsnNode("x")), "an LDC of a string is one");
		assertEquals(2, InjectionCallPointFix.producedSize(new LdcInsnNode(1L)), "an LDC of a long is two");
		assertEquals(-1, InjectionCallPointFix.producedSize(new InsnNode(Opcodes.RETURN)), "a void return pushes nothing");
		assertFalse(InjectionCallPointFix.producedSize(new InsnNode(Opcodes.RETURN)) > 0, "and it is not a candidate load");
	}
}
