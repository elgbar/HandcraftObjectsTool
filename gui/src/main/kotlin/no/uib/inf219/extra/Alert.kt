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

package no.uib.inf219.extra

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType

/**
 * @author Elg
 */

const val dontWarn = "do not warn me again"
const val enableModule = "enable the module"

/**
 * Let the user never see this warning again
 */
val OK_DISABLE_WARNING = ButtonType("OK $dontWarn", ButtonBar.ButtonData.OK_DONE)
val YES_DISABLE_WARNING = ButtonType("Yes $dontWarn", ButtonBar.ButtonData.YES)
val NO_DISABLE_WARNING = ButtonType("No $dontWarn", ButtonBar.ButtonData.NO)

val ENABLE_MODULE = ButtonType("Enable module and try again")
