package parsertests

import parsing.models.JavaMethod
import parsing.models.JavaType
import parsing.models.JavaVariable
import kotlin.contracts.ExperimentalContracts
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalContracts
class JavaClassTests {
    @Test fun `class has correct name`() {
        val expectedClassName = "Test"
        val parsedClassName = fromSource<JavaType.Class> {
            "class $expectedClassName { }"
        }.name

        assertEquals(expectedClassName, parsedClassName)
    }

    @Test fun `class has correct modifiers`() {
        val expectedModifiers = setOf("public", "abstract")
        val parsedModifiers = fromSource<JavaType.Class> {
            "public abstract class PublicAbstract { }"
        }.modifiers

        assertEquals(expectedModifiers, parsedModifiers)
    }

    @Test fun `class has correct implicit parentClass`() {
        val expectedParentClass = "java.lang.Object"
        val parsedParentClass = fromSource<JavaType.Class> {
            "public class NoInheritance { }"
        }.parentClass

        assertEquals(expectedParentClass, parsedParentClass)
    }

    @Test fun `class has correct explicit parentClass`() {
        val expectedParentClass = "java.lang.Thread"
        val parsedParentClass = fromSource<JavaType.Class> {
            "public class SomeTask extends Thread { }"
        }.parentClass

        assertEquals(expectedParentClass, parsedParentClass)
    }

    @Test fun `class has correct interfaces`() {
        val expectedInterfaces = setOf("java.lang.Runnable", "java.io.Closeable")
        val parsedInterfaces = fromSource<JavaType.Class> {
            """import java.io.Closeable;
                
            public class RunnableAndCloseable implements Runnable, Closeable {
                public void run() { }
                public void close() { }
            }
            """
        }.interfaces

        assertEquals(expectedInterfaces, parsedInterfaces)
    }

    @Test fun `class has correct implicit constructor`() {
        val className = "Boring"
        val expectedConstructors = setOf(
            JavaMethod(
                name = "<init>",
                params = emptySet(),
                returnType = className,
                modifiers = setOf("public")
            )
        )

        val parsedConstructors = fromSource<JavaType.Class> {
            "public class $className { }"
        }.constructors

        assertEquals(expectedConstructors, parsedConstructors)
    }

    @Test fun `class has correct explicit constructors`() {
        val className = "Boring"
        val expectedConstructors = setOf(
            JavaMethod(
                name = "<init>",
                params = setOf(
                    JavaVariable(name = "p1", type = "int", modifiers = emptySet()),
                    JavaVariable(name = "p2", type = "int", modifiers = setOf("final")),
                ),
                returnType = className,
                modifiers = setOf("private")
            )
        )

        val parsedConstructors = fromSource<JavaType.Class> {
            """public class $className {
                private $className(int p1, final int p2) { }
            }"""
        }.constructors

        assertEquals(expectedConstructors, parsedConstructors)
    }

    @Test fun `class has correct fields`() {
        val expectedFields = setOf(
            JavaVariable(name = "intField", type = "int", modifiers = setOf("public", "final")),
            JavaVariable(name = "stringField", type = "java.lang.String", modifiers = setOf("private")),
        )

        val parsedFields = fromSource<JavaType.Class> {
            """public class HasFields {
                public final int intField;
                private String stringField;
            }"""
        }.fields

        assertEquals(expectedFields, parsedFields)
    }

    @Test fun `class has correct methods`() {
        val expectedMethods = setOf(
            JavaMethod(
                name = "aMethod",
                params = setOf(
                    JavaVariable(name = "input", type = "java.lang.String", emptySet())
                ),
                returnType = "java.lang.String",
                modifiers = setOf("synchronized")
            )
        )

        val parsedMethods = fromSource<JavaType.Class> {
            """public class HasAMethod {
                synchronized String aMethod(String input) { }
            }"""
        }.methods

        assertEquals(expectedMethods, parsedMethods)
    }

    @Test fun `class has correct nested types`() {
        val nestedClassName = "HasNestedType\$InnerClass"
        val expectedNestedTypes = setOf(
            JavaType.Class(
                name = nestedClassName,
                modifiers = setOf("public", "static"),
                parentClass = "java.lang.Object",
                interfaces = emptySet(),
                fields = setOf(
                    JavaVariable(name = "innerClassField", type = "int", modifiers = emptySet())
                ),
                methods = setOf(
                    JavaMethod(name = "innerClassMethod", params = emptySet(), returnType = "long", modifiers = emptySet())
                ),
                constructors = setOf(
                    JavaMethod(name = "<init>", params = emptySet(), returnType = nestedClassName, modifiers = emptySet())
                ),
                nestedTypes = emptySet()
            )
        )

        val parsedNestedTypes = fromSource<JavaType.Class> {
            """public class HasNestedType {
                public static class InnerClass {
                    int innerClassField;
                    
                    InnerClass() { }
                    
                    long innerClassMethod() { }
                }
            }"""
        }.nestedTypes

        assertEquals(expectedNestedTypes, parsedNestedTypes)
    }
}
