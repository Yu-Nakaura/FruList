package app.nakaura.chloe.original

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.nakaura.chloe.original.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        val loginFragment = LoginFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, loginFragment)
        fragmentTransaction.commit()
    }
}