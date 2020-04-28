package com.tor.javalinvue

import com.tor.javalinvue.service.User
import com.tor.javalinvue.service.UserService
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import io.javalin.plugin.openapi.annotations.*

data class ErrorResponse(
    val title: String,
    val status: Int,
    val type: String,
    val details: Map<String, String>?
)

object UserController {

    @OpenApi(
        summary = "Get all user",
        operationId = "getAllUser",
        tags = ["User"],
        responses = [OpenApiResponse("200", [OpenApiContent(Array<User>::class)])]
    )
    fun getAll(ctx: Context) {
        ctx.json(UserService.getAll())
//        ctx.json(users.map { it.copy(userDetails = null) }) // remove sensitive information
    }

    @OpenApi(
        summary = "Get user by ID",
        operationId = "getUserById",
        tags = ["User"],
        pathParams = [OpenApiParam("userId", Int::class, "The user ID")],
        responses = [
            OpenApiResponse("200", [OpenApiContent(User::class)]),
            OpenApiResponse("400", [OpenApiContent(ErrorResponse::class)]),
            OpenApiResponse("404", [OpenApiContent(ErrorResponse::class)])
        ]
    )
    fun getOne(ctx: Context) {
        ctx.json(UserService.findById(ctx.validPathParamUserId()) ?: throw NotFoundResponse())
//        val user = users.find { it.id == ctx.pathParam("user-id") } ?: throw NotFoundResponse()
//        ctx.json(user)
    }

    @OpenApi(
        summary = "Create user",
        operationId = "createUser",
        tags = ["User"],
        requestBody = OpenApiRequestBody([OpenApiContent(NewUserRequest::class)]),
        responses = [
            OpenApiResponse("201"),
            OpenApiResponse("400", [OpenApiContent(ErrorResponse::class)])]
    )
    fun create(ctx: Context) {
        val user = ctx.body<NewUserRequest>()
        UserService.save(name = user.name, email = user.email, userDetails = null)
        ctx.json(201)
    }

    @OpenApi(
        summary = "Update user by ID",
        operationId = "updateUserById",
        tags = ["User"],
        pathParams = [OpenApiParam("userId", Int::class, "The user id")],
        requestBody = OpenApiRequestBody([OpenApiContent(NewUserRequest::class)]),
        responses = [
            OpenApiResponse("204"),
            OpenApiResponse("400", [OpenApiContent(ErrorResponse::class)]),
            OpenApiResponse("404", [OpenApiContent(ErrorResponse::class)])
        ]

    )
    fun update(ctx: Context) {
        val user = UserService.findById(ctx.validPathParamUserId()) ?: throw NotFoundResponse("User not found")
        val newUser = ctx.body<NewUserRequest>()
        UserService.update(id = user.id, name = newUser.name, email = newUser.email, userDetails = null)
        ctx.status(204)
    }

    @OpenApi(
        summary = "Delete user by ID",
        operationId = "deleteUserById",
        tags = ["User"],
        pathParams = [OpenApiParam("userId", Int::class, "The user id")],
        responses = [
            OpenApiResponse("204"),
            OpenApiResponse("400", [OpenApiContent(ErrorResponse::class)]),
            OpenApiResponse("404", [OpenApiContent(ErrorResponse::class)])
        ]
    )
    fun delete(ctx: Context) {
        val user = UserService.findById(ctx.validPathParamUserId()) ?: throw NotFoundResponse("User not found")
        UserService.delete(user.id)
        ctx.status(204)
    }

}

class NewUserRequest(val name: String, val email: String)

// Prevent duplicate validation of userId
private fun Context.validPathParamUserId() = this.pathParam<Int>("userId").check({ it >= 0 }).get()