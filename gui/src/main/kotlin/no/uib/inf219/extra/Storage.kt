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

package no.uib.inf219.extra

import java.io.File

/**
 * @author Elg
 */

val HOME_PATH: String = System.getProperty("user.home") + File.separator

/**
 * @return The home folder of the running user
 */
fun homeFolder(child: String = ""): File {
    return if (child.isEmpty()) File(HOME_PATH) else File(HOME_PATH, child)
}

/**
 * Return the folder of this application. The folder is guaranteed to exist
 * @throws NullPointerException If the primary stage does not have a title
 */
fun hotApplicationHome(): File {
    return homeFolder(".hot").ensureFolder()
}
