package com.stupidtree.hita.theta.ui.message

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.data.model.Message
import com.stupidtree.hita.theta.databinding.MessageItemBinding
import com.stupidtree.hita.theta.utils.ActivityTools
import com.stupidtree.hita.theta.utils.ImageUtils
import com.stupidtree.hita.theta.utils.TextTools
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder

class MessagesListAdapter(mContext: Context, mBeans: MutableList<Message>) :
    BaseListAdapter<Message, MessagesListAdapter.MHolder>(
        mContext, mBeans
    ) {
    private val localRepo = LocalUserRepository.getInstance(mContext)

    class MHolder(viewBinding: MessageItemBinding) : BaseViewHolder<MessageItemBinding>(viewBinding)

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return MessageItemBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): MHolder {
        return MHolder(viewBinding as MessageItemBinding)
    }

    override fun bindHolder(holder: MHolder, data: Message?, position: Int) {
        holder.binding.name.text = data?.userName
        holder.binding.time.text = TextTools.getArticleTimeText(mContext, data?.createTime)
        com.stupidtree.stupiduser.util.ImageUtils.loadAvatarInto(
            mContext,
            data?.userAvatar,
            holder.binding.avatar
        )
        if (data?.actionContent.isNullOrEmpty()) {
            holder.binding.actionContent.visibility = View.GONE
        } else {
            holder.binding.actionContent.visibility = View.VISIBLE
            holder.binding.actionContent.text = data?.actionContent
        }
        when (data?.action) {
            Message.ACTION.COMMENT -> {
                holder.binding.ref.visibility = View.VISIBLE
                if (data.type == Message.TYPE.COMMENT) {
                    holder.binding.action.text = mContext.getString(R.string.comment_comment)
                } else {
                    holder.binding.action.text = mContext.getString(R.string.comment_article)
                }
            }
            Message.ACTION.LIKE -> {
                holder.binding.ref.visibility = View.VISIBLE
                if (data.type == Message.TYPE.COMMENT) {
                    holder.binding.action.text = mContext.getString(R.string.like_comment)
                } else {
                    holder.binding.action.text = mContext.getString(R.string.like_article)
                }
            }
            Message.ACTION.FOLLOW -> {
                holder.binding.ref.visibility = View.GONE
                holder.binding.action.text = mContext.getString(R.string.follow_you)
            }
            Message.ACTION.UNFOLLOW -> {
                holder.binding.ref.visibility = View.GONE
                holder.binding.action.text = mContext.getString(R.string.unfollow_you)
            }
            else -> {

            }
        }
        holder.binding.content.text = data?.content
        holder.binding.author.text = localRepo.getLoggedInUser().nickname
        com.stupidtree.stupiduser.util.ImageUtils.loadAvatarInto(
            mContext,
            localRepo.getLoggedInUser().avatar,
            holder.binding.authorAvatar
        )

        holder.binding.card.setOnClickListener {
            when (data?.action) {
                Message.ACTION.FOLLOW, Message.ACTION.UNFOLLOW -> {
                    ActivityTools.startUserActivity(mContext, localRepo.getLoggedInUser().id ?: "")
                }
                Message.ACTION.COMMENT, Message.ACTION.LIKE, Message.ACTION.REPOST -> {
                    if (data.type == Message.TYPE.ARTICLE) {
                        ActivityTools.startArticleDetail(mContext, data.referenceId)
                    } else {
                        ActivityTools.startCommentDetail(mContext, "", data.referenceId)
                    }
                }
            }
        }
    }
}