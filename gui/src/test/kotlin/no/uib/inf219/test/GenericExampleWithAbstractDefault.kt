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

import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.test.precondition.Precondition

/**
 * @author Elg
 */
class GenericExampleWithAbstractDefault(
    @JsonProperty(
        "prerequisite",
        required = true,
        defaultValue = "{\"class\":\"no.uib.inf219.test.precondition.AlwaysFalsePrecondition\"}"
    ) val pre: Precondition?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenericExampleWithAbstractDefault

        if (pre != other.pre) return false

        return true
    }

    override fun hashCode(): Int {
        return pre?.hashCode() ?: 0
    }
}
