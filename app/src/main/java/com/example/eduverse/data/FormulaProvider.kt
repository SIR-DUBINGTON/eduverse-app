package com.example.eduverse.data

import com.example.eduverse.models.Formula

/**
 * `FormulaProvider` is a singleton object that provides a collection of physics formulas.
 *
 * It acts as a data source for a predefined set of formulas, each represented by a `Formula` object.
 * Each formula includes its name, equation, a list of variables, a brief theoretical description,
 * and its LaTeX representation for rendering.
 *
 * This object is designed to be used as a central repository for frequently used physics formulas
 * within an application or system. It offers a structured and easily accessible way to manage
 * and retrieve these formulas.
 */

object FormulaProvider {
    val formulas = listOf(
        Formula(
            name = "Newton's Second Law",
            equation = "F = m * a",
            variables = listOf("F", "m", "a"),
            theory = "Newton's Second Law states that force equals mass times acceleration. It describes the relationship between an object's mass, the acceleration it experiences, and the force acting upon it.",
            latex = """F = m \cdot a"""
        ),
        Formula(
            name = "Kinetic Energy",
            equation = "KE = 0.5 * m * v^2",
            variables = listOf("KE", "m", "v"),
            theory = "Kinetic Energy is the energy of motion. It is calculated as one-half of the object's mass times the square of its velocity.",
            latex = """KE = \frac{1}{2} \cdot m \cdot v^2"""
        ),
        Formula(
            name = "Ohm's Law",
            equation = "V = I * R",
            variables = listOf("V", "I", "R"),
            theory = "Ohm's Law defines the relationship between voltage, current, and resistance in an electrical circuit.",
            latex = """V = I \cdot R"""
        ),
        Formula(
            name = "Gravitational Force",
            equation = "F = G * (m1 * m2) / (r^2)",
            variables = listOf("F", "m1", "m2", "r"),
            theory = "The Law of Universal Gravitation states that every mass attracts every other mass with a force that is proportional to the product of their masses and inversely proportional to the square of the distance between them.",
            latex = """F = G \frac{m_1 m_2}{r^2}"""
        )
    )
}