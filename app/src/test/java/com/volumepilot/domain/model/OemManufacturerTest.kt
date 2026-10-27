package com.volumepilot.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class OemManufacturerTest {

    @Test
    fun `detect correctly classifies popular OEM brands`() {
        assertEquals(OemManufacturer.SAMSUNG, OemManufacturer.detect("samsung"))
        assertEquals(OemManufacturer.XIAOMI, OemManufacturer.detect("Xiaomi"))
        assertEquals(OemManufacturer.XIAOMI, OemManufacturer.detect("Redmi"))
        assertEquals(OemManufacturer.XIAOMI, OemManufacturer.detect("POCO"))
        assertEquals(OemManufacturer.ONEPLUS_OPPO_REALME, OemManufacturer.detect("OnePlus"))
        assertEquals(OemManufacturer.ONEPLUS_OPPO_REALME, OemManufacturer.detect("oppo"))
        assertEquals(OemManufacturer.ONEPLUS_OPPO_REALME, OemManufacturer.detect("Realme"))
        assertEquals(OemManufacturer.VIVO, OemManufacturer.detect("vivo"))
        assertEquals(OemManufacturer.VIVO, OemManufacturer.detect("iQOO"))
        assertEquals(OemManufacturer.HUAWEI, OemManufacturer.detect("HUAWEI"))
        assertEquals(OemManufacturer.HUAWEI, OemManufacturer.detect("HONOR"))
        assertEquals(OemManufacturer.GOOGLE, OemManufacturer.detect("Google"))
        assertEquals(OemManufacturer.GENERIC, OemManufacturer.detect("Sony"))
        assertEquals(OemManufacturer.GENERIC, OemManufacturer.detect("Motorola"))
    }
}
