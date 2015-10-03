package svh.portship;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import svh.portship.format.vfs.VFSArchive;
import svh.portship.format.vfs.VFSFile;
import svh.portship.format.vfs.VFSManager.IDXResult;
import svh.portship.util.IOUtil;

public final class PortshipConverter {

	private PortshipConverter() {
	}

	static boolean convert(IDXResult idx, File targetDir, boolean full) {
		targetDir.mkdirs();

		int total = 0;
		Portship.LOG.info("calculating total files...");
		for (IDXResult.Entry entry : idx.getEntries()) {
			total += entry.getArchive().count();
		}
		Portship.LOG.info("about to process " + total + " files");

		int counter = 0;
		for (IDXResult.Entry entry : idx.getEntries()) {
			VFSArchive archive = entry.getArchive();
			Portship.LOG.info("extracting \u001b[36m" + archive.getName() + "\u001b[0m");

			if (archive.getFile().getName().equals("ROOT.VFS")) {
				Portship.LOG.finer("ROOT.VFS detected; pulling from filesystem");
			} else {
				if (archive.getFile().exists()) {
					Portship.LOG.finer(archive.getFile().toString() + " exists");
				} else {
					Portship.LOG.severe("\u001b[31;1m" + archive.getFile() + " doesn't exist\u001b[0m");
					return false;
				}
			}

			for (VFSFile file : archive.getFiles().values()) {
				String progress = PortshipConverter.makeLogPrefix(++counter, total, full);

				if (!PortshipConverter.checkEligibility(file, progress)) {
					continue;
				}

				File outFile = targetDir.toPath().resolve(file.getNormalizedPath()).toFile();
				outFile.getParentFile().mkdirs();
				try {
					try (FileOutputStream fos = new FileOutputStream(outFile)) {
						try (InputStream fis = file.getInputStream()) {
							IOUtil.pipe(fis, fos);
						}
					}
				} catch (FileNotFoundException e) {
					Portship.LOG.warning(progress
							+ String.format("\u001b[33mskip\u001b[0m %s (not found)", file.getNormalizedPath()));
					Portship.LOG.finest(e.toString());
					continue;
				} catch (IOException e) {
					Portship.LOG.warning(progress
							+ String.format("\u001b[31;1mskip\u001b[0m %s (error)", file.getNormalizedPath()));
					e.printStackTrace(System.err);
					continue;
				}

				Portship.LOG.info(progress + "\u001b[36mextracted\u001b[0m " + file.getNormalizedPath());
			}
		}

		return true;
	}

	private static String makeLogPrefix(int i, int o, boolean full) {
		String erase = "";
		if (!full) {
			erase = "\u001b[2K\u001b[F\u001b[2K";
		}
		return String.format(erase + "(%d / %d - %.01f%%)  ", i, o, ((float) i / (float) o) * 100f);
	}

	static boolean checkEligibility(VFSFile file, String logPrefix) {
		String path = file.getNormalizedPath();

		if (file.isEncrypted()) {
			Portship.LOG
					.info(logPrefix + String.format("\u001b[35mskip\u001b[0m %s \u001b[1m(encrypted)\u001b[0m", path));
			return false;
		}

		if (file.isCompressed()) {
			Portship.LOG
					.info(logPrefix + String.format("\u001b[35mskip\u001b[0m %s \u001b[1m(compressed)\u001b[0m", path));
			return false;
		}

		if (file.isDeleted()) {
			Portship.LOG
					.info(logPrefix + String.format("\u001b[35mskip\u001b[0m %s \u001b[1m(deleted)\u001b[0m", path));
			return false;
		}

		return true;
	}
}
