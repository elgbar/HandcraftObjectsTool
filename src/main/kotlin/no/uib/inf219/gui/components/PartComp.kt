package no.uib.inf219.gui.components

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import no.elg.valentineRealms.core.parts.extract.data.PartData
import tornadofx.getValue
import tornadofx.observable
import tornadofx.setValue

/**
 * @author Elg
 */
class PartComp(part: PartData) {

    val clazzProperty = SimpleStringProperty()
    var className: String by clazzProperty

    val dataProperty = SimpleObjectProperty<ObservableList<AttributeComp>>()
    var attributes: ObservableList<AttributeComp> by dataProperty

    init {
        this.className = part.className
        this.attributes = part.attributes.map { elem ->
            AttributeComp(
                elem,
                this
            )
        }.observable()
    }
}
