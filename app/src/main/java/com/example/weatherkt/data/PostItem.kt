package com.example.weatherkt.data

class PostItem {
    lateinit var weather: Array<Weather>
    var main: Main? = null
    var sys: Sys? = null
    private val base: String? = null

    override fun toString(): String {
        return "ClassPojo [weather = $weather, main = $main, sys = $sys]"
    }

    inner class Main {
        var temp: String? = null
        var feels_like: String? = null

        override fun toString(): String {
            return "ClassPojo [temp = $temp, feels_like = $feels_like]"
        }
    }

    inner class Weather {
        var main: String? = null

        override fun toString(): String {
            return "" + main
        }
    }

    inner class Sys {
        var sunrise: String? = null
        var sunset: String? = null

        override fun toString(): String {
            return "ClassPojo [sunrise = $sunrise, sunset = $sunset]"
        }
    }
}