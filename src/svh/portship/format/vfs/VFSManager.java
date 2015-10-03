package svh.portship.format.vfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import svh.portship.Portship;

/**
 * IDX file reader
 * 
 * A couple notes: The negative offsets are because <code>fseek()</code>
 * supports negative offsets. Go figure :)
 * 
 * Also, <code>sizeof(long)</code> was on their 32-bit windows boxes. This means
 * the size was actually 4, not 8. We use <code>long</code> here but read a
 * 32-bit integer instead. This is probably a factor as to why ROSE was never
 * 64-bit. Huh.
 * 
 * @author Josh Junon
 *
 */
public class VFSManager {

	@SuppressWarnings("unused")
	private final Map<Path, VFSArchive> archives = new HashMap<>();

	public VFSManager() {
	}

	@SuppressWarnings({ "static-method" })
	public IDXResult loadIDX(File idx) throws FileNotFoundException, IOException {
		IDXResult result = new IDXResult();

		try (final VFSRandomAccessFile d = new VFSRandomAccessFile(idx, "r")) {
			Path base = idx.toPath().getParent();
			d.seek(0);

			result.stdVersion = d.readIntBE();
			result.currentVersion = d.readIntBE();

			int entryCount = d.readIntBE();
			for (int i = 0; i < entryCount; i++) {
				IDXResult.Entry entry = new IDXResult.Entry();

				entry.archive.name = d.readShortString();
				entry.archive.file = base.resolve(entry.archive.name).toFile();
				entry.offset = d.readIntBE(); // see notes at class javadoc.

				long position = d.getFilePointer();
				VFSManager.readFileEntries(d, entry);
				d.seek(position);

				result.entries.add(entry);
			}
		}

		return result;
	}

	private static void readFileEntries(VFSRandomAccessFile d, IDXResult.Entry entry) throws IOException {
		Portship.LOG.fine("reading entries: " + entry.archive.name);
		d.seek(entry.offset);
		int count = d.readIntBE();
		Portship.LOG.finer(String.format("found %d entries", count));
		d.readIntBE(); // this isn't really used anywhere.
		entry.archive.initialOffset = d.readIntBE(); // see notes at class
													 // javadoc.

		for (int i = 0; i < count; i++) {
			VFSFile file = VFSManager.readFileEntry(d, entry);
			entry.archive.files.put(file.path, file);
			Portship.LOG.finest(file.path);
		}
	}

	private static VFSFile readFileEntry(VFSRandomAccessFile d, IDXResult.Entry entry) throws IOException {
		VFSFile file = new VFSFile(entry.archive);

		file.path = d.readShortString();
		file.offset = d.readIntBE();
		file.length = d.readIntBE();
		file.blockSize = d.readIntBE();
		file.deleted = d.readBoolean();
		file.compressed = d.readBoolean();
		file.encrypted = d.readBoolean();
		file.version = d.readIntBE();
		file.crc = d.readIntBE();

		return file;
	}

	public static class IDXResult {

		int			stdVersion;
		int			currentVersion;
		List<Entry>	entries	= new ArrayList<>();

		public int getStdVersion() {
			return this.stdVersion;
		}

		public int getCurrentVersion() {
			return this.currentVersion;
		}

		public List<Entry> getEntries() {
			return this.entries;
		}

		public static class Entry {

			VFSArchive archive = new VFSArchive();
			long	   offset;

			public long getOffset() {
				return this.offset;
			}

			public VFSArchive getArchive() {
				return this.archive;
			}
		}
	}
}
