package svh.portship;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import svh.portship.format.vfs.VFSManager;
import svh.portship.format.vfs.VFSManager.IDXResult;

public class Portship {

	static final Pattern ANSI_PATTERN = Pattern
			.compile("[\\u001b\\u009b][\\[()#;?]*(?:[0-9]{1,4}(?:;[0-9]{0,4})*)?[0-9A-ORZcf-nqry\\=\\>\\<]");

	public static final Logger LOG = Logger.getLogger("portship");

	public static void main(String[] args) throws FileNotFoundException, IOException {
		PortshipOptions opts = new PortshipOptions();
		JCommander jcommander = new JCommander(opts);
		try {
			jcommander.setProgramName("portship");
			jcommander.parse(args);
		} catch (ParameterException e) {
			StringBuilder builder = new StringBuilder();
			jcommander.usage(builder);
			System.err.println(builder);
			System.err.println(e.getMessage());
			System.exit(1);
		}

		Portship.setupLogger(opts.logLevel, opts.ansi);

		VFSManager manager = new VFSManager();

		IDXResult idxResult = manager.loadIDX(opts.prefix.toPath().resolve("data.idx").toFile());
		Portship.LOG.info("VFS standard version: " + idxResult.getStdVersion());
		Portship.LOG.info("VFS current version: " + idxResult.getCurrentVersion());
		Portship.LOG.info(String.format("found %d VFS entries", idxResult.getEntries().size()));

		for (IDXResult.Entry entry : idxResult.getEntries()) {
			Portship.LOG.info("- name: " + entry.getArchive().getName());
			Portship.LOG.info("  offset: " + entry.getOffset());
			Portship.LOG.info("  file count: " + entry.getArchive().count());
		}
	}

	private static void setupLogger(Level level, final boolean ansi) {
		Portship.LOG.setUseParentHandlers(false);

		if (level != null) {
			Portship.LOG.setLevel(level);
		}

		Portship.LOG.addHandler(new Handler() {

			@Override
			public void publish(LogRecord record) {
				String message = record.getMessage();
				if (!ansi) {
					Matcher matcher = Portship.ANSI_PATTERN.matcher(message);
					message = matcher.replaceAll("");
				}

				System.out.format("%s: [%s]\t%s%n", record.getLoggerName(), record.getLevel().getName(),
						record.getMessage());
			}

			@Override
			public void flush() {
				System.out.flush();
			}

			@Override
			public void close() throws SecurityException {
				// Doesn't close.
			}
		});
	}
}
