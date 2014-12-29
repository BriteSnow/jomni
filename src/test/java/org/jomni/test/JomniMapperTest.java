package org.jomni.test;

import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
import static org.jomni.util.Maps.mapOf;

import org.jomni.test.app.Tests;
import org.jomni.test.app.User;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class JomniMapperTest {

	@Test
	public void testMapperAsComplexType(){
		JomniMapper j = new JomniBuilder().build();

		// map to object
		User user = j.as(User.class, Tests.makeUserMap());
		Tests.validateUser(user);

		// object to map
		Map userMap = j.as(HashMap.class, Tests.makeUser());
		Tests.validateUser(userMap);
	}

	@Test
	public void testMapperFunctionMapper(){
		JomniMapper j = new JomniBuilder().build();

		Optional<User> oUser = Optional.of(Tests.makeUser());
		Map userMap = oUser.map(j.as(Map.class)).get();
		Tests.validateUser(userMap);
	}



	@Test
	public void testMapperAsSimpleType(){
		JomniMapper j = new JomniBuilder().build();

		assertEquals(Long.valueOf(12L),j.as(Long.class,"12"));
		assertEquals(Long.valueOf(12L),j.as(Long.class,12));
		assertEquals(Float.valueOf(12.12F),j.as(Float.class,12.12));
		assertEquals(Float.valueOf(12.12F),j.as(Float.class,12.12F));
		assertEquals(Float.valueOf(12F),j.as(Float.class,12L));
		assertEquals(User.Role.admin,j.as(User.Role.class,"admin"));

	}


}