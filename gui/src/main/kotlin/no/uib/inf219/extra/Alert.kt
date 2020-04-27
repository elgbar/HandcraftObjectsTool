package no.uib.inf219.extra

import javafx.scene.control.ButtonType

/**
 * @author Elg
 */

val dontWarn = "do not warn me again"
val enableModule = "enable this module\n" +
        "Will close all current editors"

/**
 * Let the user never see this warning again
 */
val OK_DISABLE_WARNING = ButtonType("OK $dontWarn")
val YES_DISABLE_WARNING = ButtonType("Yes $dontWarn")

val OK_ENABLE_MODULE = ButtonType("OK $enableModule")
