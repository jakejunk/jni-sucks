package parsing

import parsing.models.*
import org.eclipse.jdt.core.compiler.CategorizedProblem
import org.eclipse.jdt.core.compiler.IProblem
import spoon.Launcher
import spoon.SpoonModelBuilder
import spoon.reflect.declaration.*
import spoon.reflect.reference.CtFieldReference
import spoon.support.compiler.VirtualFile
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler

class JavaSourceParser {
    fun parse(src: String): ParseResult {
        val launcher = Launcher()
        launcher.addInputResource(VirtualFile(src))
        launcher.environment.noClasspath = true
        launcher.environment.isAutoImports = true

        val model = launcher.buildModel()
        val errors = getErrors(launcher.modelBuilder)

        return if (errors.isEmpty()) {
            val types = model.allTypes
                .mapNotNull { parseJavaType(it) }
                .toHashSet()
            ParseResult.Ok(types)
        } else {
            ParseResult.Error(errors)
        }
    }

    private fun getErrors(modelBuilder: SpoonModelBuilder): Set<ParseError> {
        // Kinda hacky, honestly... no idea how to get errors
        // without disabling "noClasspath" mode, though
        if (modelBuilder !is JDTBasedSpoonCompiler) {
            // TODO: Log this; not sure why/when it would change
            return emptySet()
        }

        return modelBuilder.problems
            .filter { isNotableProblem(it) }
            .map { ParseError(
                // TODO: Construct custom, more user-friendly messages
                message = it.message,
                lineNum = it.sourceLineNumber,
                colStart = it.sourceStart,
                colEnd = it.sourceEnd
            )}.toHashSet()
    }

    private fun isNotableProblem(problem: CategorizedProblem) = with(problem) {
        isError && id != IProblem.PublicClassMustMatchFileName
    }

    private fun parseJavaType(type: CtType<*>): JavaType? {
        return when (type) {
            is CtEnum -> parseJavaEnum(type)
            is CtClass -> parseJavaClass(type)
            is CtInterface -> parseJavaInterface(type)
            else -> null
        }
    }

    private fun parseJavaEnum(type: CtEnum<*>) = JavaType.Enum(
        name = type.qualifiedName,
        modifiers = type.modifiers.map { it.name }.toHashSet(),
        interfaces = type.superInterfaces.map { it.qualifiedName }.toHashSet(),
        enumValues = type.enumValues.map { it.simpleName }.toHashSet(),
        fields = parseFieldRefs(type.declaredFields),
        methods = parseMethods(type.methods),
        constructors = parseConstructors(type.constructors),
        nestedTypes = type.nestedTypes.mapNotNull { parseJavaType(it) }.toHashSet()
    )

    private fun parseJavaClass(type: CtClass<*>) = JavaType.Class(
        name = type.qualifiedName,
        modifiers = type.modifiers.map { it.toString() }.toHashSet(),
        parentClass = type.superclass?.qualifiedName ?: "java.lang.Object",
        interfaces = type.superInterfaces.map { it.qualifiedName }.toHashSet(),
        fields = parseFields(type.fields),
        methods = parseMethods(type.methods),
        constructors = parseConstructors(type.constructors),
        nestedTypes = type.nestedTypes.mapNotNull { parseJavaType(it) }.toHashSet()
    )

    private fun parseJavaInterface(type: CtInterface<*>) = JavaType.Interface(
        name = type.qualifiedName,
        modifiers = type.modifiers.map { it.toString() }.toHashSet(),
        interfaces = type.superInterfaces.map { it.qualifiedName }.toHashSet(),
        fields = parseFields(type.fields),
        methods = parseMethods(type.methods),
        nestedTypes = type.nestedTypes.mapNotNull { parseJavaType(it) }.toHashSet()
    )

    private fun parseMethods(methods: MutableSet<CtMethod<*>>) = methods.map {
        JavaMethod(
            name = it.simpleName,
            params = parseMethodParams(it.parameters),
            returnType = it.type.qualifiedName,
            modifiers = it.modifiers.map { modifierKind -> modifierKind.toString() }.toHashSet()
        )
    }.toHashSet()

    private fun parseConstructors(constructors: Set<CtConstructor<*>>) = constructors.map {
        JavaMethod(
            name = it.simpleName,
            params = parseMethodParams(it.parameters),
            returnType = it.type.qualifiedName,
            modifiers = it.modifiers.map { modifierKind -> modifierKind.toString() }.toHashSet()
        )
    }.toHashSet()

    private fun parseFieldRefs(fieldRefs: Collection<CtFieldReference<*>>) = fieldRefs.map { parseField(it.fieldDeclaration) }.toHashSet()

    private fun parseFields(fields: MutableList<CtField<*>>) = fields.map { parseField(it) }.toHashSet()

    private fun parseMethodParams(params: MutableList<CtParameter<*>>) = params.map { parseField(it) }.toHashSet()

    private fun parseField(field: CtVariable<*>) = JavaVariable(
        name = field.simpleName,
        type = field.type.qualifiedName,
        modifiers = field.modifiers.map { modifierKind -> modifierKind.toString() }.toHashSet()
    )
}