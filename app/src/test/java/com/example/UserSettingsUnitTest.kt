package com.example

import com.example.data.preferences.CachedCyberAnalysis
import com.example.data.preferences.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class UserSettingsUnitTest {

    @Test
    fun testDefaultOfflineModeIsDisabled() {
        val settings = UserSettings()
        assertFalse(settings.isOfflineMode)
        assertNull(settings.cachedAnalysis)
    }

    @Test
    fun testCachedCyberAnalysisDataModel() {
        val analysis = CachedCyberAnalysis(
            title = "Security_Audit_Whitepaper.pdf",
            summary = "Enterprise network reconnaissance audit",
            content = "Detailed findings on open port quarantine",
            timestamp = 1730000000000L,
            severity = "CRITICAL",
            category = "Defensive Security Audit"
        )

        val settings = UserSettings(
            isOfflineMode = true,
            cachedAnalysis = analysis
        )

        assertEquals(true, settings.isOfflineMode)
        assertNotNull(settings.cachedAnalysis)
        assertEquals("Security_Audit_Whitepaper.pdf", settings.cachedAnalysis?.title)
        assertEquals("CRITICAL", settings.cachedAnalysis?.severity)
    }
}
