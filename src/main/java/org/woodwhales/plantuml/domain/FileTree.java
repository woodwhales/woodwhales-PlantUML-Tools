package org.woodwhales.plantuml.domain;

import java.io.File;
import java.util.List;

import lombok.Data;

@Data
public class FileTree {

	public FileTree(File file) {
		this.file = file;
		this.fileName = file.getName();
	}

	private int deep;
	
	private File file;
	
	private String fileName;
	
	private List<String> childFileNames;
	
	private List<FileTree> childFileTrees;

}
