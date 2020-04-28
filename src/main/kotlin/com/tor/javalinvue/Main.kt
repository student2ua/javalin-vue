package com.tor.javalinvue

import cc.vileda.openapi.dsl.info
import cc.vileda.openapi.dsl.openapiDsl
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.Role
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.core.util.Header
import io.javalin.http.Context
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.ReDocOptions
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.javalin.plugin.rendering.vue.JavalinVue
import io.javalin.plugin.rendering.vue.VueComponent

enum class AppRole : Role { ANYONE, LOGGED_IN }

fun main() {
    val port = 8080
    val app = Javalin.create { config ->
                config.registerPlugin(getConfiguredApiPlugin())
        config.defaultContentType = "application/json"

//        config.addStaticFiles("/static/css");

        config.enableWebjars()
      /*  config.accessManager { handler, ctx, permRoles ->
            when {
                AppRole.ANYONE in permRoles -> handler.handle(ctx)
//                AppRole.LOGGED_IN in permRoles && anyUsernameProvided(ctx) -> handler.handle(ctx)
                AppRole.LOGGED_IN in permRoles && currentUser(ctx) != null -> handler.handle(ctx)
                else -> ctx.status(401).header(Header.WWW_AUTHENTICATE, "Basic")
            }
        }*/
        config.enableDevLogging()
        JavalinVue.stateFunction = { ctx -> mapOf("currentUser" to currentUser(ctx)) }
    }.start(port)

    println("Check out ReDoc docs at http://localhost:${port}/redoc")
    println("Check out Swagger UI docs at http://localhost:${port}/swagger-ui")

    app.get("/", VueComponent("<hello-world></hello-world>"), roles(AppRole.ANYONE))
    app.get("/users", VueComponent("<user-overview></user-overview>"), roles(AppRole.ANYONE))
    app.get("/users/:userId", VueComponent("<user-profile></user-profile>"), roles(AppRole.ANYONE))
    app.error(404, "html", VueComponent("<not-found></not-found>"))
//    app.error(401, "html", VueComponent("<not-found></not-found>"))
//    app.get("/api/users", UserController::getAll, roles(AppRole.ANYONE))
//    app.get("/api/users/:userId", UserController::getOne, roles(AppRole.LOGGED_IN))
   app.routes {
        path("/api/users") {
            get(UserController::getAll, roles(AppRole.ANYONE))
            post(UserController::create, roles(AppRole.ANYONE))
            path(":userId") {
                get(UserController::getOne, roles(AppRole.ANYONE))
                patch(UserController::update, roles(AppRole.ANYONE))
                delete(UserController::delete, roles(AppRole.ANYONE))
            }
        }
    }
}

fun getConfiguredApiPlugin() = OpenApiPlugin(
    OpenApiOptions {
        openapiDsl {
            info {
                title = "User API"
                description = "Demo API with 5 operations"
                version = "1.0.0"

            }
        }
    }.apply {
        path("/swagger-docs") // endpoint for OpenAPI json
        swagger(SwaggerOptions("/swagger-ui"))//endpoint for swagger-ui
        reDoc(ReDocOptions("/redoc")) //endpoint for redoc
        defaultDocumentation { doc ->
            doc.json("500", ErrorResponse::class.java)
            doc.json("503", ErrorResponse::class.java)
        }
    })


fun anyUsernameProvided(ctx: Context): Boolean = ctx
    .basicAuthCredentials()?.username.isNotBlank() == true

private fun currentUser(ctx: Context) =
    if (ctx.basicAuthCredentialsExist()) ctx.basicAuthCredentials().username else null
