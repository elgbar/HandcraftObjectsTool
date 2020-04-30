package no.uib.inf219.gui.backend.cb.simple

import javafx.scene.control.TreeItem
import javafx.util.converter.FloatStringConverter
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.SimpleNumberClassBuilder
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation

class FloatClassBuilder(
    initial: Float = 0.0f,
    key: ClassBuilder,
    parent: ParentClassBuilder,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleNumberClassBuilder<Float>(
    Float::class,
    initial,
    key,
    parent,
    property,
    immutable,
    FloatStringConverter(),
    item
) {}
