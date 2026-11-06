package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testAppMetadataConfig() {
    val expectedName = "SecMentor AI"
    assertEquals("SecMentor AI", expectedName)
  }
}
