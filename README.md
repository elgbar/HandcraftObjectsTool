# Handcraft Objects Tool

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.com/kh498/HandcraftObjectsTool.svg?branch=master)](https://travis-ci.com/kh498/HandcraftObjectsTool)

Handcraft Objects Tool is a graphical user interface tool to create serialized objects using [Jackson](https://github.com/FasterXML/jackson) as the current backend. The tool aims to make it easy to write serialized JVM objects for use where hand crafted object are needed.

### INF219 Project Spring 2020

This is a student project for the course [Project in informatics I](https://www.uib.no/en/course/INF219) at the university of Bergen.

## Preview Pictures

<details> <summary>View Preview Pictures</summary>

![Choose a class to edit](preview/HOT-selClass2.png)
![Abstract type with a reference to the root object](preview/HOT-EditResponse-AbstractType.png)
![Object is successfully serialized](preview/HOT-serialized.png)

</details>

## Usage

To create serialized objects HOT needs to know the class' signatures. Currently, the only way of creating custom objects are by loading a [fat/über jar](https://stackoverflow.com/a/29925421) with the _Import jar_ and _Import jars_ buttons within the interface. You can also permanently load jars when placed in the application home folder found at `~/.hot/jars` for Unix and `%HOMEDRIVE%%HOMEPATH%\.hot\jars` (i.e. `C:\Users\<CurrentUserName>\.hot\jars`) for Windows.

You then choose what class you want to create either by pressing the `choose class` button or by manually writing the canonical class name in the text field.

You can now choose if you want to create a new object or load an already serialized object from disk. If any errors occur when loading the object see [Using a custom Object Mapper](#using-a-custom-object-mapper) below for a potential fix, and remember to actually load the jar.

To save and or verify the created object use the shortcut `ctrl+S` to save and `ctrl+V` to verify what your object serializes to.

### Using a custom Object Mapper

When loading a jar you can specify your own Object Mapper to be used with serialization. To do this create a file named `.hot` in the root directory of the jar (ie. root folder of your resources folder if using gradle/maven). Within this file you need to specify the full class name of the file which contains the __static__ field which contains the `ObjectMapper`. The second line contains the name of the field, it is optional and defaults to `mapper`.

The code that handles this can be found at [`no.uib.inf219.gui.loader.ObjectMapperLoader`](https://github.com/kh498/HandcraftObjectsTool/blob/master/gui/src/main/kotlin/no/uib/inf219/gui/loader/ObjectMapperLoader.kt)

#### Example content of `.hot`

Note that this example contains that optional second line with the name of the field.

```text
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
    private static com.fasterxml.jackson.databind.ObjectMapper objMapper;

    static {
        //statically create the object mapper
        objMapper = new ObjectMapper();
        objMapper.registerModule(SomeModule());
    }
}
```

## Fix JavaFx problems

To run you need to have JDK 8 with support for JavaFx.

Here are two ways to get it:

1. Use Oracle JDK 8, [found here](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (you need to login to download)
2. Use ZuluFx, [found here](https://www.azul.com/downloads/zulu-community/)

In IntelliJ add the new JDK and use it as the project jdk

## Features

By API in the feature lists below it should be understood that it allows for extension of a given feature by an external third party.

### List of Features

Features of the project that will be completed before considering the project in a finished state, the order of the list is in rough order of descending importance.

* [x] Load wanted class from given JAR(s).
* [x] Serialize to YAML.
  * _See [jacksons text module](https://github.com/FasterXML/jackson-dataformats-text)_
* [x] Type checking for primitives.
* [x] API for (de)serialization of object you can and cannot modify.
  * _Jackson provides this with MixIn annotations, they can be loaded via custom object mappers_
* [x] API to communicate information about how to serialize/deserialize different objects.
  * _This can be achieved with jackson modules, see [Using a custom Object Mapper](#using-a-custom-object-mapper)_
* [x] Allow objects to be optional.
  * _See [Jacksons JsonProperty](https://github.com/FasterXML/jackson-annotations/blob/c0d00657a17727f3aed50c0b2deb9afa2e89f6f4/src/main/java/com/fasterxml/jackson/annotation/JsonProperty.java#L60-L78)_
* [x] Support for lists, sets and maps
  * [x] List
  * [x] Set
  * [x] Map
* [x] Subtype selection.
  * Allowing to select a subclass of a to create.
* [ ] Loading of already serialized objects and continuing to editing them.
  * This is partially complete. Everything except references can be read from serialized object, but due to how it currently implemented, references are lost during reading. A warning will be shown if any references are detected.

### Planned Extra Features

This is a list of planned features this project _might_ have, the order is random.

* [x] Support for annotation to other formats:
  * [x] JSON
  * [x] XML
  * _See [jacksons text module](https://github.com/FasterXML/jackson-dataformats-text)_
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
