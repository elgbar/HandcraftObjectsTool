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

package no.uib.inf219.gui

import javafx.stage.Stage
import no.uib.inf219.gui.view.BackgroundView
import tornadofx.App

/**
 * @author Elg
 */
class GuiMain : App(BackgroundView::class, Styles::class) {

    companion object {
        const val FILES_FOLDER = "jars"
    }

    override fun start(stage: Stage) {
        stage.icons += resources.image("/icon/chisel-256.png")
        stage.icons += resources.image("/icon/chisel-128.png")
        stage.icons += resources.image("/icon/chisel-64.png")
        super.start(stage)
    }
}
