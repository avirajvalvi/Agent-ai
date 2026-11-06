package com.example

import com.example.data.models.BuildPipelineStage
import com.example.ui.viewmodels.AppBuilderViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildPipelineUnitTest {

    @Test
    fun testInitialPipelineJobHasValidArtifactAndDownloadUrl() {
        val viewModel = AppBuilderViewModel()
        val job = viewModel.currentJob.value

        assertNotNull(job)
        assertEquals(BuildPipelineStage.SUCCESS, job.stage)
        assertNotNull(job.artifact)

        val artifact = job.artifact!!
        assertEquals("SecMentor AI", artifact.appName)
        assertTrue(artifact.directDownloadUrl.startsWith("https://builds.secmentor.io/artifacts/"))
        assertTrue(artifact.fileName.endsWith(".apk"))
        assertEquals(31.2, artifact.fileSizeMb, 0.1)
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", artifact.sha256Checksum)
    }

    @Test
    fun testTriggerAutomatedBuildStartsPipeline() {
        val viewModel = AppBuilderViewModel()
        viewModel.triggerAutomatedBuild("SecMentor AI", "Debug Universal APK")

        val job = viewModel.currentJob.value
        assertNotNull(job)
        assertTrue(job.isRunning)
        assertTrue(job.logs.isNotEmpty())
    }
}
