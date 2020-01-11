package no.uib.inf219.example.gui

import javafx.scene.control.OverrunStyle
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.c
import tornadofx.px

/**
 * @author Elg
 */
class Styles : Stylesheet() {
    init {
        star {
            wrapText = true
        }
        label {
            spacing = 10.px
            padding = box(10.px, 5.px)
            fontSize = 20.px
            backgroundColor += c("#cecece")
        }
        button {
            fontSize = 14.px
            textOverrun = OverrunStyle.CLIP
            padding = box(8.px, 5.px)
        }

        tooltip {
            fontSize = 13.px
        }

    }
}
