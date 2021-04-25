package com.stupidtree.hita.theta.ui.comment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.databinding.FragmentCreateCommentBinding
import com.stupidtree.style.widgets.TransparentModeledBottomSheetDialog

class CreateCommentFragment :
    TransparentModeledBottomSheetDialog<CreateCommentViewModel, FragmentCreateCommentBinding>
        () {
    var toArticleId: String = ""
    private var toCommentId: String? = null
    var contextId: String? = null
    var toUserId: String = ""
    private var sentListener: OnCommentSentListener?=null

    fun setOnCommentSentListener(l:OnCommentSentListener):CreateCommentFragment{
        this.sentListener = l
        return this
    }

    override fun onStart() {
        super.onStart()
        binding?.content?.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS)

    }

    interface OnCommentSentListener {
        fun onSuccess()
        fun onFailed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            toArticleId = it.getString("toArticleId").toString()
            toCommentId = it.getString("toCommentId")
            contextId = it.getString("contextId")
            toUserId = it.getString("toUserId").toString()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_create_comment
    }

    override fun initViewBinding(v: View): FragmentCreateCommentBinding {
        return FragmentCreateCommentBinding.bind(v)
    }

    override fun initViews(v: View) {
        binding?.send?.setOnClickListener {
            if (!binding?.content?.text.isNullOrEmpty()) {
                viewModel.postComment(
                    binding?.content?.text.toString(),
                    toUserId,
                    toArticleId,
                    contextId,
                    toCommentId
                )
            }
        }
        viewModel.createCommentResult.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                sentListener?.onSuccess()
                dismiss()
            }else{
                sentListener?.onFailed()
            }
        }
    }

    override fun getViewModelClass(): Class<CreateCommentViewModel> {
        return CreateCommentViewModel::class.java
    }


    companion object {

        fun newInstance(
            toArticleId: String,
            contextId: String?,
            toCommentId: String?,
            toUserId: String
        ): CreateCommentFragment {
            val b = Bundle()
            b.putString("toArticleId", toArticleId)
            b.putString("toCommentId", toCommentId)
            b.putString("contextId", contextId)
            b.putString("toUserId", toUserId)
            val f = CreateCommentFragment()
            f.arguments = b
            return f
        }
    }

}