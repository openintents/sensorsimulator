package org.openintents.tools.simulator.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileExtensionFilter extends FileFilter {
	private String mExtension;

	public FileExtensionFilter(String extension) {
		this.mExtension = extension;
	}

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		String path = file.getAbsolutePath();
		String extension = mExtension;
		if (path.endsWith(extension)
				&& (path.charAt(path.length() - extension.length()) == '.')) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return mExtension;
	}
}