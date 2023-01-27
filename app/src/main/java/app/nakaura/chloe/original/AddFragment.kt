package app.nakaura.chloe.original

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import app.nakaura.chloe.original.databinding.FragmentAddBinding
import app.nakaura.chloe.original.todo.ToDoFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private var registeredName: String = ""
    private var registeredGroup: String = ""
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
        registeredGroup = arguments?.getString("group").toString()
        changeView()
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        registeredName = sharedPref.getString("userFileName", "").toString()

        val pointItems = resources.getStringArray(R.array.point_list)
        val adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.dropdown_popup_item,
            pointItems
        )
        binding.pointDropbotton.setAdapter(adapter)

        binding.doneButton.setOnClickListener {
            saveToDoData()
        }
        binding.backButton.setOnClickListener {
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
        val toDoMap = hashMapOf(
            "userName" to registeredName,
            "title" to title,
            "point" to point,
            "note" to note
        )
        Log.d("toDoMap", toDoMap.toString())

        if (title.isNotEmpty() && point.isNotEmpty()) {
            binding.errorText.isVisible = false
            db.collection("users").document("$registeredName")
                .collection("ToDo").document("$title")
                .set(toDoMap)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    Log.d(ContentValues.TAG, "Error adding document", e)
                }
            toToDoFragment()
        } else {
            binding.errorText.isVisible = true
            Log.d("key", "何も入力されていません")
        }
    }

    @SuppressLint("ResourceAsColor", "ResourceType")
    private fun changeView() {
        when (registeredGroup) {
            "apple" -> {
                Log.d("change", "changeToApple")
                binding.addTitleText.setBackgroundResource(R.color.dark_red)
                binding.addTitleBackground.setBackgroundResource(R.color.light_red)
            }
            "lemon" -> {
                Log.d("change", "changeToLemon")
                binding.addTitleText.setBackgroundResource(R.color.dark_yellow)
                binding.addTitleBackground.setBackgroundResource(R.color.light_yellow)
            }
            "pear" -> {
                Log.d("change", "changeToPear")
                binding.addTitleText.setBackgroundResource(R.color.dark_green)
                binding.addTitleBackground.setBackgroundResource(R.color.light_green)
            }
            "grape" -> {
                Log.d("change", "changeToGrape")
                binding.addTitleText.setBackgroundResource(R.color.dark_purple)
                binding.addTitleBackground.setBackgroundResource(R.color.light_purple)
            }
        }
    }

}