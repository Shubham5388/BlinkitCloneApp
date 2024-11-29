package com.example.adminblinkitclone.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminblinkitclone.AdminMainActivity
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.Utils
import com.example.adminblinkitclone.databinding.FragmentOTPBinding
import com.example.adminblinkitclone.models.Admins
import com.example.adminblinkitclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber :String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)
        getUserNumber()
        customizingEnteringOTP()
        sendOTP()
        onLoginButtonClicked()
        onBackButtonClicked()
        return binding.root
    }



    private fun onLoginButtonClicked() {
        binding.btnLogin.setOnClickListener {
            Utils.showDialog(requireContext(), message = "Signing you...")

            val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3,
                binding.etOtp4,binding.etOtp5 , binding.etOtp6)
            val otp = editTexts.joinToString(separator = "") { it.text.toString() }

            if (otp.length < editTexts.size) {
                Utils.showToast(requireContext(), message = "Please enter right otp")
            } else {
                editTexts.forEach {
                    it.text?.clear();
                    it.clearFocus()
                }
                verifyOtp(otp)
            }
        }
    }

    private fun verifyOtp(otp: String) {
        // Remove this line for now, wait for the user to be authenticated before fetching the uid
        // val user = Users(uid = Utils.getCurrentUserId(), userPhoneNumber = userNumber, userAddress = null)

        // Pass OTP for authentication
        viewModel.signInWithPhoneAuthCredential(otp, userNumber)
        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect { isSuccess ->
                if (isSuccess) {
                    // User is authenticated, now we can safely access the current user's UID
                    val userId = Utils.getCurrentUserId()
                    val user = Admins(uid = userId, userPhoneNumber = userNumber)

                    // Save user info to the database
                    viewModel.saveUserToDatabase(user)

                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Logged in...")
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }






    private fun sendOTP(){
        Utils.showDialog(requireContext(), "Sending OTP...")

        viewModel.apply {
            sendOTP(userNumber , requireActivity())

            lifecycleScope.launch {
                otpSent.collect{
                    if (it){
                        Utils.hideDialog()
                        Utils.showToast(requireContext(),"Otp send")
                    }
                }
            }
        }
    }


    private fun onBackButtonClicked(){
        binding.tbOtpFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_OTPFragment_to_signinFragment)
        }

    }




    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4,
            binding.etOtp5 , binding.etOtp6)
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                    } else if (s?.length == 0) {
                        if (i > 0) {
                            editTexts[i - 1].requestFocus()
                        }
                    }
                }
            })
        }
    }


    private fun getUserNumber(){
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text=userNumber
    }



}