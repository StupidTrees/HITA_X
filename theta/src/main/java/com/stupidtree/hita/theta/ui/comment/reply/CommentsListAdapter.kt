package com.stupidtree.hita.theta.ui.comment.reply

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.data.model.Comment
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.source.web.ArticleWebSource
import com.stupidtree.hita.theta.data.source.web.CommentWebSource
import com.stupidtree.hita.theta.data.source.web.service.codes
import com.stupidtree.hita.theta.databinding.ActivityArticleDetailBinding
import com.stupidtree.hita.theta.databinding.ActivityArticleDetailCommentItemBinding
import com.stupidtree.hita.theta.ui.comment.CreateCommentFragment
import com.stupidtree.hita.theta.utils.ActivityTools
import com.stupidtree.hita.theta.utils.TextTools
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentsListAdapter(private val hostId:String?, mContext: Context, mBeans: MutableList<Comment>) :
    BaseListAdapter<Comment, CommentsListAdapter.CHolder>(
        mContext, mBeans
    ) {
    private val localUserRepo = LocalUserRepository.getInstance(mContext.applicationContext)

    class CHolder(viewBinding: ActivityArticleDetailCommentItemBinding) :
        BaseViewHolder<ActivityArticleDetailCommentItemBinding>(
            viewBinding
        ) {
        var likeCall: Call<ApiResponse<LikeResult>>? = null
        fun bindLike(data: Comment?) {
            data?.let {
                binding.likeNum.text = it.likeNum.toString()
                binding.likeIcon.setImageResource(
                    if (it.liked) {
                        R.drawable.ic_like_filled
                    } else {
                        R.drawable.ic_like_outline
                    }
                )
            }
        }
    }

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return ActivityArticleDetailCommentItemBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): CHolder {
        return CHolder(viewBinding as ActivityArticleDetailCommentItemBinding)
    }

    override fun bindHolder(holder: CHolder, data: Comment?, position: Int) {
        holder.binding.content.text = data?.content
        holder.binding.name.text = data?.authorName
        holder.binding.time.text = TextTools.getArticleTimeText(mContext, data?.createTime)
        holder.binding.tocomment.visibility =
            if (data?.replyId.isNullOrEmpty() || data?.replyId==hostId) View.GONE else View.VISIBLE
        holder.binding.touser.text = data?.receiverName
        ImageUtils.loadAvatarInto(mContext, data?.authorAvatar, holder.binding.avatar)
        holder.binding.card.setOnClickListener {
            mOnItemClickListener?.onItemClick(data, it, position)
        }
        holder.binding.authorLayout.setOnClickListener {
            data?.authorId?.let { it1 -> ActivityTools.startUserActivity(mContext, it1) }
        }
        holder.bindLike(data)
        holder.binding.likeIcon.setOnClickListener {
            val user = localUserRepo.getLoggedInUser()
            if (user.isValid()) {
                if (holder.likeCall == null) {
                    holder.likeCall = CommentWebSource.getInstance(mContext).likeOrUnlike(
                        user.token!!,
                        data?.id ?: "0",
                        data?.liked != true
                    )
                    holder.likeCall?.enqueue(object : Callback<ApiResponse<LikeResult>> {
                        override fun onResponse(
                            call: Call<ApiResponse<LikeResult>>,
                            response: Response<ApiResponse<LikeResult>>
                        ) {
                            holder.likeCall = null
                            if (response.body()?.code == codes.SUCCESS) {
                                response.body()?.data?.let {
                                    data?.liked = it.liked
                                    data?.likeNum = it.likeNum
                                    data?.let { it1 -> holder.bindLike(it1) }
                                }
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse<LikeResult>>, t: Throwable) {
                            holder.likeCall = null
                        }

                    })
                }

            }

        }
    }
}