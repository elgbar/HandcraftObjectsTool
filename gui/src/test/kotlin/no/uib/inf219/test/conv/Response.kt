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

package no.uib.inf219.test.conv

import com.fasterxml.jackson.annotation.JsonProperty
import javafx.scene.control.Tooltip
import no.uib.inf219.example.storage.Identifiable
import no.uib.inf219.example.storage.RetrievableStorage
import no.uib.inf219.example.storage.StoreHandler

/**
 * @author Elg
 */
class Response : Identifiable<String> {

    @JsonProperty("response", required = true)
    var response: String = "???"

    @JsonProperty("name", required = false, defaultValue = "Response")
    var name: String = "Response #${++createId}"

    @JsonProperty("conv", required = false)
    var conv: Conversation? = null

    companion object {
        val exitResponse: List<Response> = listOf(
            create("End conversation", "Exit")
        )

        @JvmStatic
        var createId = 0

        @JvmStatic
        fun create(
            text: String,
            name: String? = null,
            conv: Conversation? = null
        ): Response {
            val resp = Response()
            resp.response = text
            if (name != null)
                resp.name = name
            if (conv != null)
                resp.conv = conv
            return resp
        }
    }

    init {
        val store: RetrievableStorage<String, Response> = StoreHandler.getStore(
            Response::class.java
        )
        store.store(this)
    }

    /**
     * @return If the conversation should close
     */
    fun shouldClose(): Boolean {
        return conv == null
    }

    fun tooltip(): Tooltip? {
        return if (shouldClose()) Tooltip("This will end the conversation") else null
    }

    override fun getId(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Response) return false

        if (response != other.response) return false
        if (name != other.name) return false
        return conv === other.conv
    }

    override fun hashCode(): Int {
        var result = response.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Response(response='$response', name='$name', conv=$conv)"
    }
}
