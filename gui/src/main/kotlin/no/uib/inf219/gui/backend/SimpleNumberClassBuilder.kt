package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.scene.Node
import javafx.scene.control.TextFormatter
import javafx.scene.input.MouseButton
import javafx.scene.layout.Pane
import javafx.util.StringConverter
import no.uib.inf219.extra.removeNl
import no.uib.inf219.gui.Styles.Companion.numberChanger
import no.uib.inf219.gui.view.OutputArea
import tornadofx.*


/**
 * @author Elg
 */
abstract class SimpleNumberClassBuilder<T : Number>(
    primClass: Class<T>,
    initialValue: T,
    name: String,
    parent: ClassBuilder<*>? = null,
    property: PropertyWriter? = null,
    immutable: Boolean = false,
    converter: StringConverter<T>
) : SimpleClassBuilder<T>(primClass, initialValue, name, parent, property, immutable, converter) {

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
                            serObject = when (serObject::class) {
                                Int::class -> (serObject as Int).plus(num) as T
                                Long::class -> (serObject as Long).plus(num) as T
                                Double::class -> (serObject as Double).plus(num) as T
                                Float::class -> (serObject as Float).plus(num) as T
                                Short::class -> (serObject as Short).plus(num).toShort() as T
                                Byte::class -> (serObject as Byte).plus(num).toByte() as T
                                else -> throw IllegalStateException("Unknown number ${serObject::class.java.simpleName}")
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
                            serObject = when (serObject::class) {
                                Int::class -> (serObject as Int).minus(num) as T
                                Long::class -> (serObject as Long).minus(num) as T
                                Double::class -> (serObject as Double).minus(num) as T
                                Float::class -> (serObject as Float).minus(num) as T
                                Short::class -> (serObject as Short).minus(num).toShort() as T
                                Byte::class -> (serObject as Byte).minus(num).toByte() as T
                                else -> throw IllegalStateException("Unknown number ${serObject::class.java.simpleName}")
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
                bindStringProperty(textProperty(), converter, serObjectProperty)
            }
            button("reset") {
                setOnAction {

                    @Suppress("UNCHECKED_CAST")
                    serObject = when (serObject::class) {
                        Int::class -> 0 as T
                        Long::class -> 0L as T
                        Double::class -> 0.0 as T
                        Float::class -> 0f as T
                        Short::class -> (0.toShort()) as T
                        Byte::class -> (0.toByte()) as T
                        else -> throw IllegalStateException("Unknown number ${serObject::class.java.simpleName}")
                    }
                }
            }
        }
    }

    override fun toString(): String {
        return "Simple Number CB; value=$serObject, clazz=$type)"
    }
}
