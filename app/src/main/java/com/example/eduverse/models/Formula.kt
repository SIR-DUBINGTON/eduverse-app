package com.example.eduverse.models

/**
 * Represents a mathematical formula with its name, equation, variables, underlying theory, and LaTeX representation.
 *
 * This data class encapsulates the essential information related to a specific formula,
 * making it easy to manage and access its properties.
 *
 * @property name The name or title of the formula (e.g., "Pythagorean Theorem", "Kinetic Energy").
 * @property equation The mathematical equation representing the formula (e.g., "a^2 + b^2 = c^2", "KE = 0.5 * m * v^2").
 * @property variables A list of the variables used in the equation (e.g., ["a", "b", "c"], ["KE", "m", "v"]).
 * @property theory A brief description of the theory or concept behind the formula (e.g., "Relates the lengths of the sides of a right triangle", "Describes the energy of an object due to its motion").
 * @property latex The LaTeX representation of the formula, suitable for rendering in mathematical notation (e.g., "a^2 + b^2 = c^2", "KE = \\frac{1}{2}mv^2").
 */

data class Formula(
    val name: String,
    val equation: String,
    val variables: List<String>,
    val theory: String,
    val latex: String
)