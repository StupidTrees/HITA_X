package com.stupidtree.hitax.ui.welcome.login

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.stupidtree.hitax.databinding.FragmentLoginBinding
import com.stupidtree.hitax.ui.base.BaseFragment

/**
 * 登录页面Fragment
 */
class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>() {

    override fun initViews(view: View) {
        //登录表单的数据变更监听器
        viewModel.loginFormState.observe(this, { loginFormState: LoginFormState ->
            //将表单合法性同步到登录按钮可用性
            binding?.login?.isEnabled = loginFormState.isDataValid
            //若有表单上的错误，则通知View显示错误
            if (loginFormState.usernameError != null) {
                binding?.username?.error = getString(loginFormState.usernameError!!)
            }
            if (loginFormState.passwordError != null) {
                binding?.password?.error = getString(loginFormState.passwordError!!)
            }
        })

        //登录结果的数据变更监听
        viewModel.loginResult.observe(this, { loginResult ->
            binding?.loading?.visibility = View.INVISIBLE
            if (loginResult != null) {
                Toast.makeText(context, loginResult.message, Toast.LENGTH_SHORT).show()
            }
            if (loginResult != null) {
                when (loginResult.state) {
                    LoginResult.STATES.SUCCESS -> {
                        requireActivity().finish()
                    }
                    LoginResult.STATES.WRONG_USERNAME -> {
                        binding?.username?.error = getString(loginResult.message)
                    }
                    LoginResult.STATES.WRONG_PASSWORD -> {
                        binding?.password?.error = getString(loginResult.message)
                    }
                    else -> {

                    }
                }
            }
        })

        // 登录表单的文本监视器
        val afterTextChangedListener: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                //将文本信息改变通知给ViewModel
                viewModel.loginDataChanged(binding?.username?.text.toString(),
                        binding?.password?.text.toString())
            }
        }
        binding?.username?.addTextChangedListener(afterTextChangedListener)
        binding?.password?.addTextChangedListener(afterTextChangedListener)

        //使得手机输入法上”完成“按钮映射到登录动作
        binding?.password?.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.login(binding?.username?.text.toString(),
                        binding?.password?.text.toString())
            }
            false
        }
        binding?.login?.setOnClickListener {
            binding?.loading?.visibility = View.VISIBLE
            viewModel.login(binding?.username?.text.toString(),
                    binding?.password?.text.toString())
        }
    }


    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun getViewModelClass(): Class<LoginViewModel> {
        return LoginViewModel::class.java
    }

    override fun initViewBinding(): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater)
    }
}