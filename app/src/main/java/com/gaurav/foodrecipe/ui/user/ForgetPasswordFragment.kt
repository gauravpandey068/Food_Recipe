package com.gaurav.foodrecipe.ui.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gaurav.foodrecipe.R
import com.gaurav.foodrecipe.databinding.FragmentForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgetPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_forget_password, container, false)

        binding.outlinedButtonReset.setOnClickListener {
            val email = binding.outlinedTextFieldEmail.editText?.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(email)) {
                binding.outlinedTextFieldEmail.error = "Email cannot be Empty!"
                binding.outlinedTextFieldEmail.editText?.doOnTextChanged { email, _, _, _ ->
                    binding.outlinedTextFieldEmail.error = null
                }
                return@setOnClickListener
            }
            resetPassword(email)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun resetPassword(email: String) {
        binding.progressBarReset.visibility = View.VISIBLE

        auth = Firebase.auth
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBarReset.visibility = View.INVISIBLE
                binding.outlinedTextFieldEmail.visibility = View.GONE
                binding.outlinedButtonReset.visibility = View.GONE
                binding.resetMessage.text =
                    "Password Reset Link is sent to register Email. Please check your Email."

            } else {
                binding.progressBarReset.visibility = View.INVISIBLE
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}