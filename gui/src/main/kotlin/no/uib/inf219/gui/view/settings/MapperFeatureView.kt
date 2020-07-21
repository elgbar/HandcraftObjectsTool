/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.inf219.gui.view.settings

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.StreamReadFeature
import com.fasterxml.jackson.core.StreamWriteFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import javafx.beans.property.BooleanProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.control.CheckBox
import no.uib.inf219.gui.view.ControlPanelView
import org.controlsfx.control.PropertySheet
import org.controlsfx.property.editor.AbstractPropertyEditor
import org.controlsfx.property.editor.PropertyEditor
import tornadofx.View
import tornadofx.asObservable
import tornadofx.controlsfx.propertysheet
import java.util.Optional

class MapperFeatureView : View("My View") {

    fun ObjectMapper.enable2(enum: Enum<*>): ObjectMapper {
        return when (enum) {
            is MapperFeature -> enable(enum)
            is JsonParser.Feature -> enable(enum)
            is JsonGenerator.Feature -> enable(enum)
            is SerializationFeature -> enable(enum)
            is DeserializationFeature -> enable(enum)

            is JsonFactory.Feature -> {
                factory.enable(enum); this
            }
            is StreamReadFeature -> enable(enum.mappedFeature())
            is StreamWriteFeature -> enable(enum.mappedFeature())
            else -> error("Enum ${enum.javaClass} not supported as an Object Mapper feature")
        }
    }

    class BooleanCheckBox(property: PropertySheet.Item) :
        AbstractPropertyEditor<Boolean, CheckBox>(property, CheckBox()) {
        override fun getObservableValue(): BooleanProperty {
            return editor.selectedProperty()
        }

        override fun setValue(value: Boolean) {
            editor.isSelected = value
        }
    }

    class FeatureItem(val feature: Enum<*>) : PropertySheet.Item {
        private val name = feature.name.replace('_', ' ').toLowerCase()
        private val desc = "${feature.javaClass.simpleName}.${feature.name}"

        override fun setValue(value: Any?) {
//                        if (value == null) {
//                            TODO("reset")
//                        } else if (value is Boolean) {
//                            TODO("set")
//                        }
//                        kotlin.error("Illegal value, can only be nullable boolean")
        }

        override fun getName() = name
        override fun getDescription() = desc
        override fun getType() = feature.javaClass
        override fun getValue() = with(ControlPanelView.mapper) {
            when (feature) {
                is MapperFeature -> isEnabled(feature)
                is JsonParser.Feature -> isEnabled(feature)
                is JsonGenerator.Feature -> isEnabled(feature)
                is SerializationFeature -> isEnabled(feature)
                is DeserializationFeature -> isEnabled(feature)

                is JsonFactory.Feature -> isEnabled(feature)
                is StreamReadFeature -> isEnabled(feature)
                is StreamWriteFeature -> isEnabled(feature)
                else -> error("Enum ${feature.javaClass} not supported as an Object Mapper feature")
            }
        }

        override fun getObservableValue() = Optional.empty<ObservableValue<out Any>>()
        override fun getCategory(): String = type.simpleName
        override fun getPropertyEditorClass(): Optional<Class<out PropertyEditor<*>>> =
            Optional.of(BooleanCheckBox::class.java)
    }

    private val featureEnums = HashSet<Enum<*>>()

    init {
        featureEnums.addAll(MapperFeature.values())
        featureEnums.addAll(MapperFeature.values())
        featureEnums.addAll(StreamReadFeature.values())
        featureEnums.addAll(JsonParser.Feature.values())
        featureEnums.addAll(JsonGenerator.Feature.values())
        featureEnums.addAll(JsonFactory.Feature.values())
        featureEnums.addAll(SerializationFeature.values())
        featureEnums.addAll(DeserializationFeature.values())
        featureEnums.addAll(StreamWriteFeature.values())
    }

    private val fes: ObservableList<PropertySheet.Item> = featureEnums.map { FeatureItem(it) }.asObservable()

    override val root = propertysheet(mode = PropertySheet.Mode.CATEGORY, items = fes)
}
