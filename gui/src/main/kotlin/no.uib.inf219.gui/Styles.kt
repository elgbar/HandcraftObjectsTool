package no.uib.inf219.gui

import javafx.scene.text.FontWeight
import tornadofx.Stylesheet
import tornadofx.c
import tornadofx.em

/**
 * @author Elg
 */
class Styles : Stylesheet() {
    init {
        label {
            fontSize = 2.em
            fontWeight = FontWeight.BOLD
            backgroundColor += c("#cecece")
        }
    }
}
