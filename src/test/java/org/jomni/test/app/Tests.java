package org.jomni.test.app;

import java.util.Map;

import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;

/**
 * Test utils methods
 */
public class Tests {

	static public Map makeUserMap(){
		return mapOf("id", 12, "username", "johnd", "since", "1997");
	}

	static public User makeUser(){
		User user = new User();
		user.setId(12L);
		user.setUsername("johnd");
		user.setSince(1997);
		return user;
	}

	static public void validateUser(Map userMap){
		assertEquals(Long.valueOf(12),userMap.get("id"));
		assertEquals("johnd", userMap.get("username"));
		assertEquals(Integer.valueOf(1997),userMap.get("since"));
	}

	static public void validateUser(User user){
		assertEquals(Long.valueOf(12),user.getId());
		assertEquals("johnd",user.getUsername());
		assertEquals(Integer.valueOf(1997),user.getSince());
	}
}
