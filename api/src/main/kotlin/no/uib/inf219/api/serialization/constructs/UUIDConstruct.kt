package no.uib.inf219.api.serialization.constructs

import org.yaml.snakeyaml.constructor.AbstractConstruct
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.ScalarNode
import java.util.*


/**
 * @author Elg
 */
class UUIDConstruct : AbstractConstruct() {

    override fun construct(node: Node): Any {
        return UUID.fromString((node as ScalarNode).value)
    }
}
