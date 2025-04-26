package com.example.eduverse.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.eduverse.R

/**
 * Represents a single item in a bottom navigation bar.
 *
 * This data class encapsulates the necessary information to display and navigate to a specific
 * screen within a bottom navigation bar. It includes the label to display, the icon to use,
 * and the route that corresponds to the screen it represents.
 *
 * @property label The text label to display for this navigation item.
 * @property iconRes The resource ID of the icon to display for this navigation item. This should
 *                    be a drawable resource.
 * @property route The navigation route that this item corresponds to. This route will be used
 *                 to navigate to the appropriate screen when the item is selected.
 */
data class BottomNavItem(
    val label: String,
    val iconRes: Int,
    val route: String
)

/**
 * EduVerseBottomBar is a Composable function that renders the bottom navigation bar for the EduVerse application.
 *
 * It displays a set of navigation items, each represented by an icon and a label, allowing the user to switch between different sections of the app.
 *
 * @param currentRoute The currently active route in the navigation hierarchy. This is used to highlight the currently selected tab.
 *                     It can be null if no route is active yet.
 * @param onTabSelected A callback function that is invoked when a tab is selected.
 *                      It receives the route associated with the selected tab as a String parameter.
 *                      This function should handle navigating to the corresponding screen.
 *
 * The function defines the following tabs:
 *  - "equation_solver":  Equation Solver screen
 *  - "nav_classroom":   Classroom screen (also active for routes starting with "classroom" like "classroom/123")
 *  - "home":            Home screen
 *  - "problems":        Problems screen
 *  - "settings":        Settings screen
 *
 * The selection logic is such that:
 *  - If the `currentRoute` exactly matches an item's `route`, that item is selected.
 *  - If the item's `route` is "nav_classroom" and the `currentRoute` starts with "classroom", that item is selected (for navigating within classroom sections)
 *
 * The visual representation of each tab includes an icon (specified by `iconRes`) and a label (specified by `label`).
 *
 * Example Usage:
 * ```
 * EduVerseBottomBar(
 *     currentRoute = myCurrentRoute,
 *     onTabSelected = { route ->
 *         navController.navigate(route)
 *     }
 * )
 * ```
 */
@Composable
fun EduVerseBottomBar(currentRoute: String?, onTabSelected: (String) -> Unit) {
    val items = listOf(
        BottomNavItem(stringResource(R.string.equation_solver_text), R.drawable.ic_calculator, "equation_solver"),
        BottomNavItem(stringResource(R.string.nav_classroom_text),   R.drawable.ic_classroom,  "nav_classroom"),
        BottomNavItem(stringResource(R.string.home_text),             R.drawable.ic_home,       "home"),
        BottomNavItem(stringResource(R.string.problems_text),        R.drawable.ic_problem,    "problems"),
        BottomNavItem(stringResource(R.string.settings_text),        R.drawable.ic_settings,   "settings")
    )

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentRoute == item.route ||
                    (item.route == "nav_classroom" && currentRoute?.startsWith("classroom") == true)

            NavigationBarItem(
                selected = isSelected,
                onClick  = { onTabSelected(item.route) },
                icon     = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label
                    )
                },
                label    = {
                    Text(
                        text  = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}