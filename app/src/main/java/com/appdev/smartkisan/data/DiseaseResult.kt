package com.appdev.smartkisan.data

data class DiseaseResult(
    val diseaseName: String,
    val diseaseImage: String? = "",
    val reasons: List<String>,
    val confirmation: List<String>
)
