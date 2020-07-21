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

package no.uib.inf219.gui.converter

import javafx.util.StringConverter
import java.util.UUID

/**
 *
 * Convert a string to and from [UUID] using [UUID.toString] and [UUID.fromString] respectivly
 *
 * @author Elg
 */
object UUIDStringConverter : StringConverter<UUID>() {

    /**
     * @return The given `uuid` as String, if input is `null` the returned value is also `null`
     *
     * @see UUID.toString
     */
    override fun toString(uuid: UUID?): String? {
        return uuid?.toString()
    }

    /**
     *
     * @return The given string as UUID if it is valid, if input is `null` the returned value is also `null`
     *
     * @throws IllegalArgumentException see [UUID.fromString]
     * @see UUID.fromString
     */
    override fun fromString(string: String?): UUID? {
        return if (string != null) UUID.fromString(string) else null
    }
}
