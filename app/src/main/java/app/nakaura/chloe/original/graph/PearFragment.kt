package app.nakaura.chloe.original.graph

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.nakaura.chloe.original.R
import app.nakaura.chloe.original.databinding.FragmentPearBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PearFragment : Fragment() {
    private var _binding: FragmentPearBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private val pearArray: ArrayList<String> = arrayListOf()
    private val sumArray: ArrayList<Float> = arrayListOf()
    private val userArray: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPearBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPearUsers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getPearUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //Log.d(ContentValues.TAG, "${document.id} => ${document.data["group"]}")
                    when(document.data["group"].toString()){
                        "pear" -> pearArray.add(document.id)
                    }
                    Log.d("pearArray", pearArray.toString())
                }
                getSum()
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun getSum() {
        for (i in 0 until pearArray.size) {
            db.collection("users")
                .document(pearArray[i])
                .collection("ClearedToDo")
                .document("sum")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userDocument = task.result
                        if (userDocument != null && userDocument.data != null) {
                            Log.d("sum", "${pearArray[i]} -> ${userDocument.data}")
                            val sumValue: Float = userDocument.data?.get("sum").toString().toInt().toFloat()
                            sumArray.add(sumValue)
                            userArray.add(pearArray[i])
                            Log.d("sumArray", sumArray.toString())
                        }
                    }
                    makeChart()
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun makeChart() {
        val barChart: BarChart = binding.pearChart
        Log.d("makeChart", "makeChart")
        barChart.axisLeft.apply {
            axisMinimum = 0F
            axisMaximum = 80F
            labelCount = 5
            textSize = 10F
            setDrawTopYLabelEntry(true)
        }
        barChart.axisRight.apply {
            axisMinimum = 0F
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawZeroLine(false)
            setDrawTopYLabelEntry(true)
        }
        val quarters = userArray
        val formatter: ValueFormatter = object : ValueFormatter(){
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return quarters[value.toInt()]
            }
        }
        val xAxis: XAxis = barChart.xAxis
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM;
        xAxis.valueFormatter = formatter

        barChart.xAxis.apply {
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textSize = 10F
        }

        barChart.description.isEnabled= false
        barChart.legend.isEnabled= false
        barChart.isScaleXEnabled = false

        val pearGraph: ArrayList<BarEntry> = ArrayList()
        if(sumArray.size != 0){
            for(i in 0 until sumArray.size){
                val individualPoint: Float = sumArray[i]
                pearGraph.add(BarEntry(i.toFloat(), individualPoint))
            }
        }

        val pearDataSet = BarDataSet(pearGraph, "pear").apply {
            isHighlightEnabled = false
            setColors(resources.getColor(R.color.light_green))
            setDrawValues(true)
            valueTextSize = 13F
        }

        val dataSets: MutableList<IBarDataSet> = ArrayList()
        dataSets.add(pearDataSet)

        barChart.data = BarData(dataSets)
        barChart.invalidate() // refresh
    }

}