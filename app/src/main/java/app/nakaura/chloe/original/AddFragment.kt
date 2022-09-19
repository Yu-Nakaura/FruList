package app.nakaura.chloe.original

import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import app.nakaura.chloe.original.databinding.FragmentAddBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pointItems = resources.getStringArray(R.array.point_list)
        val adapter = activity?.let {
            ArrayAdapter<String>(
                it,
                R.layout.dropdown_popup_item,
                pointItems
            )
        }
        binding.pointDropbotton.setAdapter(adapter)


        binding.doneButton.setOnClickListener {
            saveToDoData()
            toToDoFragment()
        }
    }

    private fun toToDoFragment() {
        val todoFragment = ToDoFragment()
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.replace(R.id.fragmentContainer, todoFragment)
        fragmentTransaction?.commit()
    }

    private fun saveToDoData() {
        val title: String = binding.addToDoText.text.toString()
        var point: String = binding.pointDropbotton.text.toString()
        val note: String = binding.addNoteText.text.toString()
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val registeredName: String? = sharedPref.getString("userFileName", "")
        Log.d("RegisteredName", registeredName.toString())

        val toDoMap = hashMapOf(
            "userName" to registeredName,
            "title" to title,
            "point" to point,
            "note" to note
        )
        Log.d("toDoMap", toDoMap.toString())

        db.collection("toDo").document("$registeredName")
            .set(toDoMap)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.d(ContentValues.TAG, "Error adding document", e)
            }
    }
}