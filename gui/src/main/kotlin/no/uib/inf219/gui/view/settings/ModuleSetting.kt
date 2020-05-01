package no.uib.inf219.gui.view.settings

import com.fasterxml.jackson.databind.Module
import javafx.scene.control.ButtonType
import no.uib.inf219.extra.YES_DISABLE_WARNING
import no.uib.inf219.gui.Settings
import no.uib.inf219.gui.view.BackgroundView
import no.uib.inf219.gui.view.ControlPanelView
import tornadofx.*
import java.util.regex.Pattern

class ModuleSetting(initiallyEnabled: Boolean, val tooltip: String, val createModule: () -> Module) {

    val enabledProp = booleanProperty(initiallyEnabled)
    var enabled by enabledProp

    val typeId: Any?
    val name: Any?


    internal var ignoreNext = false

    init {
        val module = createModule()
        typeId = module.typeId
        name = upperCaseRegex.findAll(module.moduleName).map { it.value }.joinToString(" ")

        ////////////////////////////
        // Warn and Update Mapper //
        ////////////////////////////


        enabledProp.addListener { _, oldValue, _ ->
            if (ignoreNext) {
                ignoreNext = false
                return@addListener
            }

            if (Settings.showCloseAllTabsOnModuleChangeWarning != false && FX.find<BackgroundView>().tabPane.tabs.size > 1) {
                warning(
                    "Changing this setting will close all currently open tabs",
                    "Are you sure you want to close all currently opened tabs?",
                    ButtonType.YES, YES_DISABLE_WARNING, ButtonType.NO,
                    owner = ControlPanelView.currentWindow
                ) { button ->
                    when (button) {
                        //return old value
                        ButtonType.NO -> {
                            ignoreNext = true
                            runLater {
                                enabled = oldValue
                            }
                            return@addListener
                        }
                        YES_DISABLE_WARNING -> {
                            Settings.showCloseAllTabsOnModuleChangeWarning = false
                        }
                    }
                }
            }
            //this forces an call to #updateMapper()
            ControlPanelView.mapper = ControlPanelView.orgMapper
        }
    }

    companion object {

        val upperCaseRegex: Regex = Pattern.compile("[A-Z][^A-Z]*").toRegex()
    }
}
