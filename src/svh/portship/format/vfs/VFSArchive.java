package svh.portship.format.vfs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VFSArchive {

	String				 name		   = null;
	File				 file		   = null;
	long				 initialOffset = 0;
	Map<String, VFSFile> files		   = new HashMap<>();

	VFSArchive() {
	}

	public VFSArchive(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public File getFile() {
		return this.file;
	}

	public long getInitialOffset() {
		return this.initialOffset;
	}

	public int count() {
		return this.files.size();
	}
}
