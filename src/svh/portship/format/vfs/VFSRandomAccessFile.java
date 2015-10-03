package svh.portship.format.vfs;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class VFSRandomAccessFile extends RandomAccessFile {

	public VFSRandomAccessFile(File file, String mode) throws FileNotFoundException {
		super(file, mode);
	}

	@Override
	public void seek(long pos) throws IOException {
		if (pos < 0) {
			long npos = this.length() - pos;
			if (npos < 0) {
				throw new IOException("negative offset < 0: " + npos);
			}
			pos = npos;
		}
		super.seek(pos);
	}

	public long readLongBE() throws IOException {
		byte[] buf = new byte[8];
		int nread = this.read(buf);
		if (nread != 8) {
			throw new EOFException();
		}

		long l = ((buf[7] & 0xFF) << 56) | ((buf[6] & 0xFF) << 48) | ((buf[5] & 0xFF) << 40) | ((buf[4] & 0xFF) << 32)
				| ((buf[3] & 0xFF) << 24) | ((buf[2] & 0xFF) << 16) | ((buf[1] & 0xFF) << 8) | (buf[0] & 0xFF);
		return l;
	}

	public int readIntBE() throws IOException {
		byte[] buf = new byte[4];
		int nread = this.read(buf);
		if (nread != 4) {
			throw new EOFException();
		}

		return ((buf[3] & 0xFF) << 24) | ((buf[2] & 0xFF) << 16) | ((buf[1] & 0xFF) << 8) | (buf[0] & 0xFF);
	}

	public short readShortBE() throws IOException {
		byte[] buf = new byte[2];
		int nread = this.read(buf);
		if (nread != 2) {
			throw new EOFException();
		}

		return (short) (((buf[1] & 0xFF) << 8) | (buf[0] & 0xFF));
	}

	public String readByteString() throws IOException {
		int length = this.readByte();
		return this.readString(length);
	}

	public String readShortString() throws IOException {
		int length = this.readShortBE() - 1;
		String str = this.readString(length);
		this.skipBytes(1); // lop off that null
		return str;
	}

	public String readString(int length) throws IOException {
		byte[] buf = new byte[length];

		int nread = this.read(buf);
		if (nread != length) {
			if (nread == -1) {
				throw new EOFException();
			}

			throw new IOException(String.format("didn't read enough bytes; expected %d, got %d", length, nread));
		}

		return new String(buf);
	}
}
