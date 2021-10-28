package com.stupidtree.hitax.ui.search.teacher

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.stupidtree.hitax.R
import com.stupidtree.hitax.ui.search.BasicFragmentSearchResult
import com.stupidtree.hitax.utils.ActivityUtils

class FragmentSearchTeacher : BasicFragmentSearchResult<TeacherSearched, SearchTeacherViewModel>() {


    override fun updateHintText(reload: Boolean, addedSize: Int) {
        result?.text = getString(R.string.teacher_total_searched, addedSize)
    }


    override fun getHolderLayoutId(): Int {
        return R.layout.dynamic_teacher_search_result_item
    }

    override fun bindListHolder(
        simpleHolder: SearchListAdapter.SimpleHolder?,
        data: TeacherSearched,
        position: Int
    ) {
        simpleHolder?.title?.text = data.name
        simpleHolder?.tag?.visibility = View.GONE
        simpleHolder?.subtitle?.text = data.department
        simpleHolder?.picture?.let {
            Glide.with(requireContext()).load(
                "http://faculty.hitsz.edu.cn/file/showHP.do?d=" +
                        data.id + "&&w=200&&h=200&&prevfix=200-"
            )
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.place_holder_avatar)
                .into(it)
        }
    }

    override fun getViewModelClass(): Class<SearchTeacherViewModel> {
        return SearchTeacherViewModel::class.java
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_search_result_1
    }

    override fun onItemClicked(card: View?, data: TeacherSearched, position: Int) {
        ActivityUtils.startOfficialTeacherActivity(
            requireContext(),
            data.id, data.url, data.name
        )
    }

}