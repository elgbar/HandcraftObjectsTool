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

package no.uib.inf219.example.data.prerequisite

import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.example.data.Conversation

/**
 * @author Elg
 */
class ReadConversationPrerequisite(
    @JsonProperty("conversation", required = true)
    val conv: Conversation
) : Prerequisite {

    override fun check(): Boolean {
        return conv.hasBeenRead
    }

    override fun reason(): String {
        return "The given conversation '${conv.name}' has not been read"
    }
}
