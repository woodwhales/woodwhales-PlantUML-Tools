package org.woodwhales.plantuml.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.woodwhales.plantuml.controller.request.ProjectInfoRequestBody;
import org.woodwhales.plantuml.controller.response.BaseVO;
import org.woodwhales.plantuml.controller.response.ProjectInfoResponse;
import org.woodwhales.plantuml.domain.ProjectNode;
import org.woodwhales.plantuml.service.ParseService;
import org.woodwhales.plantuml.service.PlantUMLService;

@RestController
public class GenerteController {
	
	@Autowired
	private ParseService parseService;
	
	@Autowired
	private PlantUMLService plantUMLService;
	
	@PostMapping("/generate")
	public Object generte(@RequestBody ProjectInfoRequestBody projectInfoRequestBody) throws Exception {
		String filePathName = projectInfoRequestBody.getProjectFilePath();

		ProjectNode projectNode = parseService.parse(filePathName);
		
		ProjectInfoResponse projectInfoResponse = plantUMLService.generatePlantUML(projectNode, projectInfoRequestBody.getShowComponent());
		
		return BaseVO.success("success", projectInfoResponse); 
	}
	
}
