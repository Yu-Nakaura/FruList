package app.nakaura.chloe.original

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import app.nakaura.chloe.original.databinding.FragmentToDoBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ToDoFragment : Fragment() {
    private var _binding: FragmentToDoBinding? = null
    private val binding get() = _binding!!
    val db = Firebase.firestore
    private lateinit var sharedPref: SharedPreferences
    var group:String = "nothing"

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
        getGroup()
        binding.personButton.setOnClickListener {
            changeToPersonalView()
        }
        binding.barChartButton.setOnClickListener {
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

    private fun changeToPersonalView() {
        binding.colorBarPerson.isVisible = true
        binding.colorBarChart.isVisible = false
        binding.addButton.isVisible = true
        binding.addButtonBackground.isVisible = true
    }

    private fun changeToChartView() {
        binding.colorBarPerson.isVisible = false
        binding.colorBarChart.isVisible = true
        binding.addButton.isVisible = false
        binding.addButtonBackground.isVisible = false
    }

    private fun getGroup() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val registeredName: String? = sharedPref.getString("userFileName", "")
        Log.d("RegisteredName", registeredName.toString())

        db.collection("users")
            .document("$registeredName")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userDocument = task.result
                    if (userDocument != null && userDocument.data != null) {
                        /*Log.d(TAG, "getData")
                        Log.d(TAG, "DocumentSnapshot data: " + userDocument.data?.get("userName"))
                        Log.d(TAG, "DocumentSnapshot data: " + userDocument.data?.get("password"))
                        Log.d(TAG, "DocumentSnapshot data: " + userDocument.data?.get("group"))*/
                        group = userDocument.data?.get("group").toString()
                        Log.d("getGroup", group)
                    } else {
                        Log.d(TAG, "No such document")
                    }
                } else {
                    Log.d(TAG, "get failed with " + task.exception)
                }
            }
            .addOnFailureListener { e -> Log.d(TAG, "Error adding document$e") }
    }
}
