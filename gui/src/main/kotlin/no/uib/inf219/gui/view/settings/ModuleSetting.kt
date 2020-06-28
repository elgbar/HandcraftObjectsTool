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
