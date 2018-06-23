package svh.portship.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtil {
	private IOUtil() {}
	
	public static void pipe(InputStream i, OutputStream o) throws IOException {
		byte[] buffer = new byte[2048];
		int c = 0;
		
		while ((c = i.read(buffer)) != -1) {
			o.write(buffer, 0, c);
		}
	}
}
