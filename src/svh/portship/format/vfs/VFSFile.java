package svh.portship.format.vfs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

	public InputStream getInputStream() throws IOException {
		return new VFSFileStream();
	}

	private class VFSFileStream extends InputStream {

		private final InputStream is;
		private long			  count	= 0;

		public VFSFileStream() throws IOException {
			this.is = new FileInputStream(VFSFile.this.getArchive().file);
			long absoluteOffset = VFSFile.this.getArchive().initialOffset + VFSFile.this.offset;
			for (int i = 0; i < absoluteOffset; i++) {
				this.is.read();
			}
		}

		@Override
		public void close() throws IOException {
			this.is.close();
		}

		@Override
		public int read() throws IOException {
			if (this.count++ >= VFSFile.this.length) {
				return -1;
			}

			return this.is.read();
		}

		@Override
		public int available() throws IOException {
			return (int) Math.min(super.available(), VFSFile.this.length - this.count);
		}
	}
}
