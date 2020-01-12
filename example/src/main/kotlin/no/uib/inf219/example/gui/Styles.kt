package no.uib.inf219.example.gui

import javafx.geometry.Pos
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
        val conversationBorderPane by cssclass()
        val responseHBox by cssclass()
        val parent by cssclass()
    }

    init {
        star {
            wrapText = true
        }
        label {
            fontSize = 1.em
        }
        button {
            fontSize = 1.em
            padding = box(0.4.em, 0.5.em)
        }

        tooltip {
            fontSize = 1.em
        }

        headLineLabel {
            fontSize = 2.em
            fontWeight = FontWeight.BOLD
        }

        /**
         * Generic rule for stuff that has multiple other elements within them
         */
        parent {
            padding = box(0.333.em)
            spacing = 0.333.em
        }

        //conversation

        conversationBorderPane {
            padding = box(0.333.em)

            backgroundColor += c("#cecece")
        }

        conversationLabel {
            padding = box(0.5.em, 0.25.em)
            fontSize = 2.em
        }

        //response

        responseHBox {
            alignment = Pos.BASELINE_LEFT
            spacing = 0.333.em
        }
        responseButton {
            fontSize = 1.25.em
            textOverrun = OverrunStyle.CLIP
            padding = box(1.0.em)
        }
    }
}
