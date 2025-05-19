package com.appdev.smartkisan.Utils

import com.appdev.smartkisan.presentation.feature.farmer.diseasedetection.DiseaseDetails
import com.example.plantdisease.model.CropType

object DiseaseDetailsProvider {
    // Map of disease details by crop type and disease name
    val DISEASE_DETAILS_MAP: Map<CropType, Map<String, DiseaseDetails>> = mapOf(
        CropType.RICE to mapOf(
            "Bacterial Blight" to DiseaseDetails(
                causes = listOf(
                    "Caused by Xanthomonas oryzae pv. oryzae bacteria",
                    "Spreads through irrigation water",
                    "Enters through wounds or natural openings in the plant"
                ),
                treatments = listOf(
                    "Use disease-resistant rice varieties",
                    "Maintain proper field drainage",
                    "Apply copper-based bactericides",
                    "Practice crop rotation with non-host crops"
                )
            ),
            "Brown Spot" to DiseaseDetails(
                causes = listOf(
                    "Caused by Cochliobolus miyabeanus fungus (Bipolaris oryzae)",
                    "Favored by nutrient deficiencies, especially potassium",
                    "Spreads during periods of high humidity"
                ),
                treatments = listOf(
                    "Ensure balanced crop nutrition with adequate potassium",
                    "Apply fungicides containing mancozeb or propiconazole",
                    "Use certified seeds",
                    "Avoid water stress"
                )
            ),
            "Leaf Blast" to DiseaseDetails(
                causes = listOf(
                    "Caused by Magnaporthe oryzae fungus (Pyricularia oryzae)",
                    "Favored by high nitrogen levels",
                    "Occurs in humid conditions with temperatures between 25-28°C (77-82°F)"
                ),
                treatments = listOf(
                    "Apply balanced fertilization (avoid excess nitrogen)",
                    "Use silicon-based fertilizers to increase resistance",
                    "Apply fungicides containing tricyclazole or azoxystrobin",
                    "Plant resistant varieties"
                )
            ),
            "Healthy" to DiseaseDetails(
                causes = listOf(),
                treatments = listOf(
                )
            )
        ),

        CropType.APPLE to mapOf(
            "Apple Scab" to DiseaseDetails(
                causes = listOf(
                    "Caused by Venturia inaequalis fungus",
                    "Overwinters in fallen leaves",
                    "Spreads during spring rain events"
                ),
                treatments = listOf(
                    "Apply fungicides from bud break until rainy season ends",
                    "Clean up and destroy fallen leaves in autumn",
                    "Prune trees to improve air circulation",
                    "Plant resistant apple varieties"
                )
            ),
            "Black Rot" to DiseaseDetails(
                causes = listOf(
                    "Caused by Botryosphaeria obtusa fungus (sexual stage of Diplodia seriata)",
                    "Infects through wounds or damaged tissue",
                    "Overwinters in cankers and mummified fruit"
                ),
                treatments = listOf(
                    "Prune out dead and diseased wood",
                    "Remove mummified fruit from trees",
                    "Apply fungicides from pink stage through harvest",
                    "Control insects that create entry wounds"
                )
            ),
            "Cedar Apple Rust" to DiseaseDetails(
                causes = listOf(
                    "Caused by Gymnosporangium juniperi-virginianae fungus",
                    "Requires both juniper and apple hosts to complete lifecycle",
                    "Spreads during spring rains"
                ),
                treatments = listOf(
                    "Remove nearby cedar/juniper trees if possible",
                    "Apply fungicides in spring during primary infection period",
                    "Plant resistant apple varieties",
                    "Ensure good air circulation around trees"
                )
            ),
            "Healthy" to DiseaseDetails(
                causes = listOf(),
                treatments = listOf(
                )
            )
        ),

        CropType.CORN to mapOf(
            "Cercospora Leaf Spot" to DiseaseDetails(
                causes = listOf(
                    "Caused by Cercospora zeae-maydis and Cercospora zeina fungus",
                    "Favored by warm, humid weather",
                    "Survives in crop residue"
                ),
                treatments = listOf(
                    "Rotate crops with non-host plants",
                    "Use resistant hybrids when available",
                    "Apply foliar fungicides",
                    "Practice deep plowing to bury infected residue"
                )
            ),
            "Common Rust" to DiseaseDetails(
                causes = listOf(
                    "Caused by Puccinia sorghi fungus",
                    "Spreads via windborne spores",
                    "Favored by cool to moderate temperatures (60-77°F) and moist conditions"
                ),
                treatments = listOf(
                    "Plant resistant corn hybrids",
                    "Apply fungicides early in disease development",
                    "Monitor fields regularly during growing season",
                    "Avoid excessive nitrogen fertilization"
                )
            ),
            "Northern Leaf Blight" to DiseaseDetails(
                causes = listOf(
                    "Caused by Exserohilum turcicum fungus (Setosphaeria turcica)",
                    "Survives in corn debris",
                    "Favored by moderate temperatures (64-81°F) and wet conditions"
                ),
                treatments = listOf(
                    "Plant resistant hybrids",
                    "Rotate with non-host crops",
                    "Apply foliar fungicides at early tasseling",
                    "Practice tillage to reduce infected residue"
                )
            ),
            "Healthy" to DiseaseDetails(
                causes = listOf(),
                treatments = listOf(
                )
            )
        ),

        CropType.TOMATO to mapOf(
            "Bacterial Spot" to DiseaseDetails(
                causes = listOf(
                    "Caused by Xanthomonas species bacteria (Xanthomonas vesicatoria, X. euvesicatoria, X. gardneri, X. perforans)",
                    "Spreads through water splash and handling wet plants",
                    "Favored by warm (75° to 86°F), wet weather"
                ),
                treatments = listOf(
                    "Use disease-free seeds and transplants",
                    "Apply copper-based bactericides",
                    "Avoid overhead irrigation",
                    "Practice crop rotation with non-solanaceous crops"
                )
            ),
            "Early Blight" to DiseaseDetails(
                causes = listOf(
                    "Caused by Alternaria solani and Alternaria tomatophila fungus",
                    "Survives in soil and plant debris",
                    "Favored by warm temperatures (75°F to 84°F) and high humidity"
                ),
                treatments = listOf(
                    "Prune lower leaves to improve air circulation",
                    "Apply fungicides at first sign of disease",
                    "Use mulch to prevent soil splash",
                    "Practice crop rotation"
                )
            ),
            "Late Blight" to DiseaseDetails(
                causes = listOf(
                    "Caused by Phytophthora infestans (oomycete or water mold)",
                    "Spreads rapidly in cool to moderate (60-80°F), wet conditions",
                    "Can destroy entire crop in days if untreated"
                ),
                treatments = listOf(
                    "Apply preventative fungicides before disease appears",
                    "Destroy infected plants immediately",
                    "Provide good drainage and air circulation",
                    "Plant resistant varieties when available"
                )
            ),
            "Leaf Mold" to DiseaseDetails(
                causes = listOf(
                    "Caused by Passalora fulva fungus (syn. Cladosporium fulvum)",
                    "Common in greenhouse and high tunnel environments",
                    "Thrives in high humidity conditions (above 85%)"
                ),
                treatments = listOf(
                    "Improve greenhouse ventilation",
                    "Reduce humidity levels",
                    "Apply fungicides labeled for leaf mold",
                    "Remove and destroy affected leaves"
                )
            ),
            "Septoria Leaf Spot" to DiseaseDetails(
                causes = listOf(
                    "Caused by Septoria lycopersici fungus",
                    "Overwinters in crop debris and on weeds in the nightshade family",
                    "Spreads through water splash"
                ),
                treatments = listOf(
                    "Apply fungicides at first sign of disease",
                    "Use mulch to prevent soil splash",
                    "Remove lower leaves once fruit sets",
                    "Practice 3-4 year crop rotation"
                )
            ),
            "Spider Mites" to DiseaseDetails(
                causes = listOf(
                    "Caused by Tetranychus urticae and related species",
                    "Thrive in hot, dry conditions",
                    "Reproduce rapidly in absence of natural predators"
                ),
                treatments = listOf(
                    "Increase humidity around plants",
                    "Apply insecticidal soap or horticultural oil",
                    "Introduce predatory mites",
                    "Regularly spray plants with water to disrupt webs"
                )
            ),
            "Target Spot" to DiseaseDetails(
                causes = listOf(
                    "Caused by Corynespora cassiicola fungus",
                    "Favored by warm (70-80°F), humid conditions",
                    "Spreads through water and air movement"
                ),
                treatments = listOf(
                    "Apply fungicides at first symptoms",
                    "Provide adequate spacing between plants",
                    "Remove infected leaves",
                    "Use drip irrigation instead of overhead watering"
                )
            ),
            "Yellow Leaf Curl Virus" to DiseaseDetails(
                causes = listOf(
                    "Caused by Tomato yellow leaf curl virus (TYLCV)",
                    "Transmitted by whiteflies (Bemisia tabaci)",
                    "Cannot be cured once plant is infected"
                ),
                treatments = listOf(
                    "Control whitefly populations",
                    "Use reflective mulches to repel whiteflies",
                    "Remove and destroy infected plants",
                    "Plant resistant varieties"
                )
            ),
            "Mosaic Virus" to DiseaseDetails(
                causes = listOf(
                    "Caused by various mosaic viruses (TMV, ToMV)",
                    "Spreads through contact and handling",
                    "Transmitted by tools, hands, and insects"
                ),
                treatments = listOf(
                    "Remove and destroy infected plants",
                    "Disinfect tools with 10% bleach solution",
                    "Wash hands after handling plants",
                    "Use resistant varieties"
                )
            ),
            "Healthy" to DiseaseDetails(
                causes = listOf(),
                treatments = listOf(
                )
            )
        ),

        CropType.POTATO to mapOf(
            "Early Blight" to DiseaseDetails(
                causes = listOf(
                    "Caused by Alternaria solani fungus (and other Alternaria species)",
                    "Favored by alternating wet and dry conditions",
                    "More severe on stressed or aging plants"
                ),
                treatments = listOf(
                    "Apply fungicides preventatively",
                    "Maintain plant vigor with proper nutrition",
                    "Practice crop rotation",
                    "Provide adequate irrigation"
                )
            ),
            "Late Blight" to DiseaseDetails(
                causes = listOf(
                    "Caused by Phytophthora infestans (oomycete or water mold)",
                    "Destroys leaves, stems, and tubers",
                    "Spreads rapidly in cool to moderate (50-78°F), wet weather"
                ),
                treatments = listOf(
                    "Apply fungicides before disease appears",
                    "Destroy volunteer potato plants",
                    "Plant certified disease-free seed potatoes",
                    "Harvest during dry weather to protect tubers"
                )
            ),
            "Healthy" to DiseaseDetails(
                causes = listOf(),
                treatments = listOf(
                )
            )
        ),

        CropType.GRAPES to mapOf(
            "Black Rot" to DiseaseDetails(
                causes = listOf(
                    "Caused by Guignardia bidwellii fungus (Phyllosticta ampelicida)",
                    "Overwinters in mummified berries, cane and tendril lesions, and fallen leaves",
                    "Spreads during warm (60-90°F), rainy periods"
                ),
                treatments = listOf(
                    "Apply fungicides from early shoot growth to berry development",
                    "Remove mummified fruit and diseased wood",
                    "Improve air circulation through proper pruning",
                    "Manage canopy to reduce humidity"
                )
            ),
            "Esca (Black Measles)" to DiseaseDetails(
                causes = listOf(
                    "Caused by complex of fungi including Phaeomoniella chlamydospora and Phaeoacremonium spp.",
                    "Enters through pruning wounds",
                    "Long-term trunk disease, more severe on stressed vines"
                ),
                treatments = listOf(
                    "Prune during dry weather",
                    "Protect pruning wounds with sealants",
                    "Remove severely infected vines",
                    "Maintain vine vigor through proper nutrition"
                )
            ),
            "Leaf Blight" to DiseaseDetails(
                causes = listOf(
                    "Caused by Pseudocercospora vitis fungus (formerly Isariopsis clavispora)",
                    "Favored by warm, humid conditions",
                    "More common in late summer"
                ),
                treatments = listOf(
                    "Apply fungicides after bloom",
                    "Improve air circulation with proper pruning",
                    "Remove fallen leaves after season",
                    "Ensure balanced vine nutrition"
                )
            ),
            "Healthy" to DiseaseDetails(
                causes = listOf(),
                treatments = listOf(
                )
            )
        ),

        CropType.ORANGE to mapOf(
            "Citrus canker" to DiseaseDetails(
                causes = listOf(
                    "Caused by Xanthomonas citri subsp. citri bacteria",
                    "Spreads through wind-driven rain and tools",
                    "Enters through natural openings and wounds"
                ),
                treatments = listOf(
                    "Remove and destroy infected trees in severe cases",
                    "Apply copper-based bactericides",
                    "Create windbreaks to reduce spread",
                    "Disinfect pruning tools between trees"
                )
            ),
            "Citrus greening" to DiseaseDetails(
                causes = listOf(
                    "Caused by Candidatus Liberibacter asiaticus bacteria",
                    "Transmitted by Asian citrus psyllid (Diaphorina citri)",
                    "Affects all parts of the tree"
                ),
                treatments = listOf(
                    "Control psyllid populations with insecticides",
                    "Remove and destroy infected trees",
                    "Utilize reflective mulch to repel psyllids",
                    "Consult with local agricultural extension service"
                )
            ),
            "Citrus mealybugs" to DiseaseDetails(
                causes = listOf(
                    "Caused by various mealybug species (Planococcus citri is common)",
                    "Secrete honeydew that leads to sooty mold",
                    "Feed on plant sap, weakening the tree"
                ),
                treatments = listOf(
                    "Introduce natural predators like ladybugs and lacewings",
                    "Apply horticultural oil or insecticidal soap",
                    "Prune heavily infested branches",
                    "Address ant populations that protect mealybugs"
                )
            ),
            "Powdery mildew" to DiseaseDetails(
                causes = listOf(
                    "Caused by Oidium species fungus (Oidium citri and O. tingitaninum)",
                    "Favored by moderate temperatures (60-80°F) and high humidity",
                    "Does not require leaf wetness to spread"
                ),
                treatments = listOf(
                    "Apply sulfur or potassium bicarbonate sprays",
                    "Improve air circulation through pruning",
                    "Avoid excessive nitrogen fertilization",
                    "Water in morning so leaves dry during day"
                )
            ),
            "Spiny whitefly" to DiseaseDetails(
                causes = listOf(
                    "Caused by Aleurocanthus spiniferus and related species",
                    "Suck plant sap leading to weakened trees",
                    "Secrete honeydew causing sooty mold growth"
                ),
                treatments = listOf(
                    "Apply insecticidal oils or soaps",
                    "Introduce parasitic wasps as biological control",
                    "Monitor and treat early infestations",
                    "Use yellow sticky traps to reduce populations"
                )
            ),
            "Healthy Leaf" to DiseaseDetails(
                causes = listOf(),
                treatments = listOf(
                )
            )
        )
    )
}