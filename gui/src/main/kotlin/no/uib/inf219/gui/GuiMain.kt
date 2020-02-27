package no.uib.inf219.gui

import no.uib.inf219.gui.view.BackgroundView
import tornadofx.App

/**
 * @author Elg
 */
class GuiMain : App(BackgroundView::class, Styles::class) {

    companion object {
        const val FILES_FOLDER = "classes"
    }
}
