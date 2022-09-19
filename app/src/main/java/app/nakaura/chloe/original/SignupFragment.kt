package app.nakaura.chloe.original

import android.content.ContentValues
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import app.nakaura.chloe.original.databinding.FragmentSignupBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.okButton.alpha = 0.5f
        binding.okButton.isEnabled = false

        FirebaseFirestore.setLoggingEnabled(true);

        binding.registerButton.setOnClickListener {
            registerUser()
        }

        changeGroup()

        binding.okButton.setOnClickListener {
            if (binding.okButton.isEnabled) {
                registerGroup()
                val todoFragment = ToDoFragment()
                val fragmentTransaction = fragmentManager?.beginTransaction()
                fragmentTransaction?.addToBackStack(null)
                fragmentTransaction?.replace(R.id.fragmentContainer, todoFragment)
                fragmentTransaction?.commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeViewToGroup() {
        binding.userSignupText.visibility = View.INVISIBLE
        binding.passwordSignupText.visibility = View.INVISIBLE
        binding.registerButton.visibility = View.INVISIBLE
        binding.appleCard.visibility = View.VISIBLE
        binding.lemonCard.visibility = View.VISIBLE
        binding.pearCard.visibility = View.VISIBLE
        binding.grapeCard.visibility = View.VISIBLE
        binding.appleText.visibility = View.VISIBLE
        binding.lemonText.visibility = View.VISIBLE
        binding.pearText.visibility = View.VISIBLE
        binding.grapeText.visibility = View.VISIBLE
        binding.okButton.visibility = View.VISIBLE
        binding.groupTitleText.text = "グループを選ぼう"
    }

    private fun changeGroup() {
        var group: String = "nothing"
        //apple
        binding.appleImage.setOnClickListener {
            makeAllGray()
            binding.appleText.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1, R.color.dark_red
                )
            })
            changeTransmittance()
            binding.appleImage.alpha = 1.0f

            group = "apple"
            Log.d("group", group)

            sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            val groupName:String = group
            Log.d("groupName", groupName)
            val editor = sharedPref.edit()
            editor.putString("groupName", groupName)
            editor.apply()
        }
        //lemon
        binding.lemonImage.setOnClickListener {
            makeAllGray()
            binding.lemonText.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1, R.color.dark_yellow
                )
            })
            changeTransmittance()
            binding.lemonImage.alpha = 1.0f

            group = "lemon"
            Log.d("group", group)

            sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            val groupName:String = group
            Log.d("groupName", groupName)
            val editor = sharedPref.edit()
            editor.putString("groupName", groupName)
            editor.apply()
        }
        //pear
        binding.pearImage.setOnClickListener {
            makeAllGray()
            binding.pearText.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1, R.color.dark_green
                )
            })
            changeTransmittance()
            binding.pearImage.alpha = 1.0f

            group = "pear"
            Log.d("group", group)

            sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            val groupName:String = group
            Log.d("groupName", groupName)
            val editor = sharedPref.edit()
            editor.putString("groupName", groupName)
            editor.apply()
        }
        //grape
        binding.grapeImage.setOnClickListener {
            makeAllGray()
            binding.grapeText.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1, R.color.dark_purple
                )
            })
            changeTransmittance()
            binding.grapeImage.alpha = 1.0f

            group = "grape"
            Log.d("group", group)

            sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            val groupName:String = group
            Log.d("groupName", groupName)
            val editor = sharedPref.edit()
            editor.putString("groupName", groupName)
            editor.apply()
        }
    }

    private fun registerUser() {
        val userName: String = binding.userSignupText.text.toString()
        val password: String = binding.passwordSignupText.text.toString()
        Log.d("userName", userName)
        Log.d("password", password)

        if(userName.isNotEmpty() && password.isNotEmpty()){
            binding.warningText.isVisible = false
            val userInfoMap = hashMapOf(
                "userName" to userName,
                "password" to password,
            )
            Log.d("userInfoMap", userInfoMap.toString())
            db.collection("users").document("$userName")
                .set(userInfoMap)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    Log.d(ContentValues.TAG, "Error adding document", e)
                }
            changeViewToGroup()
        }else{
            binding.warningText.isVisible =true
            Log.d("empty", "please enter username and password")
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val userFileName:String = userName
        val editor = sharedPref.edit()
        editor.putString("userFileName", userFileName)
        editor.apply()
    }

    private fun registerGroup(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
        val RegisteredName:String? = sharedPref.getString("userFileName", "")
        Log.d("RegisteredName", RegisteredName.toString())
        val RegisteredGroup:String? = sharedPref.getString("groupName", "")
        Log.d("RegisteredGroup", RegisteredGroup.toString())
        val registeredGroup = hashMapOf(
            "group" to RegisteredGroup,
        )
        db.collection("users").document("$RegisteredName")
            .set(registeredGroup, SetOptions.merge())
    }

    private fun makeAllGray(){
        binding.appleText.setTextColor(Color.GRAY)
        binding.lemonText.setTextColor(Color.GRAY)
        binding.pearText.setTextColor(Color.GRAY)
        binding.grapeText.setTextColor(Color.GRAY)
    }

    private fun changeTransmittance(){
        binding.appleImage.alpha = 0.5f
        binding.lemonImage.alpha = 0.5f
        binding.pearImage.alpha = 0.5f
        binding.grapeImage.alpha = 0.5f
        binding.okButton.alpha = 1.0f
        binding.okButton.isEnabled = true
    }

}
