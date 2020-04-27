package no.uib.inf219.gui.view.select

import com.fasterxml.jackson.databind.JavaType
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfoList
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonType
import no.uib.inf219.extra.OK_DISABLE_WARNING
import no.uib.inf219.extra.OK_ENABLE_MODULE
import no.uib.inf219.extra.loadType
import no.uib.inf219.extra.onChange
import no.uib.inf219.gui.controllers.Settings.showMrBeanWarning
import no.uib.inf219.gui.ems
import no.uib.inf219.gui.loader.DynamicClassLoader
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.gui.view.ControlPanelView.mrBeanModuleEnabled
import tornadofx.*
import java.lang.reflect.Modifier


/**
 * A view to allow for selection of classes via a GUI. It is near identical to how this works in IntelliJ IDEA.
 *
 * The opened GUI will use [ClassGraph] to find all subclasses (but no interfaces) of
 *
 * ## Example usage
 *
 * ```kotlin
 * val subclass = tornadofx.find<ClassSelectorView>().subtypeOf(Any::class.type())
 * ```
 *
 * @author Elg
 */
class ClassSelectorView : SelectorView<String>("Select implementation") {


    /**
     * If we the current [result] class the the class we want to return
     */
    private val finishedSearching = booleanProperty()


    private val superClassProperty = SimpleStringProperty()
    private var superClass by superClassProperty

    override fun cellText(elem: String): String {
        return elem
    }

    override val promptText = "Full class name"

    private val resultType get() = DynamicClassLoader.loadType(result)

    init {

        ControlPanelView.mrBeanModuleEnabledProp.onChange { useMrBean ->
            //reset the warning when mr bean is disabled
            if (!useMrBean) showMrBeanWarning = true
        }

        with(root) {
            resultList.children.add(0, textflow().apply {
                fun updateText(): String {
                    return "Choose subclass of $superClass (${searchResult.size} found)"
                }

                textflow().text(updateText()) {
                    style { fontSize = 1.5.ems }
                    superClassProperty.onChange { text = updateText() }
                    searchResult.onChange { text = updateText() }
                }
            })
        }
    }

    override fun confirmAndClose() {

        fun findSubType() {
            finishedSearching.set(false)
            close()
        }

        fun returnSelectedType() {
            finishedSearching.set(true)
            close()
        }

        val realResult = resultType
        if (realResult != null) {

            val resultType: String
            val contentInfo: String

            when {
                realResult.isAbstract -> {
                    if (!mrBeanModuleEnabled) {
                        if (showMrBeanWarning == true) {
                            information(
                                "Cannot select an abstract class when the Mr Bean module is not enabled.",
                                "You will now be asked to select a subclass of ${realResult.rawClass}",
                                owner = currentWindow,
                                buttons = *arrayOf(ButtonType.OK, OK_DISABLE_WARNING, OK_ENABLE_MODULE),
                                actionFn = {
                                    when (it) {
                                        OK_DISABLE_WARNING -> showMrBeanWarning = false
                                        OK_ENABLE_MODULE -> {
                                            this@ClassSelectorView.result = null
                                            returnSelectedType()
                                            mrBeanModuleEnabled = true
                                            return
                                        }
                                    }
                                }
                            )
                        }
                        //mr bean is not enabled so we cannot return abstract types
                        findSubType()
                        return
                    }
                    resultType = if (realResult.isInterface) "interface" else "abstract class"
                    contentInfo =
                        "The class you have selected is either an interface or an abstract class.\n" +
                                "You can select an abstract type as the Mr Bean module is enabled in the settings."
                }
                realResult.isFinal -> {
                    //There can never be any subclasses of final classes
                    returnSelectedType()
                    return
                }
                else -> {
                    resultType =
                        if (realResult.isEnumType) "enum" else if (realResult.isArrayType) "array" else "class"
                    contentInfo =
                        "The selected class may have subclasses you want to choose rather than this one."
                }
            }

            confirmation(
                "Do you want to return the selected $resultType ${realResult.rawClass?.name}?",
                "$contentInfo\n" +
                        "\n" +
                        "If you choose YES you will select this class and the dialogue will close.\n" +
                        "If you choose NO then you will be asked to select a subclass of this class.\n" +
                        "If you select CANCEL no choice will be made can you are free to choose another class.",
                title = "Return the selected $resultType ${realResult.rawClass?.name}?",
                owner = currentWindow,
                buttons = *arrayOf(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL),
                actionFn = {
                    when (it) {
                        ButtonType.YES -> returnSelectedType() // return the abstract class
                        ButtonType.NO -> findSubType() // find an impl of the selected class
                        ButtonType.CANCEL -> return //Return to search , do not select the class
                    }
                }
            )
        }
    }

    /**
     * Ask the user to select a subclass of the given [superType]. This method will block till the class is selected or the user cancels the search (in which case the returned value will be `null`)
     *
     * @param superType The super class to find the subclasses to
     * @param showAbstract If abstract classes should be listed
     *
     * @return A user selected subtype of [superType]. The returned type is guaranteed to not be abstract if [showAbstract] is `false`. `null` might be returned if the user cancels the search (it closing the selection window)
     *
     * @throws IllegalArgumentException if the super type is not allowed. A type is not allowed if it is a primitive class, final class or does not have a canonical name (such as local and anonymous classes)
     * @throws IllegalArgumentException if the given types does not have a java class associated with it
     */
    fun subtypeOf(superType: JavaType, showAbstract: Boolean = false): JavaType? {
        result = superType.rawClass.typeName
        finishedSearching.value = false
        do {
            val rt = resultType
            if (rt == null) {
                warning("Failed to find the javatype of $result")
                return null
            }
            tornadofx.runAsync {
                searchForSubtypes(rt, showAbstract)
            }
            openModal(block = true, owner = currentWindow, escapeClosesWindow = false)
        } while (result != null && !finishedSearching.value)
        return resultType
    }

    /**
     * Update the list of subclasses to be those of [superType]. Unless doing something fancy it's recommended to use [subtypeOf]
     *
     * @param superType The super class to find the subclasses to
     * @param showAbstract If abstract classes should be listed
     */
    private fun searchForSubtypes(superType: JavaType, showAbstract: Boolean) {


        require(superType.rawClass != null) { "Given java '$superType' types does not have a raw class" }
        require(superType.rawClass.canonicalName != null) { "Given super class '${superType.toCanonical()}' must have a canonical name" }
        require(!superType.isPrimitive) { "Given super class '${superType.toCanonical()}' cannot be primitive" }
        require(!superType.isFinal) { "Given super class '${superType.toCanonical()}' cannot be final" }


        synchronized(this) {

            runLater {
                result = null
                superClass = superType.rawClass.name
                textLabelProperty.set("")
                searching = true
            }
            val superClass: Class<*> = superType.rawClass

            ClassGraph()
                .enableExternalClasses()
                .addClassLoader(DynamicClassLoader)
                .scan().use { scanResult ->
                    val cil: ClassInfoList =
                        when {
                            superClass == Any::class.java -> scanResult.allClasses
                            superClass.isInterface -> scanResult.getClassesImplementing(superClass.name)
                            else -> scanResult.getSubclasses(superClass.canonicalName)
                        }
                    val classes = cil.filter {
                        //Only show abstract types when wanted
                        // and never show annotations
                        (showAbstract || !Modifier.isAbstract(it.modifiers)) && !it.isAnnotation
                    }.names

                    runLater {
                        searchResult.setAll(classes)
                    }
                }
            searching = false
        }
    }

    companion object {
        const val SEARCHING = "Searching..."
        const val NO_SUBCLASSES_FOUND = "No subclasses found"
    }
}

