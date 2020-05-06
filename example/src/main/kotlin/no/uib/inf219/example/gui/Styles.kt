package no.uib.inf219.example.gui

import javafx.geometry.Pos
import javafx.scene.control.OverrunStyle
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
        val responseButton by cssclass()
        val conversationLabel by cssclass()
        val conversationBorderPane by cssclass()
        val responseHBox by cssclass()
        val parent by cssclass()
        val invisibleScrollpaneBorder by cssclass()

        //em scaled
        val Number.ems: Dimension<Dimension.LinearUnits>
            get() = Dimension(
                this.toDouble() * scale,
                Dimension.LinearUnits.em
            )
    }

    init {
        star {
            wrapText = true
            fontSize = 1.ems
        }

        button {
            padding = box(0.4.ems, 0.5.ems)
        }

        headLineLabel {
            fontSize = 1.3.ems
            fontWeight = FontWeight.BOLD
        }

        /**
         * Generic rule for stuff that has multiple other elements within them
         */
        parent {
            padding = box(0.333.ems)
            spacing = 0.333.ems
        }

        //conversation

        conversationBorderPane {
            padding = box(0.333.ems)

            backgroundColor += c("#cecece")
        }

        conversationLabel {
            padding = box(0.5.ems, 0.25.ems)
            fontSize = 2.ems
        }

        //response

        responseHBox {
            alignment = Pos.BASELINE_LEFT
            spacing = 0.333.ems
        }
        responseButton {
            fontSize = 1.25.ems
            textOverrun = OverrunStyle.CLIP
            padding = box(1.0.ems)
        }

        //remove the visible borders around the scroll pane
        invisibleScrollpaneBorder {
            borderWidth = multi(box(0.ems))
            padding = box(0.ems)
        }

        tab {
            text {
                fontSize = 1.2.ems
            }
            tabMinHeight = 1.5.ems

        }

        tabHeaderArea {
            minHeight = 1.5.ems
        }
    }
}
