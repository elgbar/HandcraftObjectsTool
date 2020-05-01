package no.uib.inf219.gui

import no.uib.inf219.extra.Persistent
import java.io.File

/**
 * Settings of the application they should be be managed by [Persistent]
 *
 * @author Elg
 */

object Settings {

    /**
     * Parent folder of the lastly loaded jar file
     */
    var lastFolderLoaded: File? by Persistent()

    /**
     * Parent folder of the last saved object
     */
    var lastFolderSaved: File? by Persistent()

    /**
     * Should all children be collapsed when parent tree item is
     */
    var collapseChildren by Persistent(true)

    ///////////////////////
    // Disabled Warnings //
    ///////////////////////

    /*
     * If warnings should be displayed to the user. Preferred to be combined with OK_DISABLE_WARNING
     */

    /**
     * If a warning should be displayed when overwriting a property with a reference
     */
    var showOverwriteWithRefWarning by Persistent(true)

    /**
     * If a warning hinting that the mrbean module is not enabled
     */
    var showMrBeanWarning by Persistent(true)

    /**
     * If a warning should be displayed when changing what modules are active
     */
    var showCloseAllTabsOnModuleChangeWarning by Persistent(true)
}
