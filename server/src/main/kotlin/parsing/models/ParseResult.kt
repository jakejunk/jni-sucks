package parsing.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ParseResult {
    @Serializable
    @SerialName("ok")
    data class Ok(
        val types: Set<JavaType>
    ): ParseResult()

    @Serializable
    @SerialName("error")
    data class Error(
        val errors: Set<ParseError>
    ): ParseResult()
}
