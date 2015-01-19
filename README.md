## Jomni: an Object Mapper for Java 8

Jomni is a Java 8 library that makes it easy to convert objects of one type to another in Java. It is small, extensible, and written in Java 8 for Java 8.

```java
// build the immutable and threadsafe JomniMapper
JomniMapper mapper = new JomniBuilder().build();

// ------ Simple type conversion examples ------- //
// String to Long
Long longVal = mapper.as(Long.class,"1997");

// LocalDateTime to java.util.Date
LocalDateTime ldt = mapper.as(LocalDateTime.class,javaUtilDate);

// ------ Composite type conversion examples ------- //
// HashMap (name/value) to User typed object (auto type coercing when to pojos)
User user = mapper.as(User.class,userHashMap);

// User to Map
Map userMap = mapper.as(HashMap.class, user);

// ------ Java 8 Optional.map and Stream.map examples  ------- //
// With Java 8 Optional.map
User user = Optional.ofNullable(userMap).map(mapper.as(User.class)).get();

// With Java 8 Stream.map
List<User> users = userMapStream.map(mapper.as(User.class))
                                .collect(Collectors.toList());

// ------ Omni examples  ------- //
// Omni is a wrapper around either a typed object, or a map, that 
//   a) provides a single api to access name value pairs in both (POJO or Map)
//   b) automatically coerces values to the targeted type (for Pojo targets)

// Create an Omni from a new HashMap and populate it with properties from a POJO Object (User)
Map userMap = mapper.omni(new HashMap()).setAll(user).get();

// Omni offers fluid APIs
User user = mapper.omni(new User()).setAll(userMap).set("username","johnd").get();

// Can also be used to get any Composite type from any Composite type
Map userMap = mapper.omni(user).as(HashMap.class);

// Push any object into another one. 
User user = mapper.omni(userMap).into(new User());

// ------ Extending the mapper ------- //
// Overriding (or adding new) TypeConvertor at build time (with JomniBuilder)
JomniMapper mapper = new JomniBuilder()
                         .addTypeConvertor(String.class,Long.class,str -> -1L)
                         .build();
Long val = mapper.as(Long.class,"12"); // val === -1L
```

More Example:  [org.jomni.test.ReadmeTest](https://github.com/BriteSnow/jomni/blob/master/src/test/java/org/jomni/test/ReadmeTest.java)

#### Jomni key points

1. ~25kb, < 15 java classes, and depends only on Java 8.
1. Extensible TypeConverter model (utilizing the simplicity of Java 8 method reference and lambdas)
1. Provides Java 8 Function mappers ideal for Java 8 Optional and Stream .map apis. 
1. jomniMapper.omni(anyObject) wrap a POJO or Map object with a Omni object.
1. Omni provides a single api to access name value pairs in both (POJO or Map). (e.g., omni.get("username"), omni.set("username","johnd"), omni.setAll(someMap).
1. Omni setters automatically coerces values to the targeted type (for Pojo targets)
1. Omni APIs are fluid and directly change the wrapped object appropriately (do not share across thread).

*Still in development mode (i.e. 0.x version), API might change, but functionality will probably only increase.*


#### Upcoming (not yet implemented)

```java
// Advanced setter rules
User user = mapper.omni(userPart1).setAll(userPart2, SetterRule.not_nulls, SetterRule.no_override).get();
```

## Maven

Jomni is in Maven Central. So, just add the dependency, and you should be all set. 

Current version is 0.2.0 (in maven central)

```xml
<dependency>
    <groupId>org.jomni</groupId>
    <artifactId>jomni</artifactId>
    <version>0.2.0</version>
</dependency>
```

Latest Snapshot: 0.3.0-SNAPSHOT

## Why yet another java object mapper?

Interestingly enough, there are no simple, lightweight, and expressive Object Mapper in Java. They are either very old and cumbersome to use (i.e. Apache BeanUtils), or [rightfully] targeted for their own domain (e.g., Jackson Mapper).

Java 8 offers a great opportunity to rethink Java libraries and Jomni is one of the results of this rethinking. Jomni is built with the micro-library philosophy in mind, Java 8 centric, and have zero dependency beside Java 8.  
 
## Feedback 

Feedback are more than welcome, in the github issue or at jeremy.chone@gmail.com







