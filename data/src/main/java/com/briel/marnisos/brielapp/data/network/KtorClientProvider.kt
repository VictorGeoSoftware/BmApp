package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

object KtorClientProvider {
    @Volatile
    private var _baseUrl: String = "http://0.0.0.0:8081/api/v1" // Emulator -> host machine default

    fun setBaseUrl(url: String) {
        _baseUrl = url.trimEnd('/')
    }

    val baseUrl: String
        get() = _baseUrl

    val client: HttpClient by lazy {
        HttpClient(OkHttp) {
            engine {
                config {
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(30, TimeUnit.SECONDS)
                    writeTimeout(30, TimeUnit.SECONDS)
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

            install(Logging) {
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }

            install(DefaultRequest) {
                headers.append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }
    }

    fun close() {
        runCatching { client.close() }
    }
}
