package parsertests

import parsing.JavaSourceParser
import parsing.models.JavaType
import parsing.models.ParseResult
import java.lang.Exception
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalContracts
inline fun <reified T: JavaType> fromSource(srcBlock: () -> String): T {
    contract {
        callsInPlace(srcBlock, InvocationKind.EXACTLY_ONCE)
    }

    val parser = JavaSourceParser()
    val src = srcBlock()

    val types = when (val parseResult = parser.parse(src)) {
        is ParseResult.Ok -> parseResult.types
        is ParseResult.Error -> {
            val concatErrors = parseResult.errors.joinToString(separator = "\n")
            throw Exception("Parsing error(s):\n$concatErrors")
        }
    }

    return when (val firstType = types.elementAt(0)) {
        is T -> firstType
        else -> throw Exception("Found Java type was an instance of ${firstType.javaClass}")
    }
}