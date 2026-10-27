package com.volumepilot.service.overlay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ServiceStateTracker {

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    private val _lastHeartbeat = MutableStateFlow(0L)
    val lastHeartbeat: StateFlow<Long> = _lastHeartbeat.asStateFlow()

    fun setRunning(running: Boolean) {
        _isServiceRunning.value = running
        if (running) {
            recordHeartbeat()
        }
    }

    fun recordHeartbeat() {
        _lastHeartbeat.value = System.currentTimeMillis()
    }
}
