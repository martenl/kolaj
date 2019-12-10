package de.kolaj

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.mustache.Mustache
import io.ktor.mustache.MustacheContent

import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.pipeline.PipelineContext

fun main(args: Array<String>) {

    embeddedServer(Netty, 23567) {
        install(ContentNegotiation) {
            jackson()
        }
        install(Mustache) {
            mustacheFactory = DefaultMustacheFactory("templates")
        }
        routing {
            static("/static") {
                resources("static")
            }
            route("/api", myApi())
            get("") {
                call.respond(MustacheContent("index.hbs", emptyMap<Any,Any>()))
            }
            get("hello", helloResponse())
            get("random/{min}/{max}") {
                val min = call.parameters["min"]?.toIntOrNull() ?: 0
                val max = call.parameters["max"]?.toIntOrNull() ?: 10
                val randomString = "${(min until max).shuffled().last()}"
                call.respond(mapOf("value" to randomString))
            }
        }
    }.start(wait = true)
}

private fun myApi(): Route.() -> Unit {
    return {
        get {
            call.respond("hello api")
        }
        get("/espanol") {
            call.respond("hola api")
        }
    }
}

private fun helloResponse(): suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit {
    return {
        println(call.parameters.get("blah"))
        call.respond(HttpStatusCode.Accepted, "Hello")
    }
}

private fun api() : suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit {
    return {

    }
}