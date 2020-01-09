# INF219 Project Spring 2020

This is a student project for the course [INF219](https://www.uib.no/en/course/INF219) at the university of Bergen. The project aims to make it easy to write serialized JVM objects for use where hand crafted object are needed.

## Features

By API in the feature lists below it is understood that it allows for extension of a given feature by an external unrelated party.

### Planned Features

Features of the project that will be completed before considering the project in a finished state, the order of the list is in rough order of descending importance.

* [ ] Load wanted class from given JAR(s).
* [ ] Serialize to YAML.
* [ ] Type checking for primitives.
* [ ] API for (de)serialization of object you can and can not modify.
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

* [ ] Support for serialization to other formats:
  * [ ] JSON
  * [ ] XML
* [ ] API for creating wizards for complicated objects.
  * If an object is particularly hard to write by hand it might be useful to create a wizard to allow easier configuration of said object.
* [ ] Web version.
  * A version that can be used in the browser.
* [ ] Allow to specify a range a number (ie byte, int, long, float, double) can be.
* [ ] API for custom type checking on non-primitive objects.
* [ ] Easy enum selection
* [ ] Support for name suggestion when referencing an object
* [ ] OS independent deployment.
  * A runnable jar that do not depend on the system java version.
