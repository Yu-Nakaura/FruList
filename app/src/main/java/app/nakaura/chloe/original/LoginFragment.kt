package app.nakaura.chloe.original

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.nakaura.chloe.original.databinding.FragmentLoginBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupButton.setOnClickListener {
            val signupFragment = SignupFragment()
            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.addToBackStack(null)
            fragmentTransaction?.replace(R.id.fragmentContainer, signupFragment)
            fragmentTransaction?.commit()
        }

        binding.loginButton.setOnClickListener {
            val userNameText: String = binding.userLoginText.text.toString()
            val passwordText: String = binding.passwordLoginText.text.toString()
            var information: Boolean = false

            db.collection("users")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        //Log.d(TAG, "${document.id} => ${document.data}")
                        val userName: String = document.data?.get("userName").toString()
                        val password: String = document.data?.get("password").toString()
                        /*
                        Log.d("userName", userName)
                        Log.d("password", password)
                        Log.d("userNameText", userNameText)
                        Log.d("passwordText", passwordText)
                         */
                        if (userName == userNameText && password == passwordText) {
                            information = true
                            break
                        }
                        //Log.d("information", information.toString())
                    }
                    //Log.d("information2", information.toString())
                    if(information){
                        toToDoFragment()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toToDoFragment() {
        val todoFragment = ToDoFragment()
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.replace(R.id.fragmentContainer, todoFragment)
        fragmentTransaction?.commit()
    }

}