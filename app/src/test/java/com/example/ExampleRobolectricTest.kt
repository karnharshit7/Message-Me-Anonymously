package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.moderation.ModerationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Message Me Anonymously", appName)
  }

  @Test
  fun `test moderation engine blocks prohibited threats`() {
    val result = ModerationEngine.evaluateContent("I will find you and kill yourself")
    assertFalse(result.isAllowed)
    assertEquals("BLOCKED", result.status)
  }

  @Test
  fun `test moderation engine allows clean message`() {
    val result = ModerationEngine.evaluateContent("You are an amazing and kind person!")
    assertTrue(result.isAllowed)
    assertEquals("APPROVED", result.status)
  }
}

