package com.example

import com.example.connection.createConnectionPool
import com.example.connection.handleMysqlRequest
import com.example.dtos.UserDTO
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.mysql.MySQLQueryResult
import com.github.jasync.sql.db.util.length
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.*
import java.util.concurrent.CompletableFuture


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


val connectionPool = createConnectionPool()

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    println("STARTING ITJUANA HW PROJECT")

    if (System.getenv("ITJ_ENV_MODE") == "dev"){
        println("STARTED IN DOCKER/DEV MODE")
    }

    if (connectionPool == null) {
        throw error("DB Connection Error: Missing database environment variables.")
    }

    install(CORS){
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        method(HttpMethod.Post)
        method(HttpMethod.Get)
        allowCredentials = true
        allowNonSimpleContentTypes = true
        host("localhost:4200")
        anyHost()
    }

    connectionPool.connect().get()

    HttpClient(Apache) {
    }

    install(ContentNegotiation){
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }


    routing {
        get("/users/{userID?}") {
            errorAware {
                withContext(Dispatchers.IO) {
                    val userID = call.parameters["userID"]?.toIntOrNull()
                    if (userID != null) {
                        val userResponse = handleMysqlRequest(connectionPool, "select * from users where userID = ?", listOf(userID))

                        if (userResponse.rows.length == 0) {
                            call.respondErrorJson("User ID $userID not found.", HttpStatusCode.NotFound)
                        }
                        val userHashMap: HashMap<String, Any> = HashMap<String, Any>()
                        userHashMap["first_name"] = userResponse.rows[0][0].toString()
                        userHashMap["last_name"] = userResponse.rows[0][1].toString()
                        userHashMap["email"] = userResponse.rows[0][2].toString()
                        userHashMap["user_id"] = userResponse.rows[0][3]!!

                        call.respond(userHashMap)
                        return@withContext
                    }
                    val future: CompletableFuture<QueryResult> =
                            connectionPool.sendPreparedStatement("select * from users")
                    val queryResult = future.get()

                    val users = queryResult.rows.map {
                        val userHashMap: HashMap<String, Any> = HashMap<String, Any>()
                        userHashMap["first_name"] = it[0].toString()
                        userHashMap["last_name"] = it[1].toString()
                        userHashMap["email"] = it[2].toString()
                        userHashMap["user_id"] = it[3]!!
                        userHashMap
                    }

                    call.respond(users)
                }
            }
        }

        post("/users") {
            errorAware {
                withContext(Dispatchers.IO) {
                    val user = call.receive<UserDTO>()
                    if (user.email == null || user.firstName == null || user.lastName == null){
                        call.respondErrorJson("Missing values in request body for user.", HttpStatusCode.BadRequest)
                    }
                    val values : List<String> = listOf(user.firstName!!, user.lastName!!, user.email!!)
                    val response =
                            handleMysqlRequest(connectionPool, "INSERT INTO users (firstName, lastName, email) VALUES (?, ?, ?)", values)
                    user.userID = (response as MySQLQueryResult).lastInsertId.toInt()
                    call.respond(user)
                }
            }
        }

        delete("/users/{userID?}") {
            errorAware {
                withContext(Dispatchers.IO) {
                    val userID = call.parameters["userID"]?.toIntOrNull()
                    if (userID == null){
                        call.respondErrorJson("User ID is not valid.", HttpStatusCode.BadRequest)
                    }
                    val response = handleMysqlRequest(connectionPool, "DELETE FROM users WHERE userID = ?", listOf(userID!!))

                    if (response.rowsAffected.toInt() == 0) {
                        call.respondErrorJson("User ID $userID not found.", HttpStatusCode.NotFound)
                        return@withContext
                    }
                    call.respondSuccessJson("User ID $userID deleted")
                }
            }
        }

        patch("/users/{userID}") {
            errorAware {
                withContext(Dispatchers.IO) {
                    val userID = call.parameters["userID"]?.toIntOrNull()
                    val user = call.receive<UserDTO>()
                    if (userID == null) call.respondErrorJson("User ID is not valid.", HttpStatusCode.BadRequest)

                    val values = listOf(user.firstName!!, user.lastName!!, user.email!!, userID!!)
                    handleMysqlRequest(connectionPool, "UPDATE users SET firstName = IFNULL(?, firstName), lastName = IFNULL(?, lastName), email = IFNULL(?, email) WHERE userID = ?", values)

                    val userResponse = handleMysqlRequest(connectionPool, "select * from users where userID = ?", listOf(userID))

                    if (userResponse.rows.length == 0) {
                        call.respondErrorJson("User ID $userID not found.", HttpStatusCode.NotFound)
                    }
                    val userHashMap: HashMap<String, Any> = HashMap<String, Any>()
                    userHashMap["first_name"] = userResponse.rows[0][0].toString()
                    userHashMap["last_name"] = userResponse.rows[0][1].toString()
                    userHashMap["email"] = userResponse.rows[0][2].toString()
                    userHashMap["user_id"] = userResponse.rows[0][3]!!

                    call.respond(userHashMap)
                }
            }
        }
    }
}


private suspend fun <R> PipelineContext<*, ApplicationCall>.errorAware(block: suspend () -> R): R? {
    return try {
        block()
    } catch (e: Exception) {
        call.respondErrorJson("$e", HttpStatusCode.InternalServerError)
        null
    }
}


private suspend fun ApplicationCall.respondSuccessJson(value: String) = respond("""{"success": "$value"}""")

private suspend fun ApplicationCall.respondErrorJson(value: String, errorCode: HttpStatusCode) = respondText("""{"error": "$value"}""", ContentType.parse("application/json"), errorCode)