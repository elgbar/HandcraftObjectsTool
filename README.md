# INF219 Project Spring 2020

This is a student project for the course [INF219](https://www.uib.no/en/course/INF219) at the university of Bergen. The project aims to make it easy to write serialized JVM objects for use where hand crafted object are needed. [Jackson](https://github.com/FasterXML/jackson) is used to handle the heavy lifting with (de)serialization, originally snake yaml [Snake YAML](https://bitbucket.org/asomov/snakeyaml-engine/src/master/) was planned to be used.


## Features

By API in the feature lists below it is understood that it allows for extension of a given feature by an external unrelated party.

### List of Features

Features of the project that will be completed before considering the project in a finished state, the order of the list is in rough order of descending importance.

* [x] Load wanted class from given JAR(s).
* [x] Serialize to YAML.
* [x] Type checking for primitives.
* [x] API for (de)serialization of object you can and cannot modify.
    * _Jackson provides this with MixIn annotations, they can be loaded via custom object mappers_
* [x] API to communicate information about an object.
* [x] Allow objects to be optional.
* [x] Support for lists, sets and maps:
  * [x] List
  * [x] Set
  * [x] Map
* [x] Subtype selection.
  * Allowing to select a subclass of a to create.
* [ ] Loading of already serialized object and allow to continue to edit them.

### Planned Extra Features

This is a list of planned features this project _might_ have, the order is random.

* [x] Support for annotation to other formats:
  * [x] JSON
  * [x] XML
* [ ] API for creating wizards for complicated objects.
  * If an object is particularly hard to write by hand it might be useful to create a wizard to allow easier configuration of said object.
* [ ] Web version.
  * A version that can be used in the browser.
  * Will be hard when using JavaFx
* [ ] Allow to specify a range a number (ie byte, int, long, float, double) can be.
* [ ] API for custom type checking on non-primitive objects.
* [x] Easy enum selection.
* [ ] Support for name suggestion when referencing an object
* [ ] OS independent deployment.
  * A runnable jar that do not depend on the system java version.

## Using a custom Object Mapper

When loading a jar you can specify your own Object Mapper to be used with serialization. To do this create a file named `.hot` in the root directory of the jar (ie. root folder of your resources folder if using gradle/maven). Within this file you need to specify the full class name of the file which contains the __static__ field which contains the `ObjectMapper`. The second line contains the name of the field, it is optional and defaults to `mapper`.

The code that handles this can be found at `no.uib.inf219.gui.loader.ObjectMapperLoader`

### Example content of `.hot`

Note that this example contains that optional second line with the name of the field. 

```
no.example.JavaClass
objMapper
```

The class `JavaClass` would then be something like

```java
package no.example;

public class JavaClass{

    //visibility of field does not matter
    // but the field MUST be static!
    // this field have to be named 'mapper' if no second line is present in '.hot'
    private static ObjectMapper objMapper;

    static {
        //statically create the object mapper 
        objMapper = ObjectMapper();
        objMapper.registerModule(SomeModule());
    }
}
```

## Fix javaFx problems

To run you need to have JDK 8 with support for JavaFx. 

Here are two ways to get it:

1. Use Oracle JDK 8, [found here](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (you need to login to download)
2. Use ZuluFx, [found here](https://www.azul.com/downloads/zulu-community/)

In IntelliJ add the new JDK and use it as the project jdk

