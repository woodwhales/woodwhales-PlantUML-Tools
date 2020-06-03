package org.woodwhales.plantuml.controller;

import java.util.Set;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		
		Set<String> set = parseService.getFilePathSet(projectNode);
		
		ProjectInfoResponse projectInfoResponse = plantUMLService.generatePlantUML(projectNode, projectInfoRequestBody.getShowComponent());
		
		log.info("{}", new ObjectMapper().writeValueAsString(set));
		return BaseVO.success("success", projectInfoResponse); 
	}
	
}
