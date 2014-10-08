package org.jomni.test;

import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
import org.jomni.Omni;
import org.jomni.test.app.User;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;

/**
 * Those are just test for the sake of demonstration.
 */
public class ReadmeTest {

	@Test
	public void mapperSimpleTypes(){
		JomniMapper mapper = new JomniBuilder().build();

		// String to Long
		Long longVal = mapper.as(Long.class,"1997");
		assertEquals(1997L, longVal.longValue());

		// Dates
		LocalDateTime ldtOrig = LocalDateTime.now();
		Date date = mapper.as(Date.class, ldtOrig);
		LocalDateTime ldt = mapper.as(LocalDateTime.class,date);
		assertEquals(ldtOrig,ldt);
	}


	@Test
	public void omniSetAllMapToPojo(){
		JomniMapper mapper = new JomniBuilder().build();

		Map userMap = mapOf("username", "johnd", "since", "1997");
		// Wrap any object into an omni (Pojo or Map)
		Omni<User> omni = mapper.omni(new User());
		// call setAll or set(name,value) to set property values
		omni.setAll(userMap);
		// get the wrapped object
		User user = omni.get();
		assertEquals(1997,user.getSince().intValue());

		// Omni is fluid, so, the one liner would have been:
		user = mapper.omni(new User()).setAll(userMap).get();
	}

	@Test
	public void omniAsMapPojoToMap(){
		JomniMapper mapper = new JomniBuilder().build();

		User user = new User();
		user.setUsername("johnd");
		user.setSince(1997);

		Omni<User> omni = mapper.omni(user);
		Map map = omni.asMap();
		assertEquals(1997,map.get("since"));
	}

	@Test
	public void mapperWithOptionalFunctionMapper(){
		JomniMapper mapper = new JomniBuilder().build();

		Map userMap = mapOf("id",12L,"username","johnd","since",1997);
		User user = Optional.of(userMap).map(mapper.as(User.class)).get();
		assertEquals(12L, user.getId().longValue());
	}


	@Test
	public void mapperWithStreamFunctionMapper(){
		JomniMapper mapper = new JomniBuilder().build();

		List<Map> userMaps = new ArrayList<>();
		userMaps.add(mapOf("id",12L,"username","johnd","since",1997));
		userMaps.add(mapOf("id",13L,"username","jenw","since",2005));

		List<User> users = userMaps.stream().map(mapper.as(User.class)).collect(toList());
		assertEquals(12L, users.get(0).getId().longValue());
		assertEquals(13L, users.get(1).getId().longValue());
	}

	@Test
	public void mapperPojoToMap(){
		JomniMapper mapper = new JomniBuilder().build();

		User user = new User();
		user.setUsername("johnd");
		user.setSince(1997);

		Map userMap = mapper.as(HashMap.class,user); // or use shorthand: mapper.asMap(user)
		assertEquals("johnd",userMap.get("username"));
		assertEquals(1997,userMap.get("since"));
	}

	@Test
	public void mapperMapToPojo(){
		JomniMapper mapper = new JomniBuilder().build();

		Map userMap = mapOf("username", "johnd", "since", "1997");
		User user = mapper.as(User.class,userMap);
		assertEquals("johnd",user.getUsername());
		assertEquals(1997,user.getSince().intValue());
	}

	@Test
	public void mapperCustomConverter(){
		JomniBuilder builder = new JomniBuilder();
		// type converter than converter any string to -1L
		builder.addTypeConverter(String.class, Long.class, str -> -1L);
		JomniMapper mapper = builder.build();

		// should always transform to -1L
		assertEquals(-1L, mapper.as(Long.class, "12").longValue());

		// but this should still work as expected.
		assertEquals(12,mapper.as(Integer.class,"12").intValue());

	}

}
