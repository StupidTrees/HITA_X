package com.stupidtree.hitax.ui.about

import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.stupidtree.hitax.R
import com.stupidtree.hitax.databinding.DialogBottomUserAgreementBinding
import com.stupidtree.hitax.utils.ImageUtils.dp2px
import com.stupidtree.style.widgets.TransparentModeledBottomSheetDialog

class UserAgreementDialog :
    TransparentModeledBottomSheetDialog<UserAgreementViewModel, DialogBottomUserAgreementBinding>() {
    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_user_agreement
    }

    override fun initViewBinding(v: View): DialogBottomUserAgreementBinding {
        return DialogBottomUserAgreementBinding.bind(v)
    }

    override fun getViewModelClass(): Class<UserAgreementViewModel> {
        return UserAgreementViewModel::class.java
    }

    override fun initViews(v: View) {
        val views: MutableList<ViewGroup?> = mutableListOf(null, null)
        val adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return 2
            }
            override fun getPageTitle(position: Int): CharSequence {
                return if (position == 0) {
                    getString(R.string.name_user_agreement)
                } else {
                    getString(R.string.name_privacy_agreement)
                }
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
                views[position] = null
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                if(views[position]==null) {
                    views[position] =
                        layoutInflater.inflate(R.layout.dynamic_user_agreement,container,false) as ViewGroup?
                    val param = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                    views[position]?.layoutParams = param
                    container.addView(views[position])
                }
                return views[position] as View
            }
        }

        binding?.tabs?.setupWithViewPager(binding?.pager)
        binding?.pager?.adapter = adapter
        viewModel.userAgreementPageLiveData.observe(this){
            it.data?.let{
                (views[0]?.findViewById(R.id.text) as TextView?)?.text = Html.fromHtml(it)
            }
        }
        viewModel.privacyPolicyPageLiveData.observe(this){
            it.data?.let{
                (views[1]?.findViewById(R.id.text) as TextView?)?.text = Html.fromHtml(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }


}