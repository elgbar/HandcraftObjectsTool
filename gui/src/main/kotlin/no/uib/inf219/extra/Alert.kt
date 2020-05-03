package no.uib.inf219.extra

import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType

/**
 * @author Elg
 */

const val dontWarn = "do not warn me again"
const val enableModule = "enable the module"

/**
 * Let the user never see this warning again
 */
val OK_DISABLE_WARNING = ButtonType("OK $dontWarn", ButtonBar.ButtonData.OK_DONE)
val YES_DISABLE_WARNING = ButtonType("Yes $dontWarn", ButtonBar.ButtonData.YES)

val ENABLE_MODULE = ButtonType("Enable module and try again")
