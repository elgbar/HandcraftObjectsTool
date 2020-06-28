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

package no.uib.inf219.gui.backend.cb.simple

import javafx.event.EventTarget
import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.SimpleClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.converter.StringStringConverter
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.textarea

/**
 * Note that the default value is the empty String `""` and not the default value `null`
 */
class StringClassBuilder(
    initial: String = "",
    key: ClassBuilder?,
    parent: ParentClassBuilder?,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleClassBuilder<String>(String::class, initial, key, parent, property, immutable, StringStringConverter, item) {

    override fun createEditView(
        parent: EventTarget,
        controller: ObjectEditorController
    ) = parent.textarea {
        bindStringProperty(textProperty(), converter, serObjectObservable)
    }

    override fun validate(text: String): Boolean {
        //A string is always a valid string
        return true
    }
}
