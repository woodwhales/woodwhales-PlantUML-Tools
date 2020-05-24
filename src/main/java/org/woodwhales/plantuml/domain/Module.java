package org.woodwhales.plantuml.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Module {
	
	private String moduleName;

	public static Module parse(String module) {
		return new Module(module);
	}
	
}
