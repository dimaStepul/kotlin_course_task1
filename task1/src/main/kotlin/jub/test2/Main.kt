package jub.test2

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file

import jub.test2.api.Coordinates
import jub.test2.api.QueryHandler
import jub.test2.api.WeatherResponse
import kotlinx.coroutines.*

import org.jetbrains.kotlin.com.google.gson.Gson
import org.jetbrains.kotlin.com.google.gson.reflect.TypeToken

import java.io.File
import java.net.URLEncoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.net.ConnectException
import java.net.SocketTimeoutException

var OPEN_WEATHER_TOKEN: String = System.getenv("OPEN_WEATHER_TOKEN") ?: run {
    println("Please, add your OPEN_WEATHER_TOKEN in the environment variables")
    ""
}
@JvmInline
value class City(val name: String)

fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")

fun calculatePadding(data: List<String>) = (data.maxOfOrNull { it.length } ?: 0) + 2

fun parseCitiesFromFile(file: File): List<City> {
    return file.readLines().map { City(it.trim()) }
}

suspend fun fetchWeatherData(city: String) {
    try {
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd yyyy"))
        val coordinates = QueryHandler.getCityCoordinates(city, OPEN_WEATHER_TOKEN, limit = 1)
        val weatherResponse = QueryHandler.getWeather(coordinates.first(), OPEN_WEATHER_TOKEN)
        val padding = calculatePadding(listOf("Name", "Temperature", "Feels Like"))
        val cityName = city.padEnd(padding)
        val temperature = "${weatherResponse.weather.temp} °C".padEnd(padding)
        val feelsLike = "${weatherResponse.weather.feelsLike} °C".padEnd(padding)

        println("Name: $cityName $dateTime: $temperature feels like $feelsLike")
    } catch (e: ConnectException) {
        println("Connection error while fetching weather data for $city: ${e.message}")
    } catch (e: SocketTimeoutException) {
        println("Timeout error while fetching weather data for $city: ${e.message}")
    } catch (e: NoSuchElementException) {
        println("Error fetching weather data for $city: ${e.message}")
    }
}

object Parser {
    fun parseWeatherResponseJson(body: String): WeatherResponse = Gson().fromJson(body, WeatherResponse::class.java)

    fun parseCoordinatesResponseJson(body: String): List<Coordinates> {
        val typeToken = object : TypeToken<List<Coordinates>>() {}.type
        return Gson().fromJson(body, typeToken)
    }

    fun parseWeatherResponseXml(body: String): WeatherResponse = TODO("use $body if needed")

    fun parseWeatherResponseHtml(body: String): WeatherResponse = TODO("use $body if needed")
}

class WeatherCli : CliktCommand() {
    private val inputFile by argument("inputFile", help = "Path to the input file (.txt)").file(mustExist = true)
    override fun run() {
        runBlocking {
            val cities = parseCitiesFromFile(inputFile)

            if (cities.isEmpty()) {
                echo("No cities found in the input file.")
                return@runBlocking
            }

            val weatherJobs = cities.map { city ->
                launch  {
                    fetchWeatherData(city.name)
                }
            }

            weatherJobs.joinAll()
        }
    }
}

fun main(args: Array<String>) = WeatherCli().main(args)
