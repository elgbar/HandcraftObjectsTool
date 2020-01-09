package no.uib.inf219.gui

import no.uib.inf219.gui.view.BackgroundView
import tornadofx.App
import java.io.File

/**
 * @author Elg
 */
class GuiMain : App(BackgroundView::class, Styles::class) {

    init {
        DataManager.addSource(File("chat.yml"))
        DataManager.addSource(File("parts.yml"))
    }

    companion object {
        const val MAIN_CLASS = "no.elg.valentineRealms.chat.conversation.ConversationFile"
    }
}
