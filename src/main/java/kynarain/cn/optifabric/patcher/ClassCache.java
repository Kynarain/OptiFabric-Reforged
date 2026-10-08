/*
 * Ported from OptiFabric (https://github.com/Chocohead/OptiFabric), MPL-2.0.
 * Adapted for Minecraft 1.20.6 / Fabric Loader 0.19.x.
 */

package kynarain.cn.optifabric.patcher;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import org.apache.commons.lang3.Validate;

public class ClassCache {
	private final byte[] hash;
	private final Map<String, byte[]> classes = new HashMap<>();
	private boolean converted;

	public ClassCache(byte[] hash) {
		this.hash = hash;
	}

	public void addClass(String name, byte[] bytes) {
		if (classes.containsKey(name)) {
			throw new IllegalArgumentException(name + " is already in ClassCache");
		}

		classes.put(name, Validate.notNull(bytes, "Passed null bytes for %s", name));
	}

	public Set<String> getClasses() {
		return classes.keySet();
	}

	public byte[] getClass(String name) {
		return classes.get(name);
	}

	public byte[] popClass(String name) {
		return classes.remove(name);
	}

	public byte[] getHash() {
		return hash;
	}

	private long calculateCRC() {
		CRC32 crc = new CRC32();

		crc.update(hash);
		for (byte[] clazz : classes.values()) crc.update(clazz);

		return crc.getValue();
	}


	/**
	 * Ceilings for the four lengths a cache file declares. A corrupt header used to reach the array allocation
	 * directly: a negative length threw NegativeArraySizeException out of read(), and an absurd one allocated
	 * before the CRC below could reject the file. Both mean the same thing as any other corruption, so they
	 * take the same route - the empty cache that makes the patcher rebuild.
	 */
	private static final int MAX_HASH_BYTES = 1024;
	private static final int MAX_NAME_BYTES = 64 * 1024;
	private static final int MAX_CLASS_BYTES = 16 * 1024 * 1024;
	private static final int MAX_CLASSES = 100_000;

	public static ClassCache read(File input) throws IOException {
		FileInputStream fileIn = new FileInputStream(input);

		//The file stream is a resource of its own. When the gzip header is missing the GZIPInputStream
		//constructor throws before its declaration completes, and a file stream created inside that same
		//declaration would then never be registered, so it would never be closed on exactly the corrupt-file
		//path this method exists to survive.
		try (DataInputStream dis = new DataInputStream(new GZIPInputStream(fileIn))) {
			char formatRevision = dis.readChar(); //Check the format of the file
			if (formatRevision != 'E') return new ClassCache(null);

			long expectedCRC = dis.readLong();

			//Read the hash
			int hashLength = dis.readInt();
			if (hashLength < 0 || hashLength > MAX_HASH_BYTES) return new ClassCache(null);
			byte[] hash = new byte[hashLength];
			dis.readFully(hash);
			ClassCache classCache = new ClassCache(hash);

			int count = dis.readInt();
			if (count < 0 || count > MAX_CLASSES) return new ClassCache(null);
			for (int i = 0; i < count; i++) {
				int nameLength = dis.readInt();
				if (nameLength < 0 || nameLength > MAX_NAME_BYTES) return new ClassCache(null);
				byte[] nameBytes = new byte[nameLength];
				dis.readFully(nameBytes);
				String name = new String(nameBytes, StandardCharsets.UTF_8);

				int bodyLength = dis.readInt();
				if (bodyLength < 0 || bodyLength > MAX_CLASS_BYTES) return new ClassCache(null);
				byte[] bytes = new byte[bodyLength];
				dis.readFully(bytes);
				classCache.classes.put(name, bytes);
			}

			//Ensure the read contents matches up with what was expected
			if (classCache.calculateCRC() != expectedCRC) return new ClassCache(null);

			return classCache;
		} catch (ZipException e) {
			//InflaterInputStream can throw this when the data is corrupt
			return new ClassCache(null);
		}
	}

	public void save(File output) throws IOException {
		if (output.exists()) {
			output.delete();
		}

		try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(output)))) {
			dos.writeChar('E'); //Format version
			dos.writeLong(calculateCRC()); //Expected CRC to get from fully reading

			//Write the hash
			dos.writeInt(hash.length);
			dos.write(hash);

			//Write the number of classes
			dos.writeInt(classes.size());
			for (Entry<String, byte[]> clazz : classes.entrySet()) {
				String name = clazz.getKey();
				byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
				byte[] bytes = clazz.getValue();

				//Write the name
				dos.writeInt(nameBytes.length);
				dos.write(nameBytes);

				//Write the actual bytes
				dos.writeInt(bytes.length);
				dos.write(bytes);
			}
		}
	}

	public boolean isConverted() {
		return converted;
	}
}
