package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.scene.Node
import javafx.scene.control.TextFormatter
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.util.StringConverter
import no.uib.inf219.gui.Styles.Companion.numberChanger
import no.uib.inf219.gui.extra.removeNl
import no.uib.inf219.gui.view.OutputArea
import tornadofx.*


/**
 * @author Elg
 */
abstract class SimpleNumberClassBuilder<T : Number>(
    primClass: Class<T>,
    initialValue: T,
    parent: ClassBuilder<*>? = null,
    name: String? = null,
    property: PropertyWriter? = null,
    converter: StringConverter<T>
) : SimpleClassBuilder<T>(primClass, initialValue, parent, name, property, converter) {

    override fun editView(parent: Pane): Node {
        return parent.hbox {

            vbox {
                style {
                    fitToParentHeight()
                }
                button("+") {
                    style {
                        addClass(numberChanger)
                    }
                    setOnMouseClicked { event ->
                        if (event.button == MouseButton.PRIMARY) {

                            var num = 1
                            if (event.isShiftDown) num *= 10
                            if (event.isControlDown) num *= 100

                            @Suppress("UNCHECKED_CAST")
                            value = when (value::class) {
                                Int::class -> (value as Int).plus(num) as T
                                Long::class -> (value as Long).plus(num) as T
                                Double::class -> (value as Double).plus(num) as T
                                Float::class -> (value as Float).plus(num) as T
                                Short::class -> (value as Short).plus(num).toShort() as T
                                Byte::class -> (value as Byte).plus(num).toByte() as T
                                else -> throw IllegalStateException("Unknown number ${value::class.java.simpleName}")
                            }
                        }
                    }
                }
                button("-") {
                    style {
                        addClass(numberChanger)
                    }
                    setOnMouseClicked { event ->
                        if (event.button == MouseButton.PRIMARY) {

                            var num = 1
                            if (event.isShiftDown) num *= 10
                            if (event.isControlDown) num *= 100
                            @Suppress("UNCHECKED_CAST")
                            value = when (value::class) {
                                Int::class -> (value as Int).minus(num) as T
                                Long::class -> (value as Long).minus(num) as T
                                Double::class -> (value as Double).minus(num) as T
                                Float::class -> (value as Float).minus(num) as T
                                Short::class -> (value as Short).minus(num).toShort() as T
                                Byte::class -> (value as Byte).minus(num).toByte() as T
                                else -> throw IllegalStateException("Unknown number ${value::class.java.simpleName}")
                            }
                        }
                    }
                }
            }
            textfield {
                textFormatter = TextFormatter<Short>() {

                    val text = it.controlNewText.removeNl().trim()
                    if (it.isContentChange && text.isNotEmpty() && !validate(text)) {
                        OutputArea.logln { "Failed to parse '$text' to ${this@SimpleNumberClassBuilder.type.rawClass.simpleName}" }
                        return@TextFormatter null
                    }
                    return@TextFormatter it
                }
                bindStringProperty(textProperty(), converter, valueProperty)
            }
            button("reset") {
                setOnAction {

                    @Suppress("UNCHECKED_CAST")
                    value = when (value::class) {
                        Int::class -> 0 as T
                        Long::class -> 0L as T
                        Double::class -> 0.0 as T
                        Float::class -> 0f as T
                        Short::class -> (0.toShort()) as T
                        Byte::class -> (0.toByte()) as T
                        else -> throw IllegalStateException("Unknown number ${value::class.java.simpleName}")
                    }
                }
            }
        }
    }
}
