## Jomni: an Object Mapper for Java 8

```java
// build the immutable and threadsafe JomniMapper
JomniMapper mapper = new JomniBuilder().build();

// Simple Type Conversions
Long longVal = mapper.as(Long.class,"1997");
LocalDateTime ldt = mapper.as(LocalDateTime.class,javaUtilDate);

// Complex Type Converstion using mapper
User user = mapper.as(User.class,userHashMap);

// With Java 8 Optional.map
User user = Optional.ofNullable(userMap).map(mapper.as(User.class)).get();

// With Java 8 Stream.map
List<User> users = userMapStream.map(mapper.as(User.class)).collect(Collectors.toList());

// Wrap any object (Map or Pojo) into a Omni for normalized property access
// and automatic type conversion (with fluid apis).
Map userMap = mapper.omni(new HashMap()).setAll(user).get();

// Omni is fluid
User user = mapper.omni(new User()).setAll(userMap).set("username","johnd").get();

// Transform any object to anything
Map userMap = mapper.omni(user).as(HashMap.class);

// Push any object into another one. 
User user = mapper.omni(userMap).into(User::new);

// Overriding (or adding new) TypeConvertor at build time (with JomniBuilder)
JomniMapper mapper = new JomniBuilder()
                         .addTypeConvertor(String.class,Long.class,str -> -1L)
                         .build();
Long val = mapper.as(Long.class,"12"); // val === -1L
```

More Example:  [org.jomni.test.ReadmeTest](https://github.com/BriteSnow/jomni/blob/master/src/test/java/org/jomni/test/ReadmeTest.java)

#### Jomni key points

1. ~25kb, < 15 java classes, and depends only on Java 8
1. Extensible TypeConverter model (utilizing the simplicity of Java 8 method reference and lambdas)
1. Provides Java 8 Function mappers ideal for Java 8 Optional and Stream .map apis. 
1. jomniMapper.omni(anyObject) wrap a POJO or Map object with a Omni object.
1. Omni normalizes and extends the properties access and type conversion (setters, getters, and property list) of its wrapped instance (Pojo or Map)
1. Omni APIs are fluid and directly change the wrapped object appropriately (do not share across thread)

*Still in development mode (i.e. 0.x version), API might change, but functionality will probably only increase.*


#### Upcoming (not yet implemented)

```java
// Advanced setter rules
User user = mapper.omni(userPart1).setAll(userPart2, SetterRule.not_nulls, SetterRule.no_override).get();
```

## Maven

Not yet on Maven Central, but will be there soon. Until now, you can setup the britesnow.com nexus repository

```xml
<repositories>  
    <!-- for jomni -->
    <repository>
        <id>BriteSnow Releases</id>
        <url>http://nexus.britesnow.com/nexus/content/repositories/releases/</url>
    </repository>
    <repository>
        <id>BriteSnow Snapshots</id>
        <url>http://nexus.britesnow.com/nexus/content/repositories/snapshots/</url>
    </repository>   
    <!-- /for jomni -->
</repositories>
```

Current version is 0.2.0-SNAPSHOT (still under development, API may change)
```xml
<dependency>
    <groupId>org.jomni</groupId>
    <artifactId>jomni</artifactId>
    <version>0.2.0-SNAPSHOT</version>
</dependency>
```   

## Why yet another java object mapper?

Interestingly enough, there are no simple, lightweight, and expressive Object Mapper in Java. They are either very old and cumbersome to use (i.e. Apache BeanUtils), or [rightfully] targeted for their own domain (e.g., Jackson Mapper).

Java 8 offers a great opportunity to rethink Java libraries and Jomni is one of the results of this rethinking. Jomni is built with the micro-library philosophy in mind, Java 8 centric, and have zero dependency beside Java 8.  
 
## Feedback 

Feedback are more than welcome, in the github issue or at jeremy.chone@gmail.com







