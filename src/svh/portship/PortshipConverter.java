package svh.portship;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public final class PortshipConverter {
	private PortshipConverter() {}
	
	@SuppressWarnings("unused")
	public static ConversionResult convert(File outFile, InputStream input, String progress) {
		return null;
	}
	
	public static class ConversionResult {
		public final File file;
		public final OutputStream stream;
		
		ConversionResult(File file, OutputStream stream) {
			this.file = file;
			this.stream = stream;
		}
	}
}
