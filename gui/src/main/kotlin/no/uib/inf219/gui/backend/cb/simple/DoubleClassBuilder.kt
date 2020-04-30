package no.uib.inf219.gui.backend.cb.simple

import javafx.scene.control.TreeItem
import javafx.util.converter.DoubleStringConverter
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.SimpleNumberClassBuilder
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation

class DoubleClassBuilder(
    initial: Double = 0.0,
    key: ClassBuilder,
    parent: ParentClassBuilder,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleNumberClassBuilder<Double>(
    Double::class,
    initial,
    key,
    parent,
    property,
    immutable,
    DoubleStringConverter(),
    item
) {}
