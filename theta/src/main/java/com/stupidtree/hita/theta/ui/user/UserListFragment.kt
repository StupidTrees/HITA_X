package com.stupidtree.hita.theta.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.databinding.FragmentUserListBinding
import com.stupidtree.hita.theta.ui.user.UserListViewModel.Companion.PAGE_SIZE
import com.stupidtree.stupiduser.data.model.UserProfile
import com.stupidtree.style.base.BaseFragment
import com.stupidtree.style.base.BaseListAdapter

class UserListFragment :
    BaseFragment<UserListViewModel, FragmentUserListBinding>() {
    lateinit var listAdapter: UserListAdapter
    lateinit var mode: String
    var extra = ""
    override fun getViewModelClass(): Class<UserListViewModel> {
        return UserListViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getString("mode", "following") ?: "following"
        extra = arguments?.getString("extra", "") ?: ""
    }


    override fun initViews(view: View) {
        listAdapter = UserListAdapter(binding?.list, requireContext(), mutableListOf())
        binding?.refresh?.setColorSchemeColors(getColorPrimary())
        binding?.list?.adapter = listAdapter
        binding?.list?.layoutManager = LinearLayoutManager(requireContext())
        binding?.refresh?.setOnRefreshListener {
            viewModel.refreshAll(mode, extra)
        }
        listAdapter.setOnItemClickListener(object :
            BaseListAdapter.OnItemClickListener<UserProfile> {
            override fun onItemClick(data: UserProfile?, card: View?, position: Int) {
                val userProfile = Class.forName("com.stupidtree.hitax.ui.profile")
                val i = Intent(requireContext(), userProfile)
                i.putExtra("UserId", data?.id)
                startActivity(i)
            }
        })
        binding?.list?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (binding?.list?.canScrollVertically(1) != true && listAdapter.itemCount >= PAGE_SIZE) {
                        binding?.refresh?.isRefreshing = true
                        viewModel.loadMore(mode, extra)
                    }
                }
            }
        })
        viewModel.usersLiveData.observe(this) {
            binding?.refresh?.isRefreshing = false
            if (it.state == DataState.STATE.SUCCESS) {
                when (it.listAction) {
                    DataState.LIST_ACTION.APPEND -> {
                        it.data?.let { it1 ->
                            listAdapter.notifyItemsAppended(it1)
                            if (it1.isNotEmpty()) {
                                binding?.list?.smoothScrollBy(0, 300)
                            }
                        }
                    }
                    else -> {
                        it.data?.let { it1 -> listAdapter.notifyItemChangedSmooth(it1) }
                    }
                }
            }
        }
    }

    var isFirst = true
    override fun onStart() {
        super.onStart()
        if (isFirst) {
            isFirst = false
            refreshAll()
        }
    }

    private fun refreshAll() {
        binding?.refresh?.isRefreshing = true
        viewModel.refreshAll(arguments?.getString("mode", "following") ?: "following", extra)

    }

    override fun initViewBinding(): FragmentUserListBinding {
        return FragmentUserListBinding.inflate(layoutInflater)
    }

    companion object {
        fun newInstance(mode: String, extra: String): UserListFragment {
            val r = UserListFragment()
            val b = Bundle()
            b.putString("mode", mode)
            b.putString("extra", extra)
            r.arguments = b
            return r
        }
    }
}