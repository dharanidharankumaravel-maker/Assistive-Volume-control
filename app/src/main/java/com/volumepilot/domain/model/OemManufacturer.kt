package com.volumepilot.domain.model

enum class OemManufacturer(val displayName: String) {
    SAMSUNG("Samsung"),
    XIAOMI("Xiaomi / POCO / Redmi"),
    ONEPLUS_OPPO_REALME("OnePlus / Oppo / Realme"),
    VIVO("Vivo / iQOO"),
    HUAWEI("Huawei / Honor"),
    GOOGLE("Google Pixel"),
    GENERIC("Android Standard");

    companion object {
        fun detect(manufacturer: String): OemManufacturer {
            val lower = manufacturer.lowercase()
            return when {
                lower.contains("samsung") -> SAMSUNG
                lower.contains("xiaomi") || lower.contains("redmi") || lower.contains("poco") -> XIAOMI
                lower.contains("oneplus") || lower.contains("oppo") || lower.contains("realme") -> ONEPLUS_OPPO_REALME
                lower.contains("vivo") || lower.contains("iqoo") -> VIVO
                lower.contains("huawei") || lower.contains("honor") -> HUAWEI
                lower.contains("google") -> GOOGLE
                else -> GENERIC
            }
        }
    }
}

data class OemGuidance(
    val manufacturer: OemManufacturer,
    val headline: String,
    val explanation: String,
    val steps: List<String>,
    val settingsAction: String? = null
)
