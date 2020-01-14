# INF219 Project Spring 2020

This is a student project for the course [INF219](https://www.uib.no/en/course/INF219) at the university of Bergen. The project aims to make it easy to write serialized JVM objects for use where hand crafted object are needed.

## Features

By API in the feature lists below it is understood that it allows for extension of a given feature by an external unrelated party.

### Planned Features

Features of the project that will be completed before considering the project in a finished state, the order of the list is in rough order of descending importance.

* [ ] Load wanted class from given JAR(s).
* [ ] Serialize to YAML.
* [ ] Type checking for primitives.
* [ ] API for (de)serialization of object you can and cannot modify.
* [ ] API to communicate information about an object.
* [ ] Allow objects to be optional.
* [ ] Support for lists, sets and maps:
  * [ ] List
  * [ ] Set
  * [ ] Map
* [ ] Subtype selection.
  * Allowing to select a subclass of a to create.
* [ ] Loading of already serialized object and allow to continue to edit them.

### Planned Extra Features

This is a list of planned features this project _might_ have, the order is random.

* [ ] Support for annotation to other formats:
  * [ ] JSON
  * [ ] XML
* [ ] API for creating wizards for complicated objects.
  * If an object is particularly hard to write by hand it might be useful to create a wizard to allow easier configuration of said object.
* [ ] Web version.
  * A version that can be used in the browser.
  * Will be hard when using JavaFx
* [ ] Allow to specify a range a number (ie byte, int, long, float, double) can be.
* [ ] API for custom type checking on non-primitive objects.
* [ ] Easy enum selection.
* [ ] Support for name suggestion when referencing an object
* [ ] OS independent deployment.
  * A runnable jar that do not depend on the system java version.

## Fix javaFx problems

To run you need to have JDK 8 with support for JavaFx. 

Here are two ways to get it:

1. Use Oracle JDK 8, [found here](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (you need to login to download)
2. Use ZuluFx, [found here](https://www.azul.com/downloads/zulu-community/)

In IntelliJ add the new JDK and use it as the project jdk

