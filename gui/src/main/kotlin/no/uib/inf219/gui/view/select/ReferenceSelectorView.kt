package no.uib.inf219.gui.view.select

import com.fasterxml.jackson.databind.JavaType
import no.uib.inf219.extra.findChild
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.ReferenceClassBuilder
import no.uib.inf219.gui.controllers.classBuilderNode.FilledClassBuilderNode

/**
 * @author Elg
 */
class ReferenceSelectorView : SelectorView<ClassBuilder>("Reference") {

    override val promptText = "Class builder name"
    override fun cellText(elem: ClassBuilder) = elem.getPreviewValue()
    override fun confirmAndClose() {
        close()
    }

//    init {
//        with(root) {
//
//            style {
//                minWidth = 45.ems
//                minHeight = 25.ems
//            }
//
//            label = hbox {
//                alignment = Pos.CENTER
//                text(ClassSelectorView.SEARCHING) {
//                    addClass(Styles.largefont)
//
//                    searchingProperty.onChange {
//                        runLater {
//                            text = if (searching) ClassSelectorView.SEARCHING else ClassSelectorView.NO_SUBCLASSES_FOUND
//                        }
//                    }
//                }
//            }
//
//            resultList = vbox {
//
//                addClass(Styles.parent)
//
//                textfield {
//                    promptText = "Class Builder Name"
//                    textLabelProperty = textProperty()
//                    textProperty().onChange {
//                        if (text.isNullOrBlank()) {
//                            filteredData.predicate = null
//                        } else {
//                            filteredData.setPredicate {
//                                it.getPreviewValue().contains(text, ignoreCase = true)
//                            }
//                        }
//                    }
//                }
//
//                listview(filteredData) {
//
//                    onUserSelect {
//                        result = it
//                    }
//
//                    //close when pressing enter and something is selected or double clicking
//                    onUserSelect(2) {
//                        result = it
//                        co()
//                    }
//
//                    cellFormat() {
//                        text = it.getPreviewValue()
//                    }
//
//                    addEventHandler(KeyEvent.ANY) { event ->
//                        if (event.code == KeyCode.ENTER && result != null) {
//                            close()
//                        } else if (event.code == KeyCode.ESCAPE) {
//                            result = null
//                            close()
//                        }
//                    }
//                }
//            }
//
//            searchingProperty.onChange {
//                runLater {
//                    if (searching) {
//                        center.replaceWith(label, sizeToScene = true, centerOnScreen = true)
//                    } else {
//                        center.replaceWith(resultList, sizeToScene = true, centerOnScreen = true)
//                    }
//                }
//            }
//            center = label
//        }
//    }


    fun createReference(
        type: JavaType,
        key: ClassBuilder,
        parent: ParentClassBuilder
    ): ReferenceClassBuilder? {
        result = null
        searching = true
        searchResult.clear()
        tornadofx.runAsync {
            searchResult.setAll(
                findInstancesOf(
                    type,
                    controller.root
                ).filter { it != parent.getChild(key) })
            searching = false
        }
        openModal(block = true, owner = currentWindow, escapeClosesWindow = false)

        val ref = result ?: return null
        val item = parent.item.findChild(key)

        return ReferenceClassBuilder(ref.key, ref.parent, key, parent, item).also {
            item.value = FilledClassBuilderNode(key, it, parent, item = item)
        }
    }

    companion object {

        internal fun findInstancesOf(
            type: JavaType,
            cb: ClassBuilder
        ): Set<ClassBuilder> {

            //the set to hold all children of this class builder. Use set to prevent duplicates
            val allChildren = HashSet<ClassBuilder>()
            allChildren.add(cb) //remember to also add the parent
            if (cb is ParentClassBuilder) {
                for (child in cb.getChildren()) {
                    allChildren.addAll(
                        findInstancesOf(
                            type,
                            child
                        )
                    )
                }
            }

            //find all children that is the correct type
            // and isn't a ReferenceClassBuilder to prevent cycles
            return allChildren.filter { it.type.isTypeOrSubTypeOf(type.rawClass) && it !is ReferenceClassBuilder }
                .toSet()
        }
    }
}
