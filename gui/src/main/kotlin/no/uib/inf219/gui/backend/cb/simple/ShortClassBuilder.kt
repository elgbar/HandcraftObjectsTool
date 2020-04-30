package no.uib.inf219.gui.backend.cb.simple

import javafx.scene.control.TreeItem
import javafx.util.converter.ShortStringConverter
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.SimpleNumberClassBuilder
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation

class ShortClassBuilder(
    initial: Short = 0,
    key: ClassBuilder,
    parent: ParentClassBuilder,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleNumberClassBuilder<Short>(
    Short::class,
    initial,
    key,
    parent,
    property,
    immutable,
    ShortStringConverter(),
    item
) {}
