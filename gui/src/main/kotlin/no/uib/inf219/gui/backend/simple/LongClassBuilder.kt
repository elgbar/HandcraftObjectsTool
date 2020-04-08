package no.uib.inf219.gui.backend.simple

import javafx.scene.control.TreeItem
import javafx.util.converter.LongStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation

class LongClassBuilder(
    initial: Long = 0,
    key: ClassBuilder,
    parent: ParentClassBuilder,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleNumberClassBuilder<Long>(
    Long::class,
    initial,
    key,
    parent,
    property,
    immutable,
    LongStringConverter(),
    item
) {}
