package no.uib.inf219.gui

import javafx.stage.Stage
import no.uib.inf219.gui.view.BackgroundView
import tornadofx.App

/**
 * @author Elg
 */
class GuiMain : App(BackgroundView::class, Styles::class) {

    companion object {
        const val FILES_FOLDER = "classes"
    }

    override fun start(stage: Stage) {
        stage.icons += resources.image("/icon/chisel-256.png")
        stage.icons += resources.image("/icon/chisel-128.png")
        stage.icons += resources.image("/icon/chisel-64.png")
        super.start(stage)
    }
}
