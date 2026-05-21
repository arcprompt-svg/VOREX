package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
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
    assertEquals("VoreX", appName)
  }

  @Test
  fun `verify shared preferences saves and retrieves operator name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val sharedPrefs = context.getSharedPreferences("vorex_prefs", Context.MODE_PRIVATE)
    
    // Save name
    sharedPrefs.edit().putString("operator_name", "Commander Alice").commit()
    
    // Retrieve name
    val retrievedName = sharedPrefs.getString("operator_name", "Default")
    assertEquals("Commander Alice", retrievedName)
  }
}
