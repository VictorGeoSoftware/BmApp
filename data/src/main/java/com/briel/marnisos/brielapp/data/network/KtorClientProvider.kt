package com.briel.marnisos.brielapp.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

object KtorClientProvider {
    @Volatile
    private var _baseUrl: String = "http://10.0.2.2:8081/api/v1" // Emulator -> host machine default
//    private var _baseUrl: String = "http://0.0.0.0:8081/api/v1" // Unit test

    fun setBaseUrl(url: String) {
        _baseUrl = url.trimEnd('/')
    }

    val baseUrl: String
        get() = _baseUrl

    val client: HttpClient by lazy {
        HttpClient(OkHttp) {
            engine {
                config {
                    connectTimeout(5, TimeUnit.MINUTES)
                    readTimeout(5, TimeUnit.MINUTES)
                    writeTimeout(5, TimeUnit.MINUTES)
                }
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                        isLenient = true
                    }
                )
            }

            // Logging disabled to reduce overhead
            // install(Logging) {
            //     logger = Logger.ANDROID
            //     level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
            // }

            install(DefaultRequest) {
                headers.append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }
    }

    fun close() {
        runCatching { client.close() }
    }
}
