package com.example.blinkit.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blinkit.CartListener
import com.example.blinkit.R
import com.example.blinkit.adapters.AdapterCartProducts
import com.example.blinkit.databinding.ActivityUserMainBinding
import com.example.blinkit.databinding.BsCartProductsBinding
import com.example.blinkit.roomdb.CartProducts
import com.example.blinkit.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class UsersMainActivity : AppCompatActivity() , CartListener{
    private lateinit var binding: ActivityUserMainBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var cartProductList: List<CartProducts>
    private lateinit var adapterCartProducts: AdapterCartProducts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()

        getTotalItemCountInCart()

        onCartClicked()

        onNextButtonClicked()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun onNextButtonClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }


    private fun getAllCartProducts() {
        viewModel.getAll().observe( this) {
            cartProductList = it
        }
    }

    private fun onCartClicked() {
        binding.llItemCart.setOnClickListener {
            if (cartProductList.isEmpty()) {
                // Show a message or handle an empty cart scenario
                Toast.makeText(this, "No products in the cart", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bsCartProductsBinding = BsCartProductsBinding.inflate(LayoutInflater.from(this))
            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductsBinding.root)

            bsCartProductsBinding.tvNumberOfProductCount.text = binding.tvNumberOfProductCount.text

            bsCartProductsBinding.btnNext.setOnClickListener {
                startActivity(Intent(this, OrderPlaceActivity::class.java))
            }

            adapterCartProducts = AdapterCartProducts()
            bsCartProductsBinding.rvProductsItems.adapter = adapterCartProducts

            adapterCartProducts.differ.submitList(cartProductList)
            bs.show()
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