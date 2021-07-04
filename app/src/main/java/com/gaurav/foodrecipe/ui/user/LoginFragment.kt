package com.gaurav.foodrecipe.ui.user

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.databinding.FragmentLoginBinding
import com.gaurav.foodrecipe.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        auth = Firebase.auth
        binding.registerText.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }
        binding.forgetPassword.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment()
            )
        }
        binding.outlinedButtonLogin.setOnClickListener {
            val inputTextFieldEmail =
                binding.outlinedTextFieldEmail.editText?.text.toString().trim { it <= ' ' }
            val inputTextFieldPassword =
                binding.outlinedTextFieldPassword.editText?.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(inputTextFieldEmail)) {
                binding.outlinedTextFieldEmail.error = "Email cannot be empty!"
                binding.outlinedTextFieldEmail.editText?.doOnTextChanged { inputTextFieldEmail, _, _, _ ->
                    binding.outlinedTextFieldEmail.error = null
                }
                return@setOnClickListener
            } else if (TextUtils.isEmpty(inputTextFieldPassword)) {
                binding.outlinedTextFieldPassword.error = "Password cannot be empty!"
                binding.outlinedTextFieldPassword.editText?.doOnTextChanged { inputTextFieldPassword, _, _, _ ->
                    binding.outlinedTextFieldPassword.error = null
                }
                return@setOnClickListener
            }

            login(inputTextFieldEmail, inputTextFieldPassword)

        }
        return binding.root
    }

    private fun login(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBar.visibility = View.INVISIBLE
                val intent = Intent(context, HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            } else {
                binding.progressBar.visibility = View.INVISIBLE

                Toast.makeText(
                    context, it.exception?.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


}