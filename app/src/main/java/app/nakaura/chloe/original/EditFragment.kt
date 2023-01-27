package app.nakaura.chloe.original

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import app.nakaura.chloe.original.databinding.FragmentAddBinding
import app.nakaura.chloe.original.databinding.FragmentEditBinding
import app.nakaura.chloe.original.todo.ToDoFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private var title: String = ""
    private var point: String = ""
    private var note: String = ""
    private var registeredName: String = ""
    private var registeredGroup: String = ""
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        registeredGroup = arguments?.getString("group").toString()
        changeView()
        registeredName = sharedPref.getString("userFileName", "").toString()
        Log.d("RegisteredName", registeredName)

        title = arguments?.getString("title").toString()
        point= arguments?.getString("point").toString()
        note = arguments?.getString("note").toString()
        binding.editToDoText.setText(title, TextView.BufferType.EDITABLE);
        binding.editNoteText.setText(note, TextView.BufferType.EDITABLE);

        val pointItems = resources.getStringArray(R.array.point_list)
        val adapter = ArrayAdapter<String>(
            requireActivity(),
            R.layout.dropdown_popup_item,
            pointItems
        )
        binding.pointDropbotton.setAdapter(adapter)
        binding.pointDropbotton.setText(point, false)

        binding.doneButton.setOnClickListener {
            saveToDoData()
        }
        binding.backButton.setOnClickListener {
            toToDoFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveToDoData() {
        val editedTitle: String = binding.editToDoText.text.toString()
        var editedPoint: String = binding.pointDropbotton.text.toString()
        val editedNote: String = binding.editNoteText.text.toString()
        val toDoMap = hashMapOf(
            "userName" to registeredName,
            "title" to editedTitle,
            "point" to editedPoint,
            "note" to editedNote
        )

        if (title.isNotEmpty() && point.isNotEmpty()) {
            binding.errorText.isVisible = false
            db.collection("users").document(registeredName)
                .collection("ToDo").document(editedTitle)
                .set(toDoMap)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    Log.d(ContentValues.TAG, "Error adding document", e)
                }
            if(title != editedTitle){
                db.collection("users").document(registeredName)
                    .collection("ToDo").document(title)
                    .delete()
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.d(ContentValues.TAG, "Error deleting document", e) }
            }
            toToDoFragment()
        } else {
            binding.errorText.isVisible = true
            Log.d("key", "何も入力されていません")
        }
    }

    private fun toToDoFragment() {
        val todoFragment = ToDoFragment()
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.replace(R.id.fragmentContainer, todoFragment)
        fragmentTransaction?.commit()
    }

    @SuppressLint("ResourceAsColor", "ResourceType")
    private fun changeView() {
        when (registeredGroup) {
            "apple" -> {
                Log.d("change", "changeToApple")
                binding.editTitleText.setBackgroundResource(R.color.dark_red)
                binding.editTitleBackground.setBackgroundResource(R.color.light_red)
            }
            "lemon" -> {
                Log.d("change", "changeToLemon")
                binding.editTitleText.setBackgroundResource(R.color.dark_yellow)
                binding.editTitleBackground.setBackgroundResource(R.color.light_yellow)
            }
            "pear" -> {
                Log.d("change", "changeToPear")
                binding.editTitleText.setBackgroundResource(R.color.dark_green)
                binding.editTitleBackground.setBackgroundResource(R.color.light_green)
            }
            "grape" -> {
                Log.d("change", "changeToGrape")
                binding.editTitleText.setBackgroundResource(R.color.dark_purple)
                binding.editTitleBackground.setBackgroundResource(R.color.light_purple)
            }
        }
    }
}