package svh.portship.format.vfs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class VFSFile {

	private final VFSArchive parent;
	String					 path		= null;
	long					 offset		= 0;
	long					 length		= 0;
	long					 blockSize	= 0;
	boolean					 deleted	= false;
	boolean					 encrypted	= false;
	boolean					 compressed	= false;
	int						 version	= 0;
	int						 crc		= 0;

	VFSFile(VFSArchive parent) {
		this.parent = parent;
	}

	public VFSArchive getArchive() {
		return this.parent;
	}

	public String getPath() {
		return this.path;
	}

	public long getOffset() {
		return this.offset;
	}

	public long getBlockSize() {
		return this.blockSize;
	}
	
	public boolean isCompressed() {
		return this.compressed;
	}
	
	public boolean isEncrypted() {
		return this.encrypted;
	}
	
	public boolean isDeleted() {
		return this.deleted;
	}
	
	public String getNormalizedPath() {
		return VFSFile.winToUnix(this.path);
	}

	public InputStream getInputStream() throws IOException {
		if (this.parent.isRoot()) {
			return new FileInputStream(this.getArchive().root.resolve(VFSFile.winToUnix(this.path)).toFile());
		}

		return new VFSFileStream();
	}
	
	private static String winToUnix(String path) {
		return path.replace('\\', '/');
	}

	private class VFSFileStream extends InputStream {

		private final RandomAccessFile raf;
		private long			  count	= 0;

		public VFSFileStream() throws IOException {
			this.raf = new RandomAccessFile(VFSFile.this.getArchive().file, "r");
			long absoluteOffset = VFSFile.this.getArchive().initialOffset + VFSFile.this.offset;
			this.raf.seek(absoluteOffset);
		}

		@Override
		public void close() throws IOException {
			this.raf.close();
		}

		@Override
		public int read() throws IOException {
			if (this.count++ >= VFSFile.this.length) {
				return -1;
			}

			return this.raf.read();
		}

		@Override
		public int available() throws IOException {
			return (int) Math.min(super.available(), VFSFile.this.length - this.count);
		}
	}
}
