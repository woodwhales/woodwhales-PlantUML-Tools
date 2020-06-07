package org.woodwhales.plantuml.service;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;
import org.woodwhales.plantuml.constants.ProjectConstant;
import org.woodwhales.plantuml.domain.PackageNode;
import org.woodwhales.plantuml.domain.ProjectNode;

@Service
public class ParseService {

	public ProjectNode parse(String filePathName) throws Exception {
		FileReader fileReader = new FileReader(filePathName + File.separator + "pom.xml");
		JSONObject jsonObject = XML.toJSONObject(fileReader);
		JSONObject projectJsonObject = jsonObject.getJSONObject(ProjectConstant.PROJECT);
		return new ProjectNode(projectJsonObject, filePathName);
	}

	public Set<String> getFilePathSet(ProjectNode projectNode) {
		HashSet<String> fileNameSet = new HashSet<>();
		projectNode.getFileNameSet(fileNameSet);
		return fileNameSet;
	}
	
	public HashMap<String, ProjectNode> getModules(ProjectNode projectNode) {
		HashMap<String, ProjectNode> modulesMap = new HashMap<>();
		projectNode.getModules(modulesMap);
		return modulesMap;
	}
	
	public String treeModules(ProjectNode projectNode) {
		return projectNode.treeModules();
	}

	public String generatePackagePlantUML(PackageNode packageNode) {
		
		StringBuffer sb = new StringBuffer("");
		
		List<PackageNode> childPackageNodes = packageNode.getChildPackageNodes();
		if(CollectionUtils.isNotEmpty(childPackageNodes)) {			
			// 自己有子节点才打印自己
			sb.append(packageNode.getPlantUML());

			// 遍历自己的子节点
			childPackageNodes.stream().forEach(childPackageNode -> {
				// 如果自己的子节点有 plantUML，说明它需要打印
				if(StringUtils.isNotBlank(childPackageNode.getPlantUML()) 
						&& CollectionUtils.isNotEmpty(childPackageNode.getChildPackageNodes())) {

					// 子节点的子节点
					sb.append(childPackageNode.getPlantUML());
					
					childPackageNode.getChildPackageNodes().stream().forEach(iterm-> {
						String plantUML = generatePackagePlantUML(iterm);
						if(StringUtils.isNotBlank(plantUML)) {
							sb.append(plantUML);
						}
					});
				}
				
			});
		}
		
		return sb.toString();
	}

}
