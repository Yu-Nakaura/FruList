package app.nakaura.chloe.original

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.content.res.AppCompatResources
import app.nakaura.chloe.original.databinding.FragmentSignupBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore

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

        binding.registerButton.setOnClickListener {
            registerUser()
            changeViewToGroup()
        }

        changeTextColor()

        binding.okButton.setOnClickListener {
            if (binding.okButton.isEnabled) {
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

    private fun changeTextColor() {
        //apple
        binding.appleImage.setOnClickListener {
            binding.appleText.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1, R.color.dark_red
                )
            })
            binding.lemonText.setTextColor(Color.GRAY)
            binding.pearText.setTextColor(Color.GRAY)
            binding.grapeText.setTextColor(Color.GRAY)
            binding.appleImage.alpha = 1.0f
            binding.lemonImage.alpha = 0.5f
            binding.pearImage.alpha = 0.5f
            binding.grapeImage.alpha = 0.5f
            binding.okButton.alpha = 1.0f
            binding.okButton.isEnabled = true
        }
        //lemon
        binding.lemonImage.setOnClickListener {
            binding.lemonText.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1, R.color.dark_yellow
                )
            })
            binding.appleText.setTextColor(Color.GRAY)
            binding.pearText.setTextColor(Color.GRAY)
            binding.grapeText.setTextColor(Color.GRAY)
            binding.appleImage.alpha = 0.5f
            binding.lemonImage.alpha = 1.0f
            binding.pearImage.alpha = 0.5f
            binding.grapeImage.alpha = 0.5f
            binding.okButton.alpha = 1.0f
            binding.okButton.isEnabled = true
        }
        //pear
        binding.pearImage.setOnClickListener {
            binding.pearText.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1, R.color.dark_green
                )
            })
            binding.appleText.setTextColor(Color.GRAY)
            binding.lemonText.setTextColor(Color.GRAY)
            binding.grapeText.setTextColor(Color.GRAY)
            binding.appleImage.alpha = 0.5f
            binding.lemonImage.alpha = 0.5f
            binding.pearImage.alpha = 1.0f
            binding.grapeImage.alpha = 0.5f
            binding.okButton.alpha = 1.0f
            binding.okButton.isEnabled = true
        }
        //grape
        binding.grapeImage.setOnClickListener {
            binding.grapeText.setTextColor(context?.let { it1 ->
                AppCompatResources.getColorStateList(
                    it1, R.color.dark_purple
                )
            })
            binding.appleText.setTextColor(Color.GRAY)
            binding.lemonText.setTextColor(Color.GRAY)
            binding.pearText.setTextColor(Color.GRAY)
            binding.appleImage.alpha = 0.5f
            binding.lemonImage.alpha = 0.5f
            binding.pearImage.alpha = 0.5f
            binding.grapeImage.alpha = 1.0f
            binding.okButton.alpha = 1.0f
            binding.okButton.isEnabled = true
        }
    }

    private fun registerUser() {
        val userName: String = binding.userSignupText.text.toString()
        val password: String = binding.passwordSignupText.text.toString()

        Log.d("userName", userName)
        Log.d("password", password)

        val userInfoMap = hashMapOf(
            "userName" to userName,
            "password" to password,
        )
        Log.d("userInfoMap", userInfoMap.toString())
        db.collection("users")
            .add(userInfoMap)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.d(ContentValues.TAG, "Error adding document", e)
            }
    }

    private fun registerGroup(){
        var group: String = "nothing"
        binding.appleCard.setOnClickListener {
            group = "apple"
            Log.d("group", group)
        }
        binding.lemonCard.setOnClickListener {
            group = "lemon"
            Log.d("group", group)
        }
        binding.pearCard.setOnClickListener {
            group = "pear"
            Log.d("group", group)
        }
        binding.grapeCard.setOnClickListener {
            group = "grape"
            Log.d("group", group)
        }
        Log.d("group", group)
    }

}
