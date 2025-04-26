package com.example.eduverse.ui.screens.settings

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eduverse.R
import androidx.core.content.edit

/**
 * Composable function that displays the settings screen.
 *
 * This screen allows the user to adjust the following settings:
 * - Font Size: A slider to control the font size of the sample text.
 * - Dark Mode: A switch to toggle dark mode on or off.
 *
 * Settings are saved to SharedPreferences and applied by recreating the activity.
 *
 * @param "modifier" Modifier to apply to the root Column.
 */
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("eduverse_prefs", Context.MODE_PRIVATE)

    var fontSizeSp by remember { mutableFloatStateOf(prefs.getFloat("pref_font_scale", 1f) * 16f) }
    var darkModeOn by remember { mutableStateOf(prefs.getBoolean("pref_dark_mode", false)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_heading_text),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
        )

        Text(text = stringResource(R.string.font_size_text), fontSize = 18.sp)
        Slider(
            value = fontSizeSp,
            onValueChange = { fontSizeSp = it },
            valueRange = 12f..24f,
            steps = 12
        )
        Text(text = stringResource(R.string.sample_text), fontSize = fontSizeSp.sp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = stringResource(R.string.dark_mode_text), fontSize = 18.sp)
            Switch(
                checked = darkModeOn,
                onCheckedChange = { enabled ->
                    darkModeOn = enabled
                    prefs.edit { putBoolean("pref_dark_mode", enabled) }
                    (context as ComponentActivity).recreate()
                }
            )
        }

        Button(onClick = {
            prefs.edit {
                putFloat("pref_font_scale", fontSizeSp / 16f)
            }
            (context as ComponentActivity).recreate()
        }) {
            Text(stringResource(R.string.save_settings_text))
        }
    }
}
