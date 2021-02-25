package com.stupidtree.hitax.ui.eas.login

import android.app.Activity
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.stupidtree.hitax.R
import com.stupidtree.hitax.data.repository.EASRepository
import com.stupidtree.hitax.databinding.DialogBottomEasVerifyBinding
import com.stupidtree.hitax.ui.base.DataState
import com.stupidtree.hitax.ui.base.TransparentModeledBottomSheetDialog
import com.stupidtree.hitax.utils.ImageUtils

class PopUpLoginEAS :
    TransparentModeledBottomSheetDialog<LoginEASViewModel, DialogBottomEasVerifyBinding>() {

    var lock = false
    var onResponseListener: OnResponseListener? = null

    override fun getViewModelClass(): Class<LoginEASViewModel> {
        return LoginEASViewModel::class.java
    }

    override fun initViews(view: View) {
        viewModel.loginResultLiveData.observe(this) {
            val iconId: Int = if (it.state == DataState.STATE.SUCCESS) {
                R.drawable.ic_baseline_done_24
            } else {
                R.drawable.ic_baseline_error_24
            }
            val bitmap = ImageUtils.getResourceBitmap(requireContext(), iconId)
            binding?.buttonLogin?.doneLoadingAnimation(
                getColorPrimary(), bitmap
            )
            if (it.state == DataState.STATE.SUCCESS) {
                onResponseListener?.onSuccess(this)

            } else {
                binding?.buttonLogin?.postDelayed({
                    binding?.buttonLogin?.revertAnimation()
                }, 600)
                onResponseListener?.onFailed(this)
            }
        }
        binding?.buttonLogin?.setOnClickListener {
            if (isFormValid()) {
                binding?.buttonLogin?.startAnimation()
                viewModel.startLogin(
                    binding?.username?.text.toString(),
                    binding?.password?.text.toString()
                )
            }
        }
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                binding?.buttonLogin?.background = ContextCompat.getDrawable(
                    requireContext(), if (isFormValid()) {
                        R.drawable.element_rounded_button_bg_primary
                    } else {
                        R.drawable.element_rounded_button_bg_grey
                    }
                )
            }

        }
        binding?.username?.addTextChangedListener(textWatcher)
        binding?.password?.addTextChangedListener(textWatcher)
    }

    fun isFormValid(): Boolean {
        return !binding?.username?.text.isNullOrEmpty() && !binding?.password?.text.isNullOrEmpty()
    }

    interface OnResponseListener {
        fun onSuccess(window:PopUpLoginEAS)
        fun onFailed(window: PopUpLoginEAS)
    }

    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_eas_verify
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if(lock){
            if(context is Activity){
                (context as Activity).finish()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        val token = EASRepository.getInstance(requireActivity().application)
            .getEasToken()
        binding?.username?.setText(token.username)
        binding?.password?.setText(token.password)
    }

    override fun initViewBinding(v: View): DialogBottomEasVerifyBinding {
        return DialogBottomEasVerifyBinding.bind(v)
    }

}