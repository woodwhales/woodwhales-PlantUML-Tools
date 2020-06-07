package org.woodwhales.plantuml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.woodwhales.plantuml.domain.FileTree;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileTreeTest {

	public static void main(String[] args) throws JsonProcessingException {
		String filePathName = "D:\\code\\zheng";
		
		File file = FileUtil.file(filePathName);
		
		FileTree fileTree = new FileTree(file);
		setChildFileTrees(fileTree, 0);
		
		tree(fileTree);
		
		System.out.println(new ObjectMapper().writeValueAsString(fileTree));
	}
	
	private static void tree(FileTree fileTree) {
		System.out.println(empty(fileTree.getDeep()) + fileTree.getFileName());
		if(CollectionUtils.isNotEmpty(fileTree.getChildFileTrees())) {
			List<FileTree> childFileTrees = fileTree.getChildFileTrees();
			for (FileTree child : childFileTrees) {
				tree(child);
			}
		}
		
	}
	
	private static void setChildFileTrees(FileTree fileTree, int deep) {
		fileTree.setDeep(deep);
		File[] listFiles = fileTree.getFile().listFiles();
		List<FileTree> childFileTrees = new ArrayList<>();
		List<String> childFileNames = new ArrayList<>();
		
		for (File file : listFiles) {
			if(file.isDirectory()) {
				FileTree childFileTree = new FileTree(file);
				setChildFileTrees(childFileTree, deep + 1);
				childFileTrees.add(childFileTree);
				childFileNames.add(file.getName());
			}
		}
		fileTree.setChildFileTrees(childFileTrees);
		fileTree.setChildFileNames(childFileNames);
	}

	private static String empty(int deep) {
		StringBuffer sb = new StringBuffer("");
		
		if(deep == 0) {
			return "";
		}
		
		for (int i=0; i< deep; i++) {
			sb.append(" ");
		}
		sb.append(">");
		return sb.toString();
	}
	
}
