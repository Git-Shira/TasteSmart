package com.example.mypostsapp.ui.main

import android.app.ProgressDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mypostsapp.AlertDialogUtils
import com.example.mypostsapp.MainActivity
import com.example.mypostsapp.R
import com.example.mypostsapp.databinding.LoginFragmentBinding
import com.example.mypostsapp.ui.Login.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var loadingDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = ProgressDialog(requireContext())
        loadingDialog.setMessage(getString(R.string.please_wait))
        viewModel.onError.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            AlertDialogUtils.showAlert(requireContext(), getString(R.string.error), it)
        }
        viewModel.onSignInSuccess.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            activity?.finish()
        }


        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.login_navigation_to_create_fragment)
        }

        binding.signIn.setOnClickListener {
            if(isValidInput()) {
                loadingDialog.show()
                viewModel.onSignInClicked(binding.emailText.text.toString(), binding.passwordText.text.toString())
            }

        }

        binding.emailText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.emailError.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.passwordText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.passError.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }

    private fun isValidInput() : Boolean {
        var  isValid = true
        if (binding.emailText.text.toString().isNullOrEmpty()) {
            binding.emailError.visibility = View.VISIBLE
            isValid = false
        }
        if (binding.passwordText.text.toString().isNullOrEmpty()) {
            binding.passError.visibility = View.VISIBLE
            isValid = false
        }

        return isValid
    }

}