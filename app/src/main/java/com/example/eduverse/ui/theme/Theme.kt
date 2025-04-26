package com.example.eduverse.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Defines the light color scheme for the application.
 *
 * This color scheme specifies the colors used for various UI elements in the application when in light mode.
 *
 * - **primary:**  The primary color of the theme, used for prominent UI elements like buttons. Here, it's set to `BlueButton`.
 * - **secondary:** The secondary color of the theme, used for less prominent UI elements like cards. Here, it's set to `BlueCard`.
 * - **background:** The background color for the screen or a primary container. Here, it's set to `WhiteBase`.
 * - **surface:** The color used for surfaces, such as cards and dialogs. Here, it's set to `WhiteBase`.
 * - **onPrimary:** The color used for text and icons that appear *on top* of `primary` colored elements. Here, it's set to `Color.White` for contrast.
 * - **onSecondary:** The color used for text and icons that appear *on top* of `secondary` colored elements. Here, it's set to `Color.Black` for contrast.
 * - **onBackground:** The color used for text and icons that appear *on top* of the `background` color. Here, it's set to `Color.Black` for contrast.
 * - **onSurface:** The color used for text and icons that appear *on top* of the `surface` color. Here, it's set to `Color.Black` for contrast.
 */
private val LightColorScheme = lightColorScheme(
    primary   = BlueButton,
    secondary = BlueCard,
    background = WhiteBase,
    surface   = WhiteBase,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

/**
 * Defines the dark color scheme for the application.
 *
 * This color scheme provides a dark mode appearance for UI elements. It specifies
 * the colors used for various components in dark mode, such as buttons, cards,
 * backgrounds, and text on these elements.
 *
 * - **primary**: The primary color used for key components, set to `BlueButton`.
 * - **secondary**: The secondary color, often used for accents, set to `BlueCard`.
 * - **background**: The color of the background, a dark gray `Color(0xFF121212)`.
 * - **surface**: The color of surfaces, such as cards and dialogs, also a dark gray `Color(0xFF121212)`.
 * - **onPrimary**: The color used for text and icons on top of the `primary` color, set to `Color.White`.
 * - **onSecondary**: The color used for text and icons on top of the `secondary` color, set to `Color.White`.
 * - **onBackground**: The color used for text and icons on top of the `background` color, set to `Color.White`.
 * - **onSurface**: The color used for text and icons on top of the `surface` color, set to `Color.White`.
 *
 * This color scheme ensures that text and icons remain readable against the dark
 * backgrounds and surfaces.
 */
private val DarkColorScheme = darkColorScheme(
    primary   = BlueButton,
    secondary = BlueCard,
    background = Color(0xFF121212),
    surface   = Color(0xFF121212),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)


/**
 * EduVerseTheme is a custom Material Theme for the EduVerse application.
 *
 * It provides a consistent look and feel throughout the application by defining
 * color schemes, typography, and enabling dynamic color support.
 *
 * @param fontScale The scaling factor to apply to all font sizes. A value of 1f
 * represents the default font size. Values greater than 1f will increase the
 * font size, while values less than 1f will decrease it. Defaults to 1f.
 * @param dynamicColor Whether to use dynamic colors based on the user's wallpaper.
 * If true, the theme will adapt its colors to the wallpaper's dominant colors on
 * devices that support it. Defaults to true.
 * @param darkTheme Whether to use the dark color scheme. If true, the dark color
 * scheme will be applied. If false, the light color scheme will be used. If
 * dynamicColor is enabled, this parameter determines which dynamic color scheme
 * to use. Defaults to the system's dark theme setting.
 * @param primaryColor The primary color to use in the theme. This overrides the
 * default primary color of the selected color scheme (either dark or light).
 * Defaults to the primary color of the selected color scheme.
 */
@Composable
fun EduVerseTheme(
    fontScale: Float = 1f,
    dynamicColor: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryColor: Color = if (darkTheme) DarkColorScheme.primary else LightColorScheme.primary,
    secondaryColor: Color = if (darkTheme) DarkColorScheme.secondary else LightColorScheme.secondary,
    content: @Composable () -> Unit
) {
    when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    val base = if (darkTheme) DarkColorScheme else LightColorScheme
    val colors = base.copy(
        primary   = primaryColor,
        secondary = secondaryColor
    )

    val scaledTypography = Typography(
        displayLarge  = Typography.displayLarge.copy(fontSize = Typography.displayLarge.fontSize * fontScale),
        displayMedium = Typography.displayMedium.copy(fontSize = Typography.displayMedium.fontSize * fontScale),

        headlineLarge = Typography.headlineLarge.copy(fontSize = Typography.headlineLarge.fontSize * fontScale),
        headlineMedium= Typography.headlineMedium.copy(fontSize = Typography.headlineMedium.fontSize * fontScale),
        headlineSmall = Typography.headlineSmall.copy(fontSize = Typography.headlineSmall.fontSize * fontScale),

        titleLarge    = Typography.titleLarge.copy(fontSize = Typography.titleLarge.fontSize * fontScale),
        titleMedium   = Typography.titleMedium.copy(fontSize = Typography.titleMedium.fontSize * fontScale),
        titleSmall    = Typography.titleSmall.copy(fontSize = Typography.titleSmall.fontSize * fontScale),

        bodyLarge     = Typography.bodyLarge.copy(fontSize = Typography.bodyLarge.fontSize * fontScale),
        bodyMedium    = Typography.bodyMedium.copy(fontSize = Typography.bodyMedium.fontSize * fontScale),
        bodySmall     = Typography.bodySmall.copy(fontSize = Typography.bodySmall.fontSize * fontScale),

        labelLarge    = Typography.labelLarge.copy(fontSize = Typography.labelLarge.fontSize * fontScale),
        labelMedium   = Typography.labelMedium.copy(fontSize = Typography.labelMedium.fontSize * fontScale),
        labelSmall    = Typography.labelSmall.copy(fontSize = Typography.labelSmall.fontSize * fontScale),
    )

    MaterialTheme(
        colorScheme = colors,
        typography  = scaledTypography,
        content     = content
    )
}
