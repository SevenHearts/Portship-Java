package svh.portship.format.vfs;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class VFSArchive {

	Path				 root		   = null;
	String				 name		   = null;
	File				 file		   = null;
	long				 initialOffset = 0;
	Map<String, VFSFile> files		   = new HashMap<>();

	VFSArchive() {
	}
	
	public Map<String, VFSFile> getFiles() {
		return this.files;
	}

	public boolean isRoot() {
		return this.name.equals("ROOT.VFS");
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
