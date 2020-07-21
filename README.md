# Handcraft Objects Tool

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.com/kh498/HandcraftObjectsTool.svg?branch=master)](https://travis-ci.com/kh498/HandcraftObjectsTool)

Handcraft Objects Tool is a graphical user interface tool to create serialized objects using [Jackson](https://github.com/FasterXML/jackson) as the current backend. The tool aims to make it easy to write serialized JVM objects for use where handcrafted object are needed.

### INF219 Project Spring 2020

This was a student project for the course [Project in informatics I](https://www.uib.no/en/course/INF219) at the university of Bergen. See [academic readme](./academic/README.md) for more information, the white paper, and presentation.

## Preview Pictures

<details> <summary>View Preview Pictures</summary>

![Choose a class to edit](preview/HOT-selClass2.png)
![Abstract type with a reference to the root object](preview/HOT-EditResponse-AbstractType.png)
![Object is successfully serialized](preview/HOT-serialized.png)

</details>

## Usage

To create serialized objects HOT needs to know the class' signatures. Currently, the only way of creating custom objects are by loading a [fat/uber jar](https://stackoverflow.com/a/29925421) with the _Import jar_ and _Import jars_ buttons within the interface. You can also permanently load jars when placed in the application home folder found at `~/.hot/jars` for Unix and `%HOMEDRIVE%%HOMEPATH%\.hot\jars` (i.e. `C:\Users\<CurrentUserName>\.hot\jars`) for Windows.

You then choose what class you want to create either by pressing the `choose class` button or by manually writing the canonical class name in the text field.

You can now choose if you want to create a new object or load an already serialized object from disk. If any errors occur when loading the object see [Using a custom Object Mapper](#using-a-custom-object-mapper) below for a potential fix, and remember to actually load the jar.

To save and or verify the created object use the shortcut `ctrl+S` to save and `ctrl+D` to verify what your object serializes to.

### Using a custom Object Mapper

When loading a jar you can specify your own Object Mapper to be used with serialization. To do this create a file named `.hot` in the root directory of the jar (ie. root folder of your resources folder if using gradle/maven). Within this file you need to specify the full class name of the file which contains the __static__ field which contains the `ObjectMapper`. The second line contains the name of the field, it is optional and defaults to `mapper`.

The code that handles this can be found at [`no.uib.inf219.gui.loader.ObjectMapperLoader`](https://github.com/kh498/HandcraftObjectsTool/blob/master/gui/src/main/kotlin/no/uib/inf219/gui/loader/ObjectMapperLoader.kt)

#### Example content of `.hot`

Note that this example contains that optional second line with the name of the field.

```text
no.example.SomeClass
objMapper
```

The class `SomeClass` would then be something like for java

<details> <summary>Java example for custom ObjectMapper</summary>

```java
package no.example;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SomeClass{

    //visibility of field does not matter
    // but the field MUST be static!
    // this field have to be named 'mapper' if no second line is present in '.hot'
    private static ObjectMapper objMapper;

    static {
        //statically create the object mapper
        objMapper = new ObjectMapper();
        objMapper.registerModule(SomeModule());
    }
}
```
</details>

<details> <summary>Kotlin object example for custom ObjectMapper</summary>

```kotlin
package no.example

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object SomeClass {

  @JvmStatic
  val mapper = jacksonObjectMapper().also {
    it.registerModule(SomeModule())
  }
}
```
</details>

<details> <summary>Kotlin companion object example for custom ObjectMapper</summary>

```kotlin
package no.example

import com.fasterxml.jackson.databind.ObjectMapper

class SomeClass {

    companion object {
        //visibility of field does not matter
        // but the field MUST be static!
        // this field have to be named 'mapper' if no second line is present in '.hot'
        @JvmStatic
        private var objMapper = ObjectMapper().also {
            it.registerModule(SomeModule())
        }
    }
}
```
</details>

## FAQ

* Q: I get the exception `Error: Could not find or load main class no.uib.inf219.gui.GuiMain`
    * Example exception when running JRE 14  
    ```
    Error: Could not find or load main class no.uib.inf219.gui.GuiMain
    Caused by: java.lang.NoClassDefFoundError: javafx/application/Application
    ``` 
    * Example exception when running JRE 8 without JavaFx
    ```
    Error: Could not find or load main class no.uib.inf219.gui.GuiMain
    ```
* A: You are running a jre without javafx. See [Installing a JRE with JavaFx 8 available](#installing-a-jre-with-javafx-8-available)
---
* Q: I cannot choose a class. The exception `java.lang.NoClassDefFoundError: com/sun/javafx/scene/control/skin/TableColumnHeader` is thrown!
* A: You are probably using too new a JavaFx version. TornadoFx only supports javaFx for java 8. See [Installing a JRE with JavaFx 8 available](#installing-a-jre-with-javafx-8-available)

## Installing a JRE with JavaFx 8 available

To run you need to have a JRE 8 that supports JavaFx.

Here are several ways to get it:

1. Use Oracle JDK 8, [found here](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (you need to login to download)
2. Use ZuluFx, [jdk found here](https://www.azul.com/downloads/zulu-community/?version=java-8-lts&architecture=x86-64-bit&package=jdk-fx), [and jre here](https://www.azul.com/downloads/zulu-community/?version=java-8-lts&architecture=x86-64-bit&package=jre-fx)
3. Use a tool like [sdkman](https://sdkman.io/) that will manage all different jdks and jres for you.
   * Look for a java version with `fx` suffix
   * Example install commands:
        ```bash
        $ curl -s "https://get.sdkman.io" | bash #Install sdk man
        $ sdk list java #List all available java versions 
        $ sdk install java 8.0.252.fx-zulu #Install newest (at writing) jdk 8 with javaFx
        ```

In IntelliJ add the new JDK and use it as the project jdk

## Features

By API in the feature lists below it should be understood that it allows for extension of a given feature by an external third party.

### List of Features

Features of the project that will be completed before considering the project in a finished state, the order of the list is in rough order of descending importance.

* [x] Load wanted class from given JAR(s).
* [x] Serialize to YAML.
  * _See [Jacksons text module](https://github.com/FasterXML/jackson-dataformats-text)_
* [x] Type checking for primitives.
* [x] API for (de)serialization of object you can and cannot modify.
  * _Jackson provides this with MixIn annotations, they can be loaded via custom object mappers_
* [x] API to communicate information about how to serialize/deserialize different objects.
  * _This can be achieved with Jackson modules, see [Using a custom Object Mapper](#using-a-custom-object-mapper)_
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
  * _See [Jacksons text module](https://github.com/FasterXML/Jackson-dataformats-text)_
* [ ] API for creating wizards for complicated objects.
  * If an object is particularly hard to write by hand it might be useful to create a wizard to allow easier configuration of said object.
* [ ] Web that can run in the browser.
  * Will be hard due to JavaFx.
* [ ] Allow to specify a range a number (ie byte, int, long, float, double) can be.
* [x] API for custom type checking on non-primitive objects.
    * _This is handled with Jackson_
* [x] Easy enum selection.
* [ ] ~~Support for name suggestion when referencing an object.~~
    * _This is not longer applicable, the reference system is gui based._
* [ ] OS independent deployment.
  * A runnable jar that do not depend on the system java version.
