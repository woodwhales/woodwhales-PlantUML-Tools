package org.woodwhales.plantuml.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.woodwhales.plantuml.controller.request.ProjectInfoRequestBody;
import org.woodwhales.plantuml.controller.response.BaseVO;
import org.woodwhales.plantuml.controller.response.ProjectInfoResponse;
import org.woodwhales.plantuml.domain.PackageNode;
import org.woodwhales.plantuml.domain.ProjectNode;
import org.woodwhales.plantuml.service.ParseService;
import org.woodwhales.plantuml.service.PlantUMLService;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GenerateController {
	
	@Autowired
	private ParseService parseService;
	
	@Autowired
	private PlantUMLService plantUMLService;
	
	@PostMapping("/generate")
	public Object generate(@RequestBody ProjectInfoRequestBody projectInfoRequestBody) throws Exception {
		String filePathName = projectInfoRequestBody.getProjectFilePath();

		ProjectNode projectNode = parseService.parse(filePathName);
		
		// 获取所有的文件目录
		Set<String> set = parseService.getFilePathSet(projectNode);
		
		// 获取所有的模块
		HashMap<String, ProjectNode> moduleMap = parseService.getModules(projectNode);
		
		ProjectInfoResponse projectInfoResponse = plantUMLService.generatePlantUML(projectNode, projectInfoRequestBody.getShowComponent());
		
		log.info("{}", new ObjectMapper().writeValueAsString(set));
		List<String> collect = moduleMap.entrySet().stream().map(entry -> {
			return entry.getKey();
		}).collect(Collectors.toList());
		log.info("{}", new ObjectMapper().writeValueAsString(collect));
		log.info("{}", collect.size());

		
		PackageNode packageNode = new PackageNode(projectNode);
		log.info("{}", new ObjectMapper().writeValueAsString(new PackageNode(projectNode)));
		log.info("{}", parseService.generatePackagePlantUML(packageNode));
		
		return BaseVO.success("success", projectInfoResponse); 
	}
	

}
