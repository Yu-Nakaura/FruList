package app.nakaura.chloe.original

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import app.nakaura.chloe.original.databinding.FragmentToDoBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ToDoFragment : Fragment() {
    private var _binding: FragmentToDoBinding? = null
    private val binding get() = _binding!!
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.personButton.setOnClickListener {
            changeToPersonalView()
        }
        binding.barChartButton.setOnClickListener{
            changeToChartView()
        }
        binding.addButton.setOnClickListener {
            val addFragment = AddFragment()
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.replace(R.id.fragmentContainer, addFragment)
            fragmentTransaction?.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeToPersonalView(){
        binding.colorBarPerson.isVisible = true
        binding.colorBarChart.isVisible = false
        binding.addButton.isVisible = true
        binding.addButtonBackground.isVisible = true
    }

    private fun changeToChartView(){
        binding.colorBarPerson.isVisible = false
        binding.colorBarChart.isVisible = true
        binding.addButton.isVisible = false
        binding.addButtonBackground.isVisible = false
    }

}