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

package no.uib.inf219.test

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators

/**
 * @author Elg
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator::class)
class UselessRecursiveObject {

    @JsonProperty("with")
    var with: UselessRecursiveObject? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UselessRecursiveObject

        if (with !== other.with) return false

        return true
    }

    override fun toString(): String {
        return "UselessRecursiveObject(hashCode=${hashCode()}, with=${with.hashCode()})"
    }
}
