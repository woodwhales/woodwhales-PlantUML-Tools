package org.woodwhales.plantuml.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoRequestBody {

	private String projectFilePath;
	
	private Boolean showComponent = false;
	
}
