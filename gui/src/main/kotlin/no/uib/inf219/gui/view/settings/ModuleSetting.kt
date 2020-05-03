package no.uib.inf219.gui.view.settings

import com.fasterxml.jackson.databind.Module
import no.uib.inf219.gui.view.ControlPanelView
import tornadofx.booleanProperty
import tornadofx.getValue
import tornadofx.onChange
import tornadofx.setValue
import java.util.regex.Pattern

class ModuleSetting(
    initiallyEnabled: Boolean,
    val tooltip: String,
    val createModule: () -> Module
) {

    val enabledProp = booleanProperty(initiallyEnabled)
    var enabled by enabledProp

    val typeId: Any?
    val name: String

    init {
        val module = createModule()
        typeId = module.typeId
        name = upperCaseRegex.findAll(module.moduleName).map { it.value }.joinToString(" ")
        enabledProp.onChange { ControlPanelView.reloadMapper() }
    }

    companion object {
        val upperCaseRegex: Regex = Pattern.compile("[A-Z][^A-Z]*").toRegex()
    }
}
