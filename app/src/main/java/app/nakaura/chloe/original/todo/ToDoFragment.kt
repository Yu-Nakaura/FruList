package app.nakaura.chloe.original.todo

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import app.nakaura.chloe.original.AddFragment
import app.nakaura.chloe.original.EditFragment
import app.nakaura.chloe.original.R
import app.nakaura.chloe.original.databinding.FragmentToDoBinding
import app.nakaura.chloe.original.graph.GraphAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
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
    private val groupArray: ArrayList<String> = arrayListOf()
    private val userArray: ArrayList<String> = arrayListOf()
    private val appleArray: ArrayList<Int> = arrayListOf()
    private val lemonArray: ArrayList<Int> = arrayListOf()
    private val pearArray: ArrayList<Int> = arrayListOf()
    private val grapeArray: ArrayList<Int> = arrayListOf()
    private val individualPointArray: ArrayList<Int> = arrayListOf()
    private val toDoList = ArrayList<ToDo>()
    private val toDoAdapter = ToDoAdapter()
    private var individualPointSum: Int = 0
    private var appleArraySum: Int = 0
    private var lemonArraySum: Int = 0
    private var pearArraySum: Int = 0
    private var grapeArraySum: Int = 0
    var clearedPointNumber: Int = 0

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
        binding.individualButton.setOnClickListener {
            changeToIndividualView()
            /// adapterのインスタンス生成
            val graphAdapter = GraphAdapter(this)
            /// adapterをセット
            val viewPager2 = binding.viewPager
            viewPager2.adapter = graphAdapter
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
                                    val clearedPointString: String =
                                        userDocument.data?.get("point").toString()

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

        //OpenButton is checked
        toDoAdapter.setOnOpenButtonClickListener(
            object : ToDoAdapter.OnOpenButtonClickListener {
                override fun onItemClick(position: Int) {
                    db.collection("users")
                        .document(registeredName)
                        .collection("ToDo")
                        .document(sortedTitleArray[position])
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userDocument = task.result
                                if (userDocument != null && userDocument.data != null) {
                                    val getTitle: String = userDocument.data?.get("title").toString()
                                    val getPoint: String = userDocument.data?.get("point").toString()
                                    val getNote: String = userDocument.data?.get("note").toString()

                                    val bundle = Bundle()
                                    bundle.putString("title", getTitle)
                                    bundle.putString("point", getPoint)
                                    bundle.putString("note", getNote)
                                    bundle.putString("group", group)

                                    val editFragment = EditFragment()
                                    editFragment.arguments = bundle
                                    val fragmentTransaction = fragmentManager?.beginTransaction()
                                    fragmentTransaction?.addToBackStack(null)
                                    fragmentTransaction?.replace(R.id.fragmentContainer, editFragment)
                                    fragmentTransaction?.commit()
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
        binding.colorBarIndividual.isVisible = false
        binding.addButton.isVisible = true
        binding.recyclerView.isVisible = true
        binding.barChart.isVisible = false
        binding.appleIcon.isVisible = false
        binding.lemonIcon.isVisible = false
        binding.pearIcon.isVisible = false
        binding.grapeIcon.isVisible = false
        binding.appleNumber.isVisible = false
        binding.lemonNumber.isVisible = false
        binding.pearNumber.isVisible = false
        binding.grapeNumber.isVisible = false
        binding.viewPager.isVisible = false
        binding.barChartButton.isEnabled = true
        appleArray.clear()
        lemonArray.clear()
        pearArray.clear()
        grapeArray.clear()
    }

    private fun changeToChartView() {
        binding.colorBarPerson.isVisible = false
        binding.colorBarIndividual.isVisible = false
        binding.colorBarChart.isVisible = true
        binding.addButton.isVisible = false
        binding.recyclerView.isVisible = false
        binding.barChart.isVisible = true
        binding.appleIcon.isVisible = true
        binding.lemonIcon.isVisible = true
        binding.pearIcon.isVisible = true
        binding.grapeIcon.isVisible = true
        binding.appleNumber.isVisible = true
        binding.lemonNumber.isVisible = true
        binding.pearNumber.isVisible = true
        binding.grapeNumber.isVisible = true
        binding.viewPager.isVisible = false
        binding.barChartButton.isEnabled = false
        appleArraySum = 0
        lemonArraySum = 0
        pearArraySum = 0
        grapeArraySum = 0
        getAllUsers()
    }

    private fun changeToIndividualView() {
        binding.colorBarPerson.isVisible = false
        binding.colorBarChart.isVisible = false
        binding.colorBarIndividual.isVisible = true
        binding.addButton.isVisible = false
        binding.recyclerView.isVisible = false
        binding.barChart.isVisible = false
        binding.appleIcon.isVisible = false
        binding.lemonIcon.isVisible = false
        binding.pearIcon.isVisible = false
        binding.grapeIcon.isVisible = false
        binding.appleNumber.isVisible = false
        binding.lemonNumber.isVisible = false
        binding.pearNumber.isVisible = false
        binding.grapeNumber.isVisible = false
        binding.viewPager.isVisible = true
        binding.barChartButton.isEnabled = true
        appleArray.clear()
        lemonArray.clear()
        pearArray.clear()
        grapeArray.clear()
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
                binding.colorBarIndividual.setBackgroundResource(R.color.light_red)
            }
            "lemon" -> {
                Log.d("change", "changeToLemon")
                binding.toDoTitleText.setBackgroundResource(R.color.dark_yellow)
                binding.colorBarChart.setBackgroundResource(R.color.light_yellow)
                binding.colorBarPerson.setBackgroundResource(R.color.light_yellow)
                binding.colorBarIndividual.setBackgroundResource(R.color.light_yellow)
            }
            "pear" -> {
                Log.d("change", "changeToPear")
                binding.toDoTitleText.setBackgroundResource(R.color.dark_green)
                binding.colorBarChart.setBackgroundResource(R.color.light_green)
                binding.colorBarPerson.setBackgroundResource(R.color.light_green)
                binding.colorBarIndividual.setBackgroundResource(R.color.light_green)
            }
            "grape" -> {
                Log.d("change", "changeToGrape")
                binding.toDoTitleText.setBackgroundResource(R.color.dark_purple)
                binding.colorBarChart.setBackgroundResource(R.color.light_purple)
                binding.colorBarPerson.setBackgroundResource(R.color.light_purple)
                binding.colorBarIndividual.setBackgroundResource(R.color.light_purple)
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
                Log.d(TAG, "Error getting documents: ", exception)
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

    private fun setClearedPoint(clearedPointNumber: Int, arrayPosition: Int) {
        val clearedPointMap = hashMapOf(
            "point" to clearedPointNumber
        )
        Log.d("clearedPointMap", sortedTitleArray[arrayPosition] + "," + clearedPointMap.toString())

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

    private fun clearCheckedToDo(arrayPosition: Int) {
        db.collection("users").document(registeredName)
            .collection("ToDo").document(sortedTitleArray[arrayPosition])
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.d(TAG, "Error deleting document", e) }
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
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun getClearedPointSum() {
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
                            if (pointString != "null") {
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

    private fun putSum() {
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

    private fun getAllUsers() {
        userArray.clear()
        groupArray.clear()
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data["group"]}")
                    userArray.add(document.id)
                    Log.d("userArray", userArray.toString())
                    groupArray.add(document.data["group"].toString())
                    Log.d("groupArray", groupArray.toString())
                }
                getSum()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    private fun getSum() {
        for (i in 0 until userArray.size) {
            db.collection("users")
                .document(userArray[i])
                .collection("ClearedToDo")
                .document("sum")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userDocument = task.result
                        if (userDocument != null && userDocument.data != null) {
                            Log.d("sum", "${userArray[i]} -> ${userDocument.data}")
                            val sumData = userDocument.data?.get("sum").toString()
                            val groupData = groupArray[i]
                            Log.d("sumData", sumData)
                            Log.d("groupData", groupData)
                            val sumValue: Int = sumData.toInt()
                            distributePoint(groupData, sumValue)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun distributePoint(groupData: String, sumValue: Int) {
        when (groupData) {
            "apple" -> appleArray.add(sumValue)
            "lemon" -> lemonArray.add(sumValue)
            "pear" -> pearArray.add(sumValue)
            "grape" -> grapeArray.add(sumValue)
        }
        appleArraySum = appleArray.sum()
        lemonArraySum = lemonArray.sum()
        pearArraySum = pearArray.sum()
        grapeArraySum = grapeArray.sum()
        makeChart()
    }

    private fun makeChart() {
        val apple: Float = appleArraySum.toFloat()
        val lemon: Float = lemonArraySum.toFloat()
        val pear: Float = pearArraySum.toFloat()
        val grape: Float = grapeArraySum.toFloat()

        val barChart: BarChart = binding.barChart

        barChart.axisLeft.apply {
            axisMinimum = 0F
            axisMaximum = 100F
            labelCount = 5
            textSize = 10F
            setDrawTopYLabelEntry(true)
        }

        barChart.axisRight.apply {
            axisMinimum = 0F
            axisMaximum = 3F
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawZeroLine(false)
            setDrawTopYLabelEntry(true)
        }

        barChart.xAxis.apply {
            labelCount = 0
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawAxisLine(false)
        }

        barChart.description.isEnabled= false
        barChart.legend.isEnabled= false
        barChart.isScaleXEnabled = false

        //グラフのデータを設定
        val appleGraph: ArrayList<BarEntry> = ArrayList()
        appleGraph.add(BarEntry(0F, apple))

        val lemonGraph: ArrayList<BarEntry> = ArrayList()
        lemonGraph.add(BarEntry(1F, lemon))

        val pearGraph: ArrayList<BarEntry> = ArrayList()
        pearGraph.add(BarEntry(2F, pear))

        val grapeGraph: ArrayList<BarEntry> = ArrayList()
        grapeGraph.add(BarEntry(3F, grape))

        //chartに設定
        val appleDataSet = BarDataSet(appleGraph, "apple").apply {
            isHighlightEnabled = false
            setColors(resources.getColor(R.color.light_red))
        }

        val lemonDataSet = BarDataSet(lemonGraph, "lemon").apply {
            isHighlightEnabled = false
            setColors(resources.getColor(R.color.light_yellow))
        }

        val pearDataSet = BarDataSet(pearGraph, "pear").apply {
            isHighlightEnabled = false
            setColors(resources.getColor(R.color.light_green))
        }

        val grapeDataSet = BarDataSet(grapeGraph, "grape").apply {
            isHighlightEnabled = false
            setColors(resources.getColor(R.color.light_purple))
        }

        appleDataSet.setDrawValues(false)
        lemonDataSet.setDrawValues(false)
        pearDataSet.setDrawValues(false)
        grapeDataSet.setDrawValues(false)

        val dataSets: MutableList<IBarDataSet> = ArrayList()
        dataSets.add(appleDataSet)
        dataSets.add(lemonDataSet)
        dataSets.add(pearDataSet)
        dataSets.add(grapeDataSet)

        barChart.data = BarData(dataSets)
        barChart.invalidate() // refresh

        setGraphNumber()
    }

    private fun setGraphNumber(){
        binding.appleNumber.text = appleArraySum.toString()
        binding.lemonNumber.text = lemonArraySum.toString()
        binding.pearNumber.text = pearArraySum.toString()
        binding.grapeNumber.text = grapeArraySum.toString()

        if(appleArraySum != 0){
            binding.appleNumber.setTextColor(Color.WHITE)
        }

        if(lemonArraySum != 0){
            binding.lemonNumber.setTextColor(Color.WHITE)
        }

        if(pearArraySum != 0){
            binding.pearNumber.setTextColor(Color.WHITE)
        }

        if(grapeArraySum != 0){
            binding.grapeNumber.setTextColor(Color.WHITE)
        }
    }

}
