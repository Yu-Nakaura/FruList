package app.nakaura.chloe.original

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import app.nakaura.chloe.original.databinding.FragmentToDoBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ToDoFragment : Fragment() {
    private var _binding: FragmentToDoBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private lateinit var sharedPref: SharedPreferences
    private var group: String = "nothing"
    private var registeredName: String = ""
    private var docTitleArray: ArrayList<String> = arrayListOf()
    private var clearedTitleArray: ArrayList<String> = arrayListOf()
    private val sortedTitleArray: ArrayList<String> = arrayListOf()
    private val individualPointArray: ArrayList<Int> = arrayListOf()
    private val toDoList = ArrayList<ToDo>()
    private val toDoAdapter = ToDoAdapter()
    private var individualPointSum:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        registeredName = sharedPref.getString("userFileName", "").toString()
        getGroup()
        getToDoTitle()

        binding.personButton.setOnClickListener {
            changeToPersonalView()
        }
        binding.barChartButton.setOnClickListener {
            changeToChartView()
        }
        binding.addButton.setOnClickListener {
            toAddFragment()
        }

        //CheckBox is checked
        toDoAdapter.setOnCheckBoxClickListener(
            object : ToDoAdapter.OnCheckBoxClickListener {
                override fun onItemClick(position: Int) {
                    val arrayPosition: Int = position
                    db.collection("users")
                        .document(registeredName)
                        .collection("ToDo")
                        .document(sortedTitleArray[position])
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userDocument = task.result
                                if (userDocument != null && userDocument.data != null) {
                                    val clearedPointString:String = userDocument.data?.get("point").toString()
                                    var clearedPointNumber: Int = 0
                                    when (clearedPointString) {
                                        "1pt" -> clearedPointNumber = 1
                                        "2pt" -> clearedPointNumber = 2
                                        "3pt" -> clearedPointNumber = 3
                                        "4pt" -> clearedPointNumber = 4
                                    }
                                    Log.d("clearedPointNumber", clearedPointNumber.toString())
                                    setClearedPoint(clearedPointNumber, arrayPosition)
                                    clearCheckedToDo(arrayPosition)
                                    sortedTitleArray.clear()
                                    toDoList.clear()
                                    getToDoList()
                                    getIndividualPoint()
                                }
                            }
                        }
                }
            }
        )
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
        binding.recyclerView.isVisible = false
        getAllUsers()
    }

    private fun getGroup() {
        db.collection("users")
            .document(registeredName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userDocument = task.result
                    if (userDocument != null && userDocument.data != null) {
                        group = userDocument.data?.get("group").toString()
                        Log.d("getGroup", group)
                        changeView()
                    } else {
                        Log.d(TAG, "No such document(getGroup)")
                    }
                } else {
                    Log.d(TAG, "get failed with " + task.exception)
                }
            }
            .addOnFailureListener { e -> Log.d(TAG, "Error adding document$e") }
    }

    @SuppressLint("ResourceAsColor", "ResourceType")
    private fun changeView() {
        when (group) {
            "apple" -> {
                Log.d("change", "changeToApple")
                binding.toDoTitleText.setBackgroundResource(R.color.dark_red)
                binding.colorBarChart.setBackgroundResource(R.color.light_red)
                binding.colorBarPerson.setBackgroundResource(R.color.light_red)
            }
            "lemon" -> {
                Log.d("change", "changeToLemon")
                binding.toDoTitleText.setBackgroundResource(R.color.dark_yellow)
                binding.colorBarChart.setBackgroundResource(R.color.light_yellow)
                binding.colorBarPerson.setBackgroundResource(R.color.light_yellow)
            }
            "pear" -> {
                Log.d("change", "changeToPear")
                binding.toDoTitleText.setBackgroundResource(R.color.dark_green)
                binding.colorBarChart.setBackgroundResource(R.color.light_green)
                binding.colorBarPerson.setBackgroundResource(R.color.light_green)
            }
            "grape" -> {
                Log.d("change", "changeToGrape")
                binding.toDoTitleText.setBackgroundResource(R.color.dark_purple)
                binding.colorBarChart.setBackgroundResource(R.color.light_purple)
                binding.colorBarPerson.setBackgroundResource(R.color.light_purple)
            }
        }
    }

    private fun getToDoTitle() {
        db.collection("users")
            .document(registeredName)
            .collection("ToDo")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    docTitleArray.add(document.id)
                    Log.d("docTitleArray", docTitleArray.toString())
                }
                getToDoList()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun getToDoList() {
        for (i in 0 until docTitleArray.size) {
            db.collection("users")
                .document(registeredName)
                .collection("ToDo")
                .document(docTitleArray[i])
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sortedTitleArray.add(docTitleArray[i])
                        Log.d("sortedTitleArray", sortedTitleArray.toString())
                        val userDocument = task.result
                        if (userDocument != null && userDocument.data != null) {
                            //set Adapter
                            binding.recyclerView.adapter = toDoAdapter
                            binding.recyclerView.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                            //set data from Firestore to ToDo
                            val userList = ToDo(
                                userDocument.data?.get("title").toString(),
                                userDocument.data?.get("point").toString(),
                                userDocument.data?.get("note").toString()
                            )
                            toDoList.add(userList)
                            Log.d("toDoList", toDoList.toString())
                            toDoAdapter.submitList(toDoList)
                        } else {
                            Log.d(TAG, "No such document(getToDoList)")
                        }
                    } else {
                        Log.d(TAG, "get failed with " + task.exception)
                    }
                }
                .addOnFailureListener { e -> Log.d(TAG, "Error adding document$e") }
        }
    }

    private fun toAddFragment() {
        val bundle = Bundle()
        bundle.putString("group", group)
        val addFragment = AddFragment()
        addFragment.arguments = bundle
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.replace(R.id.fragmentContainer, addFragment)
        fragmentTransaction?.commit()
    }

    private fun setClearedPoint(clearedPointNumber: Int, arrayPosition: Int){
        val clearedPointMap = hashMapOf(
            "point" to clearedPointNumber
        )
        Log.d("clearedPointMap", sortedTitleArray[arrayPosition]+"," +clearedPointMap.toString())

        db.collection("users").document(registeredName)
            .collection("ClearedToDo").document(sortedTitleArray[arrayPosition])
            .set(clearedPointMap)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error adding document", e)
            }
    }

    private fun clearCheckedToDo(arrayPosition: Int){
        db.collection("users").document(registeredName)
            .collection("ToDo").document(sortedTitleArray[arrayPosition])
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    private fun getIndividualPoint() {
        db.collection("users")
            .document(registeredName)
            .collection("ClearedToDo")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    clearedTitleArray.add(document.id)
                    Log.d("clearedTitleArray", clearedTitleArray.toString())
                }
                getClearedPointSum()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun getClearedPointSum(){
        for (i in 0 until clearedTitleArray.size) {
            db.collection("users")
                .document(registeredName)
                .collection("ClearedToDo")
                .document(clearedTitleArray[i])
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userDocument = task.result
                        if (userDocument != null && userDocument.data != null) {
                            val pointString = userDocument.data?.get("point").toString()
                            Log.d("pointString", pointString)
                            if(pointString != "null"){
                                val clearedPoint: Int = pointString.toInt()
                                individualPointArray.add(clearedPoint)
                                Log.d("individualPointArray", individualPointArray.toString())
                                individualPointSum = individualPointArray.sum()
                                Log.d("individualPointSum", individualPointSum.toString())
                                putSum()
                            }
                        } else {
                            Log.d(TAG, "No such document(getClearedPointSum)")
                        }
                    } else {
                        Log.d(TAG, "get failed with " + task.exception)
                    }
                }
                .addOnFailureListener { e -> Log.d(TAG, "Error adding document$e") }
        }
    }

    private fun putSum(){
        val pointSumMap = hashMapOf(
            "sum" to individualPointSum
        )
        Log.d("pointSumMap", pointSumMap.toString())
        db.collection("users").document(registeredName)
            .collection("ClearedToDo").document("sum")
            .set(pointSumMap)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Error adding document", e)
            }
    }

    private fun getAllUsers(){
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}
