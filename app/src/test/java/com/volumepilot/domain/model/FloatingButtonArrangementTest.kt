package com.volumepilot.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FloatingButtonArrangementTest {

    @Test
    fun `PRIMARY_CONFIGURATIONS contains exactly 6 recommended combinations`() {
        val primary = FloatingButtonArrangement.PRIMARY_CONFIGURATIONS
        assertEquals(6, primary.size)
        assertEquals(
            listOf(
                FloatingButtonArrangement.POWER_ONLY,
                FloatingButtonArrangement.UP_DOWN,
                FloatingButtonArrangement.POWER_TOGGLE,
                FloatingButtonArrangement.POWER_UP_DOWN,
                FloatingButtonArrangement.TOGGLE_ONLY,
                FloatingButtonArrangement.POWER_TOGGLE_UP_DOWN
            ),
            primary
        )
    }

    @Test
    fun `POWER_ONLY contains only Power button and has expected capabilities`() {
        val arrangement = FloatingButtonArrangement.POWER_ONLY
        assertEquals(listOf(FloatingButtonType.POWER), arrangement.getButtons())
        assertTrue(arrangement.hasPower)
        assertFalse(arrangement.hasVolumeUp)
        assertFalse(arrangement.hasVolumeDown)
        assertFalse(arrangement.hasToggle)
        assertFalse(arrangement.isVolumeOnly)
    }

    @Test
    fun `UP_DOWN contains Volume Up and Down, has isVolumeOnly set to true`() {
        val arrangement = FloatingButtonArrangement.UP_DOWN
        assertEquals(
            listOf(FloatingButtonType.VOLUME_UP, FloatingButtonType.VOLUME_DOWN),
            arrangement.getButtons()
        )
        assertFalse(arrangement.hasPower)
        assertTrue(arrangement.hasVolumeUp)
        assertTrue(arrangement.hasVolumeDown)
        assertFalse(arrangement.hasToggle)
        assertTrue(arrangement.isVolumeOnly)
    }

    @Test
    fun `POWER_TOGGLE contains Power and Toggle Control`() {
        val arrangement = FloatingButtonArrangement.POWER_TOGGLE
        assertEquals(
            listOf(FloatingButtonType.POWER, FloatingButtonType.TOGGLE_CONTROL),
            arrangement.getButtons()
        )
        assertTrue(arrangement.hasPower)
        assertTrue(arrangement.hasToggle)
        assertFalse(arrangement.hasVolumeUp)
        assertFalse(arrangement.hasVolumeDown)
        assertFalse(arrangement.isVolumeOnly)
    }

    @Test
    fun `POWER_UP_DOWN contains Power and Volume Up and Down`() {
        val arrangement = FloatingButtonArrangement.POWER_UP_DOWN
        assertEquals(
            listOf(FloatingButtonType.POWER, FloatingButtonType.VOLUME_UP, FloatingButtonType.VOLUME_DOWN),
            arrangement.getButtons()
        )
        assertTrue(arrangement.hasPower)
        assertTrue(arrangement.hasVolumeUp)
        assertTrue(arrangement.hasVolumeDown)
        assertFalse(arrangement.hasToggle)
        assertFalse(arrangement.isVolumeOnly)
    }

    @Test
    fun `TOGGLE_ONLY contains only Toggle Control`() {
        val arrangement = FloatingButtonArrangement.TOGGLE_ONLY
        assertEquals(
            listOf(FloatingButtonType.TOGGLE_CONTROL),
            arrangement.getButtons()
        )
        assertFalse(arrangement.hasPower)
        assertTrue(arrangement.hasToggle)
        assertFalse(arrangement.hasVolumeUp)
        assertFalse(arrangement.hasVolumeDown)
        assertFalse(arrangement.isVolumeOnly)
    }

    @Test
    fun `POWER_TOGGLE_UP_DOWN contains all 4 functional buttons`() {
        val arrangement = FloatingButtonArrangement.POWER_TOGGLE_UP_DOWN
        assertEquals(
            listOf(
                FloatingButtonType.POWER,
                FloatingButtonType.TOGGLE_CONTROL,
                FloatingButtonType.VOLUME_UP,
                FloatingButtonType.VOLUME_DOWN
            ),
            arrangement.getButtons()
        )
        assertTrue(arrangement.hasPower)
        assertTrue(arrangement.hasToggle)
        assertTrue(arrangement.hasVolumeUp)
        assertTrue(arrangement.hasVolumeDown)
        assertFalse(arrangement.isVolumeOnly)
    }

    @Test
    fun `legacy arrangements map to valid non-empty button lists without crashing`() {
        val legacyArrangements = listOf(
            FloatingButtonArrangement.POWER_UP_DOWN_TOGGLE,
            FloatingButtonArrangement.UP_DOWN_POWER,
            FloatingButtonArrangement.UP_POWER_DOWN,
            FloatingButtonArrangement.POWER_UP,
            FloatingButtonArrangement.POWER_DOWN,
            FloatingButtonArrangement.UP_DOWN_TOGGLE,
            FloatingButtonArrangement.TOGGLE_UP_DOWN
        )

        for (legacy in legacyArrangements) {
            val buttons = legacy.getButtons()
            assertTrue("Buttons for $legacy should not be empty", buttons.isNotEmpty())
        }
    }
}
