package parsing.models

import kotlinx.serialization.Serializable

@Serializable
data class JavaVariable(
    val name: String,
    val type: String,
    val modifiers: Set<String>
)