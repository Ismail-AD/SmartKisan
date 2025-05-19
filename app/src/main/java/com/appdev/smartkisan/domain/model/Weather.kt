package com.appdev.smartkisan.domain.model

data class Weather(
    val base: String,
    val clouds: com.appdev.smartkisan.domain.model.Clouds,
    val cod: Int,
    val coord: com.appdev.smartkisan.domain.model.Coord,
    val dt: Int,
    val id: Int,
    val main: com.appdev.smartkisan.domain.model.Main,
    val name: String,
    val rain: com.appdev.smartkisan.domain.model.Rain,
    val sys: com.appdev.smartkisan.domain.model.Sys,
    val timezone: Int,
    val visibility: Int,
    val weather: List<com.appdev.smartkisan.domain.model.WeatherX>,
    val wind: com.appdev.smartkisan.domain.model.Wind
)