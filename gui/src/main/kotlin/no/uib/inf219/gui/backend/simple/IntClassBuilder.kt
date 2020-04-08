package no.uib.inf219.gui.backend.simple

import javafx.scene.control.TreeItem
import javafx.util.converter.IntegerStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation

class IntClassBuilder(
    initial: Int = 0,
    key: ClassBuilder?,
    parent: ParentClassBuilder?,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleNumberClassBuilder<Int>(
    Int::class,
    initial,
    key,
    parent,
    property,
    immutable,
    IntegerStringConverter(),
    item
) {}
