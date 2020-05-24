package org.woodwhales.plantuml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.woodwhales.plantuml.util.StringTool;

public class StringToolTest {

	@Test
	public void test() {
		String version = "${java.version}";
		
		boolean isVersionKey = StringTool.isVersionKey(version);
		assertEquals(true, isVersionKey);
		
		String versionKey = StringTool.getVersionKey(version);
		assertEquals("java.version", versionKey);
		
	}
}
