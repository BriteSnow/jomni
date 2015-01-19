package org.jomni.test;

import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
import org.jomni.Omni;
import org.jomni.test.app.Tests;
import org.jomni.test.app.User;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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


	@Test
	public void testLocalDateTime(){
		JomniMapper j = new JomniBuilder().build();

		String strSample = "2014-12-29T15:29:46";
		LocalDateTime ldt = LocalDateTime.of(2014,12,29,15,29,46);
		String str = j.as(String.class,ldt);
		assertEquals(strSample,str);

		LocalDateTime ldtFromStr = j.as(LocalDateTime.class,strSample);
		assertEquals(ldt,ldtFromStr);
	}

	@Test
	public void testNullVal() {
		JomniMapper j = new JomniBuilder().build();

		Long num = j.as(Long.class, null);
		assertNull(num);
	}

}