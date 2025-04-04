package com.example.eduverse.ui.fragments.classroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.eduverse.R

class ClassroomFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_classroom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val theoryTitle = arguments?.getString("formulaTitle") ?: "Formula"
        val theoryContent = arguments?.getString("formulaTheory") ?: "No theory available for this formula"

        view.findViewById<TextView>(R.id.theoryTitle).text = theoryTitle
        view.findViewById<TextView>(R.id.theoryBody).text = theoryContent

    }
}
