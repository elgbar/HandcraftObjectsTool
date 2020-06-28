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

import javafx.beans.InvalidationListener
import javafx.beans.Observable

/**
 * A listener that will call the given block when it is invalidated.
 * The returned value can be used to remove the listener.
 *
 * @return The actual listener allow for removal of it.
 * @author Elg
 *
 * @see Observable.removeListener
 *
 */
fun Observable.onChange(block: InvalidationListener.() -> Unit): InvalidationListener {
    val listener = object : InvalidationListener {
        override fun invalidated(observable: Observable?) {
            this.block()
        }
    }
    addListener(listener)
    return listener
}
