package org.woodwhales.plantuml.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.woodwhales.plantuml.constants.CommonConstant;
import org.woodwhales.plantuml.controller.response.ProjectInfoResponse;
import org.woodwhales.plantuml.domain.ProjectNode;

@Service
public class PlantUMLService {
	
	private static final String BR = "\n";
	
	public ProjectInfoResponse generatePlantUML(ProjectNode projectNode, Boolean isShowComponent) {
StringBuffer sb = new StringBuffer("");
		
		HashMap<String, ProjectNode> map = new HashMap<>();
		
		sb.append("@startuml").append(CommonConstant.BR);
		
		if(projectNode.isParentPom()) {
			sb.append(projectNode.getPlantUML(map, isShowComponent));
		}
		
		sb.append(CommonConstant.BR).append("@enduml");
		
		// 模块视图
		String modules = sb.toString();
		
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("@startuml").append(CommonConstant.BR);
		map.entrySet().forEach(entry -> {
			ProjectNode projectNode_ = entry.getValue();
			
			if(!projectNode_.isParentPom()) {
				List<String> refrenceModuleNames = projectNode_.getRefrenceModuleNames(map);
				stringBuffer.append(projectNode_.getRefrencePlantUML(refrenceModuleNames));
			}
			
		});
		stringBuffer.append(CommonConstant.BR).append("@enduml");
		
		// 依赖关系视图
		String relations = stringBuffer.toString();
		
		return new ProjectInfoResponse(modules, relations);
	}
}
