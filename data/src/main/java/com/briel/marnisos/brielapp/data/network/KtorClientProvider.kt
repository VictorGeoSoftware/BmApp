package com.briel.marnisos.brielapp.data.network

import com.briel.marnisos.brielapp.data.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

object KtorClientProvider {
    @Volatile
    private var _baseUrl: String = BuildConfig.API_BASE_URL

    private val isProdRelease: Boolean
        get() = BuildConfig.BUILD_TYPE == "release" && BuildConfig.FLAVOR == "prod"

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

            install(Logging) {
                logger = Logger.SIMPLE
                level = if (isProdRelease) LogLevel.NONE else LogLevel.BODY
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

            install(DefaultRequest) {
                headers.append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }
    }
}
