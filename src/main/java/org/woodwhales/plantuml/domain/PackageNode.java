package org.woodwhales.plantuml.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.woodwhales.plantuml.util.StringTool;

import lombok.Data;

@Data
public class PackageNode {

	private String name;
	
	private PackageNode parentPackageNode;
	
	private List<PackageNode> childPackageNodes;
	
	private String plantUML;

	public PackageNode(ProjectNode projectNode) {
		if(projectNode.isParentPom()) {
			this.name = packageName(projectNode.getArtifactId());
			List<ProjectNode> childProjectNodes = projectNode.getChildProjectNodes();
			
			// 设置所有子节点
			this.childPackageNodes = getChildPackageNodes(childProjectNodes);
			
			if(CollectionUtils.isNotEmpty(childPackageNodes)) {
				StringBuffer sb = new StringBuffer();
				this.childPackageNodes.stream().forEach(childPackageNode -> {
					if(CollectionUtils.isEmpty(childPackageNode.getChildPackageNodes())) {
						sb.append(String.format("%s -down-> %s", this.name, childPackageNode.getName()));
						sb.append("\n");
					}
					
				});
				this.plantUML = sb.toString();
			}
		}
	}

	public PackageNode(String artifactId) {
		this.name = packageName(artifactId);
	}

	private List<PackageNode> getChildPackageNodes(List<ProjectNode> childProjectNodes) {
		if(CollectionUtils.isNotEmpty(childProjectNodes)) {
			List<PackageNode> childPackageNodes = new ArrayList<>();
			childProjectNodes.stream().forEach(childProjectNode -> {
				if(childProjectNode.isParentPom()) {
					PackageNode childPackageNode = new PackageNode(childProjectNode);
					// 设置 子节点的所有子节点
					childPackageNode.setChildPackageNodes(getChildPackageNodes(childProjectNode.getChildProjectNodes()));
					childPackageNodes.add(childPackageNode);
				}
			});
			return childPackageNodes;
		}
		
		return Collections.emptyList();
	}
	
	private String packageName(String artifactId) {
		return StringTool.upperWithOutFisrtChar(artifactId + "-module");
	}
	
}
