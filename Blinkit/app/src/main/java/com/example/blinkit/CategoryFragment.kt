package com.example.blinkit

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkit.adapters.AdapterProduct
import com.example.blinkit.databinding.FragmentCategoryBinding
import com.example.blinkit.databinding.ItemViewProductBinding
import com.example.blinkit.models.Product
import com.example.blinkit.roomdb.CartProducts
import com.example.blinkit.viewmodels.UserViewModel
import kotlinx.coroutines.launch


class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private val viewModel : UserViewModel by viewModels()
    private var category: String? = null
    private lateinit var adapterProduct : AdapterProduct
    private var cartListener : CartListener ?  = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?  {
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        getProductCategory()
        setToolBarTitle()
        onSearchMenuClick()
        fetchCategoryProduct()
        onNavigationIconClick()
        return binding.root
    }

    private fun onNavigationIconClick() {
        binding.tbSearchFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }

    private fun onSearchMenuClick() {
        binding.tbSearchFragment.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.searchMenu -> {
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchCategoryProduct() {
        lifecycleScope.launch {
            viewModel.getCategoryProduct(category!!)
                .collect {
                    if (it.isEmpty()) {
                        binding.rvProducts.visibility = View.GONE
                        binding.tvText.visibility = View.VISIBLE
                    } else {
                        binding.rvProducts.visibility = View.VISIBLE
                        binding.tvText.visibility = View.GONE
                    }

                    adapterProduct = AdapterProduct(::onAddButtonClicked , ::onIncrementButtonClicked , ::onDecrementButtonClicked)
                    binding.rvProducts.adapter = adapterProduct
                    adapterProduct.differ.submitList(it)
                }
        }
    }

    private fun setToolBarTitle() {
        binding.tbSearchFragment.title = category
    }

    private fun getProductCategory() {
        val bundle = arguments
        category = bundle?.getString("category")
    }


   private fun onAddButtonClicked(product: Product , productBinding: ItemViewProductBinding){
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE

        //Step 1
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()


        cartListener?.showCartLayout(1)

       product.itemCount = itemCount
       lifecycleScope.launch {
           cartListener?.savingCartItemCount(1)
           saveProductInRoomDb(product)
           viewModel.updateItemCount(product , itemCount)
       }
    }


    fun onIncrementButtonClicked(product: Product,productBinding: ItemViewProductBinding){

        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++

        if(product.productStock!! + 1 > itemCountInc ) {
            productBinding.tvProductCount.text = itemCountInc.toString()

            cartListener?.showCartLayout(1)


            product.itemCount = itemCountInc
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInRoomDb(product)
                viewModel.updateItemCount(product, itemCountInc)

            }
        }

        else{
            Utils.showToast(requireContext(),"Stock Empty")
        }
    }

    fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding) {

        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()

        itemCountDec--

        product.itemCount = itemCountDec
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product , itemCountDec)

        }

        if (itemCountDec > 0) {
            productBinding.tvProductCount.text = itemCountDec.toString()

        } else {
            lifecycleScope.launch{viewModel.deleteCartProduct(product.productRandomId!!)}
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"

        }


        cartListener?.showCartLayout( -1)




    }

    private fun saveProductInRoomDb(product: Product) {
        val cartProduct = CartProducts(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹" + "${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            productImage = product.productImageUris?.get(0)!!,
            productCategory = product.productCategory,
            adminUid = product.adminUid
        )

        lifecycleScope.launch {
            viewModel.insertCartProduct(cartProduct)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is CartListener) {
            cartListener = context
        } else {
            throw ClassCastException("Please implement cart listener")
        }
    }

}