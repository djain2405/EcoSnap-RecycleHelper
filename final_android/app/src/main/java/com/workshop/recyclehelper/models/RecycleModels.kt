package com.workshop.recyclehelper.models

import androidx.compose.ui.graphics.Color

/**
 * RecycleCategory - The three possible classification outcomes
 *
 * This enum represents whether an item is recyclable, landfill, or unknown
 * Same as the iOS version's RecycleCategory enum
 */
enum class RecycleCategory {
    RECYCLABLE,
    LANDFILL,
    NOT_SURE;

    /**
     * User-friendly display name with emoji
     */
    val displayName: String
        get() = when (this) {
            RECYCLABLE -> "♻️ Likely Recyclable"
            LANDFILL -> "🗑️ Probably Landfill"
            NOT_SURE -> "❓ Not Sure"
        }

    /**
     * Color for UI display
     */
    val color: Color
        get() = when (this) {
            RECYCLABLE -> Color(0xFF4CAF50)  // Green
            LANDFILL -> Color(0xFF757575)     // Gray
            NOT_SURE -> Color(0xFFFF9800)     // Orange
        }

    /**
     * Helper message for the user
     */
    val message: String
        get() = when (this) {
            RECYCLABLE -> "This item appears to be recyclable. Please rinse and check local guidelines."
            LANDFILL -> "This item likely goes in the landfill or compost."
            NOT_SURE -> "I'm not confident about this item. Please check your local recycling rules."
        }
}

/**
 * RecycleResult - Complete classification result
 *
 * Contains all information about a classification:
 * - category: recyclable/landfill/not sure
 * - confidence: how confident the model is (0.0 to 1.0)
 * - detectedLabel: raw output from the ML model
 *
 * This is a data class (like a struct in Swift)
 */
data class RecycleResult(
    val category: RecycleCategory,
    val confidence: Float,
    val detectedLabel: String
) {
    /**
     * Confidence as a percentage (0-100)
     */
    val confidencePercentage: Int
        get() = (confidence * 100).toInt()
}
