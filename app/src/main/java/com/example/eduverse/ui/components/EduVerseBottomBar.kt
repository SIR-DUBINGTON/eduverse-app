package com.example.eduverse.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.eduverse.R

data class BottomNavItem(
    val label: String,
    val iconRes: Int,
    val route: String
)

@Composable
fun EduVerseBottomBar(currentRoute: String?, onTabSelected: (String) -> Unit) {
    val items = listOf(
        BottomNavItem("Home", R.drawable.ic_home, "home"),
        BottomNavItem("Solver", R.drawable.ic_calculator, "equation_solver"),
        BottomNavItem("Classroom", R.drawable.ic_classroom, "nav_classroom"),
        BottomNavItem("Problems", R.drawable.ic_problem, "problems")
    )

    NavigationBar {
        items.forEach { item ->
            val isSelected = when (item.route) {
                "nav_classroom" -> currentRoute?.startsWith("classroom") == true || currentRoute == "nav_classroom"
                "problems" -> currentRoute == "problems"
                else -> currentRoute == item.route
            }


            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
