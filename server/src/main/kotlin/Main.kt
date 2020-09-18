import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import parsing.JavaSourceParser

fun main(args: Array<String>) {
    println("Working Directory = ${System.getProperty("user.dir")}")

    embeddedServer(
        factory = Netty,
        port = 8080,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(CachingHeaders) {
        options { outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Application.Json -> CachingOptions(CacheControl.MaxAge(0, mustRevalidate = true))
                else -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
            }
        }
    }
    install(Routing) {
        post("/api/v1/parseJavaSource") {
            val srcParam = "javaSrc"
            val javaSrc = call.receiveParameters()[srcParam]
            val parseResultJson = javaSrc?.let {
                val sourceParser = JavaSourceParser()
                val parseResult = sourceParser.parse(it)

                Json.encodeToString(parseResult)
            }

            if (parseResultJson != null) {
                call.respondText(parseResultJson, ContentType.Application.Json)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Missing parameter '$srcParam'")
            }
        }
        static {
            files("client_static")
            default("client_static/index.html")
        }
    }
}