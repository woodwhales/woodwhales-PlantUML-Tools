package org.woodwhales.plantuml.util;

import org.apache.commons.lang3.StringUtils;

public class StringTool {
	
	private static final String prefix = "${";
	private static final String suffix = "}";

	public static boolean isVersionKey(String version) {
		if(StringUtils.isBlank(version)) {
			return false;
		}
		
		if(StringUtils.startsWith(version, prefix) && StringUtils.endsWith(version, suffix)) {
			return true;
		}
		
		return false;
	}
	
	public static String getVersionKey(String version) {
		if(StringUtils.isBlank(version)) {
			return StringUtils.EMPTY;
		}
		
		if(isVersionKey(version)) {
			return StringUtils.substringBetween(version, prefix, suffix);
		}
		
		return version;
	}
}
