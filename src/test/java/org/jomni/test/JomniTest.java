package org.jomni.test;

import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
import org.jomni.Omni;
import org.jomni.test.app.Tests;
import org.jomni.test.app.User;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;

public class JomniTest {

	@Test
	public void testJomniSetAll(){
		JomniMapper j = new JomniBuilder().build();

		// map to object
		User user = j.omni(new User()).setAll(Tests.makeUserMap()).get();
		Tests.validateUser(user);

		// object to map
		Map userMap = j.omni(new HashMap()).setAll(Tests.makeUser()).get();
		Tests.validateUser(userMap);
	}

	@Test
	public void testJomniAsAndInto(){
		JomniMapper j = new JomniBuilder().build();

		User user = j.omni(Tests.makeUserMap()).as(User.class);
		Tests.validateUser(user);

		user = j.omni(Tests.makeUserMap()).into(User::new);
		Tests.validateUser(user);

		user = j.omni(Tests.makeUserMap()).into(() -> new User());
		Tests.validateUser(user);

		user = j.omni(Tests.makeUserMap()).into(new User());
		Tests.validateUser(user);
	}


}