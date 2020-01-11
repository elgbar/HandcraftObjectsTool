package no.uib.inf219.example.gui

import javafx.scene.control.OverrunStyle
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * @author Elg
 */
class Styles : Stylesheet() {

    companion object {
        val headLineLabel by cssclass()
        val responseButton by cssclass()
        val conversationLabel by cssclass()
    }

    init {
        star {
            wrapText = true
        }
        label {
            fontSize = 14.px
        }
        button {
            fontSize = 12.px
            textOverrun = OverrunStyle.CLIP
            padding = box(3.px, 5.px)
        }

        tooltip {
            fontSize = 13.px
        }

        headLineLabel {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        responseButton {
            fontSize = 14.px
            textOverrun = OverrunStyle.CLIP
            padding = box(8.px, 5.px)
        }

        conversationLabel {
            spacing = 10.px
            padding = box(10.px, 5.px)
            fontSize = 20.px
            backgroundColor += c("#cecece")
        }


    }
}
