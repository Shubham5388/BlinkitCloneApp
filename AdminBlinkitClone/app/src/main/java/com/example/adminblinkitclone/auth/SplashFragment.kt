package com.example.adminblinkitclone.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminblinkitclone.AdminMainActivity
import com.example.adminblinkitclone.R
import com.example.adminblinkitclone.databinding.FragmentSplashBinding
import com.example.adminblinkitclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        setStatusBarColor()
        Handler(Looper.getMainLooper()).postDelayed({


            lifecycleScope.launch {
                viewModel.isACurrentUser.collect {

                    if (it) {
                        startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                        requireActivity().finish()
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_signinFragment)
                    }

                }}
        },
            3000
        )
        return binding.root
    }
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}