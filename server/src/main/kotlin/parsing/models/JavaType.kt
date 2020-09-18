package parsing.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class JavaType {
    @Serializable
    @SerialName("class")
    data class Class(
        val name: String,
        val modifiers: Set<String>,
        val parentClass: String,
        val interfaces: Set<String>,
        val fields: Set<JavaVariable>,
        val methods: Set<JavaMethod>,
        val constructors: Set<JavaMethod>,
        val nestedTypes: Set<JavaType>
    ): JavaType()

    @Serializable
    @SerialName("interface")
    data class Interface(
        val name: String,
        val modifiers: Set<String>,
        val interfaces: Set<String>,
        val fields: Set<JavaVariable>,
        val methods: Set<JavaMethod>,
        val nestedTypes: Set<JavaType>
    ): JavaType()

    @Serializable
    @SerialName("enum")
    data class Enum(
        val name: String,
        val modifiers: Set<String>,
        val interfaces: Set<String>,
        val enumValues: Set<String>,
        val fields: Set<JavaVariable>,
        val methods: Set<JavaMethod>,
        val constructors: Set<JavaMethod>,
        val nestedTypes: Set<JavaType>
    ): JavaType()
}