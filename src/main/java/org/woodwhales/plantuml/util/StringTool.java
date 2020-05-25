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
	
	/**
	 * 将中划线之后的字母转换成大写，首个字母不会转成大写
	 * @param str
	 * @return
	 */
	public static String upperWithOutFisrtChar(String str) {
		if(StringUtils.isBlank(str)) {
			return StringUtils.EMPTY;
		}
		
		char[] charArray = str.toCharArray();
		int length = charArray.length;
		// A-Z 对应数字65-90 a-z 对应数字97-122
		for (int i = 0; i < length; i++) {
			if (charArray[i] == '-') {
				// 如果下划线之后没有字母了，结束本次循环
				if((i + 1) == length) {
					continue;
				}
				
				// 字符在97-122之间的都是小写字母，在原基础上减32转换成大写
				if (charArray[i + 1] >= 97 && charArray[i + 1] <= 122) {
					int upper = charArray[i + 1] - 32;
					charArray[i + 1] = (char) upper;
				}
			}
		}
		
		StringBuffer result = new StringBuffer("");
		for (int i = 0; i < length; i++) {
			result.append(charArray[i]);
		}
		
		return result.toString().replace("-", "");
	}
}
