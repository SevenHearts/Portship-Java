package svh.portship;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;
import svh.portship.format.vfs.VFSFile;
import svh.portship.util.IOUtil;

public final class PortshipConverter {

	private PortshipConverter() {
	}
	
	public static class UnknownTypeException extends Exception {
		private static final long serialVersionUID = -6194060208736728640L;

		public UnknownTypeException() {
		}
	}

	public static boolean convert(final VFSFile vfsFile, File outFile, InputStream stream,
			String progress) throws UnknownTypeException {
		String[] exts = outFile.getName().split("\\.");
		if (exts.length <= 1) {
			return false;
		}
		String ext = exts[exts.length - 1];

		switch (ext) {
		case "dds":
			String original = outFile.toString();
			outFile = PortshipConverter.changeExt(outFile, "png");
			return PortshipConverter.toPNG(vfsFile, stream, original, outFile, progress);
		default:
			throw new UnknownTypeException();
		}
	}

	private static boolean toPNG(VFSFile vfsFile, InputStream ddsStream, String originalName, File newFile, String progress) {
		byte[] arr = null;

		try (ByteArrayOutputStream out = new ByteArrayOutputStream((int) vfsFile.getLength())) {
			IOUtil.pipe(ddsStream, out);
			arr = out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			return false;
		}

		try {
			ImageInfo info = new ImageInfo();
			info.setFileName(originalName);
			MagickImage image = new MagickImage(info, arr);
			ImageInfo target = new ImageInfo();
			target.setFileName(newFile.toString());
			byte[] blob = image.imageToBlob(target);
			newFile.getParentFile().mkdirs();
			try (ByteArrayInputStream bis = new ByteArrayInputStream(blob)) {
				try (FileOutputStream fos = new FileOutputStream(newFile)) {
					IOUtil.pipe(bis, fos);
				}
			}
			Portship.LOG.info(progress + "\u001b[38;5;199mDDS -> PNG:\u001b[0m " + newFile.toString());
		} catch (MagickException | IOException e) {
			e.printStackTrace(System.err);
			return false;
		}

		return true;
	}

	private static File changeExt(File original, String extension) {
		String fs = original.toString();
		return new File(fs.substring(0, fs.lastIndexOf('.')) + "." + extension);
	}
}
