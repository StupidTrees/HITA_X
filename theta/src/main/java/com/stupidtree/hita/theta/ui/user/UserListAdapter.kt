package com.stupidtree.hita.theta.ui.user

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.databinding.UserItemBinding
import com.stupidtree.stupiduser.data.model.FollowResult
import com.stupidtree.stupiduser.data.model.UserProfile
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.stupiduser.data.source.web.ProfileWebSource
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserListAdapter(
    val list: RecyclerView?,
    mContext: Context,
    mBeans: MutableList<UserProfile>
) :
    BaseListAdapter<UserProfile, UserListAdapter.AHolder>(
        mContext, mBeans
    ) {
    private val localUserRepo = LocalUserRepository.getInstance(mContext.applicationContext)

    inner class AHolder(viewBinding: UserItemBinding) :
        BaseViewHolder<UserItemBinding>(viewBinding) {
        var followCall: Call<ApiResponse<FollowResult>>? = null
        fun bindLike(data: UserProfile) {
            if(data.id==localUserRepo.getLoggedInUser().id){
                binding.follow.visibility = View.GONE
            }else{
                binding.follow.visibility = View.VISIBLE
            }
            binding.followIcon.setImageResource(
                if (data.followed) {
                    R.drawable.ic_unfollow
                } else {
                    R.drawable.ic_baseline_add_24
                }
            )
            binding.followText.text = if (data.followed) {
                "取消关注"
            } else {
                "关注"
            }
        }


    }

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return UserItemBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): AHolder {
        return AHolder(viewBinding as UserItemBinding)
    }

    override fun bindHolder(holder: AHolder, data: UserProfile?, position: Int) {
        data?.let {
            holder.bindLike(it)
            holder.binding.username.text = data.nickname
            holder.binding.description.text = data.signature
            holder.binding.item.setOnClickListener {
                val c = Class.forName("com.stupidtree.hitax.ui.profile.ProfileActivity")
                val i = Intent(mContext, c)
                i.putExtra("id", data?.id)
                mContext.startActivity(i)
            }
            ImageUtils.loadAvatarInto(mContext, it.avatar, holder.binding.avatar)
            holder.binding.follow.setOnClickListener { v ->
                if (holder.followCall == null) {
                    val user = localUserRepo.getLoggedInUser()
                    holder.followCall = ProfileWebSource.getInstance(mContext).followCall(
                        user.token ?: "", it.id, !it.followed
                    )
                    holder.followCall?.enqueue(object :
                        Callback<ApiResponse<FollowResult>> {
                        override fun onResponse(
                            call: Call<ApiResponse<FollowResult>>,
                            response: Response<ApiResponse<FollowResult>>
                        ) {
                            response.body()?.data?.let { fr ->
                                data.followed = fr.follow
                                holder.bindLike(data)
                            }
                            holder.followCall = null
                        }

                        override fun onFailure(
                            call: Call<ApiResponse<FollowResult>>,
                            t: Throwable
                        ) {
                            holder.followCall = null
                        }

                    })
                }
            }
        }
    }

}