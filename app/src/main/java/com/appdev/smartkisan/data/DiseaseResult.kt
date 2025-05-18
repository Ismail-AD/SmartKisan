package com.appdev.smartkisan.data


data class DiseaseResult(
    val diseaseName: String,
    val confidence: Int = 0,
    val causedBy:List<String> = emptyList(),
    val treatments: List<String> = emptyList()
)