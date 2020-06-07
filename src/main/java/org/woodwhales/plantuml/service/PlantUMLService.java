package org.woodwhales.plantuml.service;

import static org.woodwhales.plantuml.constants.CommonConstant.BR;
import static org.woodwhales.plantuml.constants.CommonConstant.ENDUML;
import static org.woodwhales.plantuml.constants.CommonConstant.STARTUML;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.woodwhales.plantuml.controller.response.ProjectInfoResponse;
import org.woodwhales.plantuml.domain.ProjectNode;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PlantUMLService {
	
	public ProjectInfoResponse generatePlantUML(ProjectNode projectNode, Boolean isShowComponent) {
		StringBuffer sb = new StringBuffer("");
		
		HashMap<String, ProjectNode> componentMap = new HashMap<>();
		
		HashMap<String, ProjectNode> parentProjectMap = new HashMap<>();
		
		sb.append(STARTUML).append(BR);
		
		if(projectNode.isParentPom()) {
			sb.append(projectNode.getPlantUML(componentMap, parentProjectMap, isShowComponent));
			
			// sb.append(projectNode.getPorjectRelationsPlantUML(parentProjectMap));
			sb.append(BR);
		}
		
		
		String relationStr = generteRelationsPlantUML(componentMap);
		sb.append(relationStr);
		
		sb.append(ENDUML);
		
		// 模块视图
		String modules = sb.toString();
		
		String relations = new StringBuffer().append(STARTUML).append(BR).append(relationStr).append(ENDUML).toString();
		log.info("modules = {}", componentMap.size());
		return new ProjectInfoResponse(modules, relations);
	}

	private String generteRelationsPlantUML(HashMap<String, ProjectNode> componentMap) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(BR);
		componentMap.entrySet().forEach(entry -> {
			ProjectNode projectNode_ = entry.getValue();
			
			if(!projectNode_.isParentPom()) {
				List<String> refrenceModuleNames = projectNode_.getRefrenceModuleNames(componentMap);
				stringBuffer.append(projectNode_.getRefrencePlantUML(refrenceModuleNames));
			}
			
		});
		stringBuffer.append(BR);
		
		// 依赖关系视图
		String relations = stringBuffer.toString();
		return relations;
	}
	
}
