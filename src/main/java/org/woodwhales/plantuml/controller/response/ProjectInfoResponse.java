package org.woodwhales.plantuml.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoResponse {
	
	// 模块视图
	private String modules;
	// 依赖关系视图
	private String relations;
	
}
