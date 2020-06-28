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

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.Screen
import tornadofx.*


/**
 * @author Elg
 */
class Styles : Stylesheet() {
    companion object {

        const val X1_DPI = 108 // dpi of screen originally implemented on
        val scale = Screen.getPrimary().dpi / X1_DPI

        val headLineLabel by cssclass()
        val largefont by cssclass()
        val parent by cssclass()
        val numberChanger by cssclass()
        val invisibleScrollpaneBorder by cssclass()
        val flowPane by cssclass()

        val monospaceFont = loadFont("/fonts/ubuntu/UbuntuMono-R.ttf", -1)!!
    }

    init {
        star {
            wrapText = true
            fontSize = 1.ems
        }

        button {
            padding = box(0.4.ems, 0.5.ems)
        }

        hyperlink {
            borderColor = multi(box(Color.TRANSPARENT))
            padding = box(0.ems, 0.ems)
        }

        headLineLabel {
            fontSize = 2.ems
            fontWeight = FontWeight.BOLD
        }

        largefont {
            fontSize = 2.ems
        }

        flowPane {
            hgap = 0.333.ems
            vgap = hgap
        }

        /**
         * Generic rule for stuff that has multiple other elements within them
         */
        parent {
            padding = box(0.333.ems)
            spacing = 0.333.ems
        }

        splitPaneDivider {
            padding = box(0.005.ems)
        }
        numberChanger {
            font = monospaceFont
            fontSize = 0.1.ems
            padding = box(0.ems, 0.333.ems)
        }


        //remove the visible borders around the scroll pane
        invisibleScrollpaneBorder {
            borderWidth = multi(box(0.ems))
            padding = box(0.ems)
        }
    }
}


//em scaled
val Number.ems: Dimension<Dimension.LinearUnits>
    get() = Dimension(
        this.toDouble() * Styles.scale,
        Dimension.LinearUnits.em
    )
