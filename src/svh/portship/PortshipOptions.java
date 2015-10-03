package svh.portship;

import java.io.File;
import java.util.logging.Level;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class PortshipOptions {
	
	@Parameter(
			names = { "--all", "--full" },
			description = "Don't condense the log outputs")
	public boolean full = false;
	
	@Parameter(
			names = "--log",
			description = "Specifies the logging level",
			arity = 1,
			validateWith = LoggingLevelValidator.class,
			converter = LoggingLevelConverter.class)
	public Level logLevel = null;
	
	@Parameter(
			names = { "--colors", "--color", "--ansi", "-c" },
			description = "Enable ANSI colors")
	public boolean ansi = false;

	@Parameter(
			names = "--prefix",
			description = "Path to the directory holding data.idx",
			required = true,
			arity = 1,
			validateValueWith = PrefixExistenceChecker.class)
	public File prefix;
	
	@Parameter(
			names = { "--out", "--target" },
			description = "Directory to place processed files",
			required = true,
			arity = 1,
			validateValueWith = TargetChecker.class)
	public File target;
	
	public static class TargetChecker implements IValueValidator<File> {

		@Override
		public void validate(String name, File value) throws ParameterException {
			if (value.exists() && !value.isDirectory()) {
				throw new ParameterException("target path exists and is not a directory: " + value.toString());
			}
		}
	}

	public static class PrefixExistenceChecker implements IValueValidator<File> {
		public PrefixExistenceChecker() {
		}

		@Override
		public void validate(String name, File value) throws ParameterException {
			if (!value.exists() || !value.isDirectory()) {
				throw new ParameterException("prefix does not exist or is not a directory: " + value.toString());
			}

			File idxFile = value.toPath().resolve("data.idx").toFile();

			if (!idxFile.exists() || !idxFile.isFile()) {
				throw new ParameterException(
						"prefix does not contain data.idx, or it is not a file: " + value.toString());
			}
		}
	}
	
	public static class LoggingLevelConverter implements IStringConverter<Level> {
		@Override
		public Level convert(String value) {
			return Level.parse(value);
		}
	}
	
	public static class LoggingLevelValidator implements IParameterValidator {

		@Override
		public void validate(String name, String value) throws ParameterException {
			try {
				Level.parse(value);
			} catch (IllegalArgumentException e) {
				throw new ParameterException("illegal log level: " + value, e);
			}
		}
	}
}
