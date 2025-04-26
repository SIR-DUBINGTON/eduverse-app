package com.example.eduverse.engine

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * `EquationEngine` is a utility object that provides functionality to solve physics equations.
 * It supports a predefined set of equations and can solve for different variables within those equations.
 */
object EquationEngine {

    fun solve(equation: String, unknown: String, values: Map<String, Double>): Double {
        return when (equation) {
            "F = m * a" -> when (unknown) {
                "F" -> values.getValue("m") * values.getValue("a")
                "m" -> values.getValue("F") / values.getValue("a")
                "a" -> values.getValue("F") / values.getValue("m")
                else -> throw IllegalArgumentException("Unknown variable $unknown")
            }

            "KE = 0.5 * m * v^2" -> when (unknown) {
                "KE" -> 0.5 * values.getValue("m") * values.getValue("v").pow(2.0)
                "m"  -> (2 * values.getValue("KE")) / values.getValue("v").pow(2.0)
                "v"  -> sqrt((2 * values.getValue("KE")) / values.getValue("m"))
                else -> throw IllegalArgumentException("Unknown variable $unknown")
            }

            "V = I * R" -> when (unknown) { // Ohm's Law
                "V" -> values.getValue("I") * values.getValue("R")
                "I" -> values.getValue("V") / values.getValue("R")
                "R" -> values.getValue("V") / values.getValue("I")
                else -> throw IllegalArgumentException("Unknown variable $unknown")
            }

            "F = G * (m1 * m2) / (r^2)" -> when (unknown) { // Gravitational Force
                "F" -> 6.67430e-11 * (values.getValue("m1") * values.getValue("m2")) / values.getValue(
                    "r"
                ).pow(2.0)
                "m1" -> values.getValue("F") * values.getValue("r").pow(2.0) / (6.67430e-11 * values.getValue("m2"))
                "m2" -> values.getValue("F") * values.getValue("r").pow(2.0) / (6.67430e-11 * values.getValue("m1"))
                "r" -> sqrt((6.67430e-11 * values.getValue("m1") * values.getValue("m2")) / values.getValue("F"))
                else -> throw IllegalArgumentException("Unknown variable $unknown")
            }

            else -> throw IllegalArgumentException("Unsupported formula $equation")
        }
    }
}
