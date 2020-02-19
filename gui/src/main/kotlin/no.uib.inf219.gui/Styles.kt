package no.uib.inf219.gui

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
        val parent by cssclass()

        //em scaled
        val Number.ems: Dimension<Dimension.LinearUnits>
            get() = Dimension(
                this.toDouble() * scale,
                Dimension.LinearUnits.em
            )
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

        headLineLabel {
            fontSize = 2.ems
            fontWeight = FontWeight.BOLD
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
    }
}
