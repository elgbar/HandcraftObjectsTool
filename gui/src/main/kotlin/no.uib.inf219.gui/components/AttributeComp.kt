package no.uib.inf219.gui.components

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import no.uib.inf219.extract.data.AttributeData
import tornadofx.getValue
import tornadofx.setValue

/**
 * @author Elg
 */
open class AttributeComp(data: AttributeData, parent: PartComp) {

    val parentProperty = SimpleObjectProperty<PartComp>()
    var parent: PartComp by parentProperty

    val pathProperty = SimpleStringProperty()
    var path: String by pathProperty

    val classNameProperty = SimpleStringProperty()
    var className: String by classNameProperty

    val isListProperty = SimpleBooleanProperty()
    var isList by isListProperty

    val requiredProperty = SimpleBooleanProperty()
    var required by requiredProperty

    val defaultValueProperty = SimpleStringProperty()
    var defaultValue: String by defaultValueProperty

    val noteProperty = SimpleStringProperty()
    var note: String by noteProperty

    val valueProperty = SimpleStringProperty()
    var value: String by valueProperty

    init {
        this.path = data.path
        this.className = data.className
        this.isList = data.isList
        this.required = data.required
        this.defaultValue = data.defaultValue
        this.note = data.note
        //copy default value as the initial value
        this.value = defaultValue
        this.parent = parent
    }
}
