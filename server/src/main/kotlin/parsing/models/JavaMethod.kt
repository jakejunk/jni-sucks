package parsing.models

import kotlinx.serialization.Serializable

@Serializable
data class JavaMethod(
    val name: String,
    val params: Set<JavaVariable>,
    val returnType: String,
    val modifiers: Set<String>
)