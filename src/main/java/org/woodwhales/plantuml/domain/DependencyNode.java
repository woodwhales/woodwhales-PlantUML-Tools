package org.woodwhales.plantuml.domain;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DependencyNode {

	private String groupId;
	private String artifactId;
	private String version;
	private String optional;
	private String scope;
	
	public static DependencyNode parse(JSONObject jsonObject) {
		String groupId = jsonObject.getString("groupId");
		String artifactId = jsonObject.getString("artifactId");
		String version = null;
		String optional = null;
		String scope = null;
		try {
			version = jsonObject.getString("version");
		} catch (Exception e) {
			version = StringUtils.EMPTY;
		}
		
		try {
			optional = jsonObject.getString("optional");
		} catch (JSONException e) {
			optional = StringUtils.EMPTY;
		}
		
		try {
			scope = jsonObject.getString("scope");
		} catch (JSONException e) {
			scope = StringUtils.EMPTY;
		}
		
		return new DependencyNode(groupId, artifactId, version, optional, scope);
	}
	
}
