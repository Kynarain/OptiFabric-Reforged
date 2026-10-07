/*
 * New in the 26.x port of OptiFabric (which is MPL-2.0, see LICENSE.txt). This file has no upstream
 * counterpart.
 *
 * Declares, on a class OptiFabric is taking over from OptiFine's patched set, a mixin interface that Fabric API
 * would otherwise have put there itself.
 *
 * Why it is needed
 * ----------------
 * OptiFabric serves OptiFine's recompiled copies of the game's classes through Fabric Loader's game transformer.
 * Mixin then builds the metadata of such a class from bytes whose accessor-mixin pass has not run, and an @Coerce
 * against one of Fabric API's @Mixin interfaces is rejected:
 *
 *   InvalidInjectionException @WrapOperation operation wrapper method
 *   net/minecraft/client/gui/render/GuiRenderer::fixNonQuadIndexing ... Cannot @Coerce argument type
 *   net.minecraft.client.gui.render.GuiRenderer$Draw at index 4 to
 *   net.fabricmc.fabric.mixin.client.rendering.GuiRendererDrawAccessor
 *
 * That message comes from org.spongepowered.asm.mixin.injection.code.Injector#checkCoerce, whose contract is that
 * the desired type (here GuiRenderer$Draw, taken from GuiRenderer.executeDraw's own arguments) must be a
 * supertype of the handler's argument type (GuiRendererDrawAccessor). ClassInfo#canCoerce answers that by walking
 * the hierarchy, and GuiRendererDrawAccessor is only in GuiRenderer$Draw's hierarchy once the accessor mixin has
 * been applied. It is not applied here, so OptiFabric declares the interface itself - which is exactly the part
 * of the accessor mixin's work the coercion check reads. Mixin still contributes the accessor methods it
 * generates, in its own ACCESSOR pass, as it does for the game's copy.
 *
 * What must NOT be done is to also implement those accessors: a class that already declares
 * fabric$pipeline()/fabric$Draw() is met by MixinApplicatorStandard.applyAccessors -> mergeMethod and the whole
 * class fails with
 *
 *   InvalidMixinException fabric$pipeline()... cannot overwrite method in
 *   net.minecraft.client.gui.render.GuiRenderer$Draw because @Overwrite is required by the parent configuration
 *
 * Measured on 26.2: the interface alone reaches the title screen; interface plus both accessors dies at 20s with
 * the message above.
 *
 * Why the interface is looked up instead of named
 * -----------------------------------------------
 * The interface is Fabric API's and its name is not stable across the releases this line serves: 26.2's
 * fabric-rendering-v1 calls it net/fabricmc/fabric/mixin/client/rendering/GuiRendererDrawAccessor, while 26.1.2's
 * calls the same thing .../DrawAccessor. Naming one of them makes the other line fail inside the fixer with
 * "Error loading class: ... (ClassNotFoundException)". So the interface is found the way Mixin finds it: the mods'
 * own mixin packages are scanned for an interface whose @Mixin names this class, and a candidate only counts if it
 * declares a method the class does not already have - which is what rules out an interface that merely happens to
 * target an inner class with the same short name.
 */
package kynarain.cn.optifabric.patcher.fixes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class AddInterfaceFix implements ClassFixer {
	/** The interfaces found for a class, worked out once for the whole run. */
	private static final Map<String, Set<Candidate>> INTERFACES = new LinkedHashMap<>();

	private final String targetClass;

	/** @param targetClass the internal name of the class whose hierarchy the mixin interface has to join */
	public AddInterfaceFix(String targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public void fix(ClassNode optifine, ClassNode minecraft) {
		Set<Candidate> candidates = INTERFACES.computeIfAbsent(targetClass, AddInterfaceFix::findMixinInterfaces);

		if (candidates.isEmpty()) {
			System.err.println("[OptiFabric] No mixin interface in the loaded mods targets " + targetClass
					+ ", leaving it as OptiFine compiled it");
			return;
		}

		for (Candidate candidate : candidates) {
			if (optifine.interfaces.contains(candidate.name)) continue;

			List<String> missing = new ArrayList<>();

			for (String method : candidate.methods) {
				String name = method.substring(0, method.indexOf('('));
				String desc = method.substring(method.indexOf('('));

				if (!hasMethod(optifine, name, desc)) missing.add(method);
			}

			if (missing.isEmpty()) {
				System.out.println("[OptiFabric] Not adding " + candidate.name + " to " + optifine.name
						+ ": it declares nothing this class does not already have");
				continue;
			}

			optifine.interfaces.add(candidate.name);
			System.out.println("[OptiFabric] Added " + candidate.name + " to " + optifine.name
					+ " so the mixin that coerces to it can apply (its " + String.join(", ", missing)
					+ " will be contributed by Mixin)");
		}
	}

	/** Every interface in the loaded mods whose {@code @Mixin} names the given class. */
	private static Set<Candidate> findMixinInterfaces(String targetClass) {
		Set<Candidate> found = new LinkedHashSet<>();

		for (Path jar : modJars()) {
			boolean zipFile = Files.isRegularFile(jar);

			if (!zipFile && !Files.isDirectory(jar)) continue;

			try (ZipFile zip = zipFile ? new ZipFile(jar.toFile()) : null) {
				Set<String> packages = mixinPackages(zip, jar);

				if (packages.isEmpty()) continue;

				for (String className : classNames(zip, jar, packages)) {
					if (className.indexOf('$') >= 0) continue;

					ClassNode node = read(zip, jar, className);

					if (node == null || (node.access & Opcodes.ACC_INTERFACE) == 0) continue;
					if (!targets(node, targetClass)) continue;

					List<String> methods = new ArrayList<>();

					for (MethodNode method : node.methods) {
						if ((method.access & Opcodes.ACC_ABSTRACT) != 0) methods.add(method.name + method.desc);
					}

					if (methods.isEmpty()) continue;

					found.add(new Candidate(className, methods));
				}
			} catch (IOException | RuntimeException e) {
				//not a zip, or one this JVM will not read: nothing to find there
			}
		}

		return found;
	}

	/** The class names under the given packages, from a jar's entries or by walking a directory. */
	private static Set<String> classNames(ZipFile zip, Path root, Set<String> packages) {
		Set<String> out = new LinkedHashSet<>();

		if (zip != null) {
			for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
				String name = entries.nextElement().getName();

				if (!name.endsWith(".class")) continue;

				String className = name.substring(0, name.length() - 6);

				if (isInPackages(className, packages)) out.add(className);
			}

			return out;
		}

		try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
			stream.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".class"))
					.forEach(path -> {
						String name = root.relativize(path).toString().replace('\\', '/');

						name = name.substring(0, name.length() - 6);

						if (isInPackages(name, packages)) out.add(name);
					});
		} catch (IOException e) {
			//an unreadable tree contributes nothing
		}

		return out;
	}

	/** The mods' roots plus the jars nested inside them, because a nested mixin travels in its own jar. */
	private static List<Path> modJars() {
		List<Path> out = new ArrayList<>();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			try {
				Path root = mod.getRootPath();

				if (!out.contains(root)) out.add(root);
			} catch (Throwable t) {
				//a mod that cannot name its root cannot be searched; not fatal here
			}
		}

		List<Path> nested = new ArrayList<>();

		for (Path jar : out) {
			try (ZipFile zip = new ZipFile(jar.toFile())) {
				for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
					ZipEntry entry = entries.nextElement();

					if (!entry.getName().startsWith("META-INF/jars/") || !entry.getName().endsWith(".jar")) continue;

					try (InputStream in = zip.getInputStream(entry)) {
						Path temp = Files.createTempFile("optifabric-mixin-", ".jar");

						Files.write(temp, in.readAllBytes());
						temp.toFile().deleteOnExit();
						nested.add(temp);
					} catch (IOException e) {
						//a nested jar that cannot be spilled simply is not searched
					}
				}
			} catch (IOException | RuntimeException e) {
				//not a zip
			}
		}

		for (Path jar : nested) {
			if (!out.contains(jar)) out.add(jar);
		}

		return out;
	}

	/** The packages of a jar's mixin configurations, read out of the configuration files themselves. */
	private static Set<String> mixinPackages(ZipFile zip, Path root) {
		Set<String> out = new LinkedHashSet<>();

		if (zip != null) {
			for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();

				if (!name.endsWith(".mixins.json") && !name.endsWith("mixins.json")) continue;

				String pkg = packageOf(readText(zip, entry));

				if (pkg != null) out.add(pkg.replace('.', '/'));
			}

			return out;
		}

		try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
			for (Path path : (Iterable<Path>) stream.filter(Files::isRegularFile)
					.filter(p -> p.toString().endsWith("mixins.json"))::iterator) {
				String pkg = packageOf(readText(path));

				if (pkg != null) out.add(pkg.replace('.', '/'));
			}
		} catch (IOException e) {
			//an unreadable tree contributes nothing
		}

		return out;
	}

	private static String readText(Path path) {
		try {
			return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// silent by design: returns null; the caller treats it as "no text here" and keeps going
			return null;
		}
	}

	private static boolean isInPackages(String className, Set<String> packages) {
		if (packages.isEmpty()) return false;

		for (String prefix : packages) {
			if (className.startsWith(prefix)) return true;
		}

		return false;
	}

	private static String packageOf(String text) {
		if (text == null) return null;

		int at = text.indexOf("\"package\"");

		if (at < 0) return null;

		int start = text.indexOf('"', at + 9);
		int end = start < 0 ? -1 : text.indexOf('"', start + 1);

		if (start < 0 || end < 0) return null;

		return text.substring(start + 1, end);
	}

	private static String readText(ZipFile zip, ZipEntry entry) {
		try (InputStream in = zip.getInputStream(entry)) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// silent by design: returns null; the caller treats it as "no text here" and keeps going
			return null;
		}
	}

	private static ClassNode read(ZipFile zip, ZipEntry entry) {
		try (InputStream in = zip.getInputStream(entry)) {
			return read(in);
		} catch (IOException | RuntimeException e) {
			// silent by design: returns null; this candidate is skipped and the search continues
			return null;
		}
	}

	private static ClassNode read(ZipFile zip, Path root, String className) {
		if (zip != null) {
			ZipEntry entry = zip.getEntry(className + ".class");

			return entry == null ? null : read(zip, entry);
		}

		try (InputStream in = Files.newInputStream(root.resolve(className + ".class"))) {
			return read(in);
		} catch (IOException | RuntimeException e) {
			// silent by design: returns null; this candidate is skipped and the search continues
			return null;
		}
	}

	private static ClassNode read(InputStream in) throws IOException {
		ClassNode node = new ClassNode();
		new ClassReader(in).accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

		return node;
	}

	/** Whether an annotation value - one Type or a list of them - names the given internal class name. */
	private static boolean namesTarget(Object value, String targetClass) {
		if (value instanceof org.objectweb.asm.Type type) return targetClass.equals(type.getInternalName());

		if (value instanceof List) {
			for (Object element : (List<?>) value) {
				if (namesTarget(element, targetClass)) return true;
			}
		}

		return false;
	}

	private static boolean targets(ClassNode node, String targetClass) {
		List<AnnotationNode> annotations = new ArrayList<>();

		if (node.invisibleAnnotations != null) annotations.addAll(node.invisibleAnnotations);
		if (node.visibleAnnotations != null) annotations.addAll(node.visibleAnnotations);

		for (AnnotationNode annotation : annotations) {
			if (!"Lorg/spongepowered/asm/mixin/Mixin;".equals(annotation.desc) || annotation.values == null) continue;

			for (int i = 0; i + 1 < annotation.values.size(); i += 2) {
				Object key = annotation.values.get(i);
				Object value = annotation.values.get(i + 1);

				//@Mixin(targets = "a.b.C") names the class with a String, and @Mixin(C.class) - the form the
				//annotation's own value member takes - arrives as a Type or a list of them. Both name the same
				//target, so both have to be read: an interface that used the value form used to be missed here.
				if ("targets".equals(key) && value instanceof List) {
					for (Object target : (List<?>) value) {
						if (target instanceof String && targetClass.equals(((String) target).replace('.', '/'))) return true;
					}
				} else if ("value".equals(key) && namesTarget(value, targetClass)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean hasMethod(ClassNode node, String name, String desc) {
		for (MethodNode method : node.methods) {
			if (method.name.equals(name) && method.desc.equals(desc)) return true;
		}

		return false;
	}

	/** A mixin interface that targets the class, and the methods it would contribute. */
	private static final class Candidate {
		final String name;
		final List<String> methods;

		Candidate(String name, List<String> methods) {
			this.name = name;
			this.methods = methods;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Candidate && ((Candidate) other).name.equals(name);
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}
}
