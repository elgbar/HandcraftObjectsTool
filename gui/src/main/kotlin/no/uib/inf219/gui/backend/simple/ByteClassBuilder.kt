package no.uib.inf219.gui.backend.simple

import javafx.scene.control.TreeItem
import javafx.util.converter.ByteStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation

class ByteClassBuilder(
    initial: Byte = 0,
    key: ClassBuilder,
    parent: ParentClassBuilder,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleNumberClassBuilder<Byte>(
    Byte::class,
    initial,
    key,
    parent,
    property,
    immutable,
    ByteStringConverter(),
    item
) {}
