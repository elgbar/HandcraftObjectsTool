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

/**
 * Remove the given substring from this string
 * @author Elg
 */
fun String.remove(old: String): String {
    return this.replace(old, "")
}

/**
 * Remove all newline characters `\n` and `\r`
 */
fun String.removeNl(): String {
    return this.remove("\n").remove("\r")
}
