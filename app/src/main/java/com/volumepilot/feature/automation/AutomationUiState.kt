package com.volumepilot.feature.automation

import com.volumepilot.domain.model.AutomationConfig
import com.volumepilot.domain.model.VolumeProfile

data class AutomationUiState(
    val config: AutomationConfig = AutomationConfig(),
    val profiles: List<VolumeProfile> = emptyList(),
    val isLoading: Boolean = false,
    val exportedJson: String? = null,
    val feedbackMessage: String? = null,
    val errorMessage: String? = null
)
