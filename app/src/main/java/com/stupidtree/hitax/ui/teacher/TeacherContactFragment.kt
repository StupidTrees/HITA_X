package com.stupidtree.hitax.ui.teacher

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.FragmentTeacherContactBinding
import com.stupidtree.style.widgets.TransparentBottomSheetDialog

class TeacherContactFragment : TransparentBottomSheetDialog<FragmentTeacherContactBinding>() {
    private var phoneS: String? = null
    private var emailS: String? = null
    private var addressS: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            val contact: Bundle? = arguments
            phoneS = contact?.getString("phone")
            emailS = contact?.getString("email")
            addressS = contact?.getString("address")
        }
    }

    private fun setInfo() {
        if (TextUtils.isEmpty(phoneS)) binding.phone.setText(R.string.no_teacher_contact_data) else binding.phone.text =
            phoneS
        if (TextUtils.isEmpty(emailS)) binding.email.setText(R.string.no_teacher_contact_data) else binding.email.text =
            emailS
        if (TextUtils.isEmpty(addressS)) binding.address.setText(R.string.no_teacher_contact_data) else binding.address.text =
            addressS
    }

    companion object {
        fun newInstance(contact: Map<String, String>): TeacherContactFragment {
            val b = Bundle()
            b.putString("phone", contact["phone"])
            b.putString("email", contact["email"])
            b.putString("address", contact["address"])
            val f = TeacherContactFragment()
            f.arguments = b
            return f
        }
    }

    override fun onStart() {
        super.onStart()
        setInfo()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_teacher_contact
    }

    override fun initViewBinding(v: View): FragmentTeacherContactBinding {
        return FragmentTeacherContactBinding.bind(v)
    }

    override fun initViews(v: View) {

    }
}