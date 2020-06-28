/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.inf219.gui

import no.uib.inf219.extra.Persistent
import no.uib.inf219.extra.PersistentFolder
import tornadofx.booleanProperty
import tornadofx.getValue
import tornadofx.setValue
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
    var lastFolderLoaded: File? by PersistentFolder()

    /**
     * Parent folder of the last saved object
     */
    var lastFolderSaved: File? by PersistentFolder()

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

    ///////////////////////////////////////////
    // Properties displayed in control panel //
    ///////////////////////////////////////////

    var unsafeSerializationProp = booleanProperty(false)
    var unsafeSerialization by unsafeSerializationProp

    var printStackTraceOnErrorProp = booleanProperty(false)
    var printStackTraceOnError by printStackTraceOnErrorProp

    var prettyPrintProp = booleanProperty(true)
    var prettyPrint by prettyPrintProp
}
