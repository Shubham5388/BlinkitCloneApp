package com.example.blinkit.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.example.blinkit.CartListener
import com.example.blinkit.R
import com.example.blinkit.databinding.ActivityUserMainBinding
import com.example.blinkit.viewmodels.UserViewModel

class UsersMainActivity : AppCompatActivity() , CartListener{
    private lateinit var binding: ActivityUserMainBinding
    private val viewModel : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getTotalItemCountInCart()
        onCartClicked()
        
    }

    private fun onCartClicked() {
        binding.llCart.setOnClickListener{

        }
    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalCartItemCount().observe(this) {
if(it>0){
    binding.llCart.visibility = View.VISIBLE
    binding.tvNumberOfProductCount.text = it.toString()

}

            else{
                binding.llCart.visibility = View.GONE
            }
        }
    }

    override fun showCartLayout( itemCount : Int) {
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if (updatedCount > 0) {
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        } else {
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }
    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemCount().observe(this) {
            viewModel.savingCartItemCount( it + itemCount)
        }
    }
}