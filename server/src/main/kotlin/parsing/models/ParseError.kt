package parsing.models

import kotlinx.serialization.Serializable

@Serializable
data class ParseError(
    val message: String,
    val lineNum: Int,
    val colStart: Int,
    val colEnd: Int
) {
    override fun toString(): String {
        return "$lineNum: error: $message"
    }
}