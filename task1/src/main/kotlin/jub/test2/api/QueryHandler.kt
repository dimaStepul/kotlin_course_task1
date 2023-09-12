package jub.test2.api

import jub.test2.Parser.parseCoordinatesResponseJson
import jub.test2.Parser.parseWeatherResponseHtml
import jub.test2.Parser.parseWeatherResponseJson
import jub.test2.Parser.parseWeatherResponseXml
import jub.test2.utf8
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.org.apache.http.HttpStatus
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object QueryHandler {
    fun getResponseBody(url: String, urlParams: String): String {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$url?$urlParams"))
            .build()

        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != HttpStatus.SC_OK) {
                return "Sorry, you sent an invalid query"
            }
            return response.body() ?: ""
        } catch (e: IllegalStateException) {
            return "An error occurred: ${e.message}"
        }
    }


    suspend fun getCityCoordinates(
        city: String,
        appKey: String,
        limit: Int = 5,
    ): List<Coordinates> = withContext(Dispatchers.IO) {
        val params = mutableMapOf("q" to city, "limit" to "$limit", "appid" to appKey)
        val urlParams = params.map { (k, v) -> "${(k.utf8())}=${v.utf8()}" }
            .joinToString("&")
        val responseBody = getResponseBody(OpenWeatherUrls.GEO_URL, urlParams)
        parseCoordinatesResponseJson(responseBody)
    }

    suspend fun getWeather(
        coordinates: Coordinates,
        appKey: String,
        mode: ResponseFormat = ResponseFormat.JSON,
        temperatureUnit: String = "metric",
    ): WeatherResponse = withContext(Dispatchers.IO) {
        val params = mutableMapOf(
            "lat" to "${coordinates.lat}",
            "lon" to "${coordinates.lon}",
            "appid" to appKey,
            "units" to temperatureUnit
        )
        if (mode == ResponseFormat.XML || mode == ResponseFormat.HTML) {
            params["mode"] = mode.name
        }
        val urlParams = params.map { (k, v) -> "${(k.utf8())}=${v.utf8()}" }
            .joinToString("&")
        val responseBody = getResponseBody(OpenWeatherUrls.CURRENT_WEAHTER_URL, urlParams)

        when (mode) {
            ResponseFormat.JSON -> parseWeatherResponseJson(responseBody)
            ResponseFormat.HTML -> parseWeatherResponseHtml(responseBody)
            ResponseFormat.XML -> parseWeatherResponseXml(responseBody)
        }
    }

    enum class ResponseFormat {
        HTML, JSON, XML
    }

    object OpenWeatherUrls {
        const val GEO_URL = "http://api.openweathermap.org/geo/1.0/direct"
        const val CURRENT_WEAHTER_URL = "https://api.openweathermap.org/data/2.5/weather"
    }
}
