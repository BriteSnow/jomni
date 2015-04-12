package org.jomni.test;

import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
import org.junit.Test;

import java.util.Map;

import static org.jomni.util.Maps.*;
import static org.junit.Assert.assertEquals;

public class NestedAndFlattenMapTest {

	@Test
	public void nestedAndFlattenTest(){
		JomniMapper mapper = new JomniBuilder().build();

		// first flatten
		Map orginalFlattenMap = mapOf("username", "john", "company.name", "Nike", "title", "Staff", "company.info.since", 1964);

		// test asNestedMap (and nestedValue)
		Map<String,Object> nestedMap = asNestedMap(orginalFlattenMap);
		assertEquals("john", nestedMap.get("username"));
		assertEquals(1964, nestedValue(nestedMap, "company.info.since"));
		assertEquals("Nike", nestedValue(nestedMap, "company.name"));

		// test asFlatMap
		Map<String, Object> flattenMap = asFlatMap(nestedMap);
		assertEquals("john", flattenMap.get("username"));
		assertEquals(1964, flattenMap.get("company.info.since"));
		assertEquals("Nike", flattenMap.get("company.name"));
	}
}
