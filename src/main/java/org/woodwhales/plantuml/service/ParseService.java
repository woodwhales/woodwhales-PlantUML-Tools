package org.woodwhales.plantuml.service;

import java.io.File;
import java.io.FileReader;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;
import org.woodwhales.plantuml.constants.ProjectConstant;
import org.woodwhales.plantuml.domain.ProjectNode;

@Service
public class ParseService {

	public ProjectNode parse(String filePathName) throws Exception {
		FileReader fileReader = new FileReader(filePathName + File.separator + "pom.xml");
		JSONObject jsonObject = XML.toJSONObject(fileReader);
		JSONObject projectJsonObject = jsonObject.getJSONObject(ProjectConstant.PROJECT);
		return new ProjectNode(projectJsonObject, filePathName);
	}
}