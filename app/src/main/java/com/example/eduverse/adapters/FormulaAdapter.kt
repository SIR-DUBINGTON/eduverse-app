package com.example.eduverse.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eduverse.R
import com.example.eduverse.models.Formula
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class FormulaAdapter(
    private val formulas: List<Formula>,
    private val onClick: (Formula) -> Unit
) : RecyclerView.Adapter<FormulaAdapter.FormulaViewHolder>() {

    inner class FormulaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val formulaTextView: TextView = view.findViewById(R.id.formulaTextView)
        val formulaName: TextView = view.findViewById(R.id.formulaName)
        val theoryButton: Button = view.findViewById(R.id.theoryButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormulaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_formula, parent, false)
        return FormulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormulaViewHolder, position: Int) {
        val formula = formulas[position]

        // Display the formula string and name on the card
        holder.formulaTextView.text = formula.equation
        holder.formulaName.text = formula.name

        // Click listeners
        holder.itemView.setOnClickListener { onClick(formula) }
        holder.theoryButton.setOnClickListener { view ->
            val bundle = Bundle().apply {
                putString("formulaTitle", formula.name)
                putString("formulaTheory", formula.theory)
            }

            val navController = view.findNavController()
            navController.navigate(R.id.nav_classroom, bundle)

            // Update selection WITHOUT triggering navigation again
            val activity = view.context as? AppCompatActivity
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav?.menu?.findItem(R.id.nav_classroom)?.isChecked = true
        }

    }


    override fun getItemCount(): Int = formulas.size
}
