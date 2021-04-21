package com.stupidtree.hita.theta.ui.list

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.stupidtree.component.data.DataState
import com.stupidtree.component.web.ApiResponse
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.data.model.Article
import com.stupidtree.hita.theta.data.model.LikeResult
import com.stupidtree.hita.theta.data.source.web.ArticleWebSource
import com.stupidtree.hita.theta.data.source.web.service.codes
import com.stupidtree.hita.theta.databinding.ArticleItemBinding
import com.stupidtree.hita.theta.ui.DirtyArticles
import com.stupidtree.hita.theta.ui.create.CreateArticleActivity
import com.stupidtree.hita.theta.ui.detail.ArticleDetailActivity
import com.stupidtree.hita.theta.utils.TextTools
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.atan

class ArticleListAdapter(
    val fragmentUUID: String,
    val list: RecyclerView?,
    mContext: Context,
    mBeans: MutableList<Article>
) :
    BaseListAdapter<Article, ArticleListAdapter.AHolder>(
        mContext, mBeans
    ) {
    private val localUserRepo = LocalUserRepository.getInstance(mContext.applicationContext)
    private val dirtyIds = mutableSetOf<String>()

    inner class AHolder(viewBinding: ArticleItemBinding) :
        BaseViewHolder<ArticleItemBinding>(viewBinding) {
        var likeCall: Call<ApiResponse<LikeResult>>? = null
        fun bindLike(data: Article) {
            binding.likeIcon.setImageResource(
                if (data.liked) {
                    R.drawable.ic_like_filled
                } else {
                    R.drawable.ic_like_outline
                }
            )
            binding.likeNum.text = data.likeNum.toString()
        }

        fun bindInfo(data: Article?) {
            binding.author.text = data?.authorName
            binding.content.text = data?.content
            binding.likeNum.text = data?.likeNum.toString()
            binding.commentNum.text = data?.commentNum.toString()
            binding.time.text = TextTools.getArticleTimeText(mContext, data?.createTime)
            ImageUtils.loadAvatarInto(mContext, data?.authorId, binding.avatar)
            if (data?.repostId.isNullOrEmpty()) {
                binding.repostLayout.visibility = View.GONE
            } else {
                binding.repostLayout.visibility = View.VISIBLE
                binding.repostLayout.setOnClickListener {
                    val i = Intent(mContext, ArticleDetailActivity::class.java)
                    i.putExtra("articleId", data?.repostId)
                    mContext.startActivity(i)
                }
                binding.repostAuthor.text = data?.repostAuthorName
                binding.repostContent.text = data?.repostContent
                binding.repostTime.text =
                    TextTools.getArticleTimeText(mContext, data?.repostTime)
                ImageUtils.loadAvatarInto(mContext, data?.repostAuthorId, binding.repostAvatar)
            }
            binding.item.setOnClickListener {
                mOnItemClickListener?.onItemClick(data, it, position)
            }
            binding.repostIcon.setOnClickListener {
                val i = Intent(mContext, CreateArticleActivity::class.java)
                i.putExtra("repostId", data?.id)
                mContext.startActivity(i)
            }

            binding.likeIcon.setOnClickListener {
                val user = localUserRepo.getLoggedInUser()
                if (user.isValid()) {
                    if (likeCall == null) {
                        likeCall = ArticleWebSource.likeOrUnlike(
                            user.token!!,
                            data?.id ?: "0",
                            data?.liked != true
                        )
                        likeCall?.enqueue(object : Callback<ApiResponse<LikeResult>> {
                            override fun onResponse(
                                call: Call<ApiResponse<LikeResult>>,
                                response: Response<ApiResponse<LikeResult>>
                            ) {
                                likeCall = null
                                if (response.body()?.code == codes.SUCCESS) {
                                    response.body()?.data?.let {
                                        data?.liked = it.liked
                                        data?.likeNum = it.likeNum
                                        data?.let { it1 -> bindLike(it1) }
                                    }
                                    DirtyArticles.addDirtyId(data?.id ?: "", fragmentUUID)
                                }
                            }

                            override fun onFailure(
                                call: Call<ApiResponse<LikeResult>>,
                                t: Throwable
                            ) {
                                likeCall = null
                            }

                        })
                    }

                }

            }
        }
    }

    override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
        return ArticleItemBinding.inflate(mInflater, parent, false)
    }

    override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): AHolder {
        return AHolder(viewBinding as ArticleItemBinding)
    }

    override fun bindHolder(holder: AHolder, data: Article?, position: Int) {
        holder.bindInfo(data)
        data?.let { holder.bindLike(it) }
        if (data?.id in dirtyIds) {
            dirtyIds.remove(data?.id)
            ArticleWebSource.getArticleInfoCall(
                localUserRepo.getLoggedInUser().token ?: "",
                data?.id, false
            ).enqueue(object : Callback<ApiResponse<Article>> {
                override fun onResponse(
                    call: Call<ApiResponse<Article>>,
                    response: Response<ApiResponse<Article>>
                ) {
                    response.body()?.data?.let {
                        val idx = indexOfArticle(data?.id ?: "")
                        if (idx >= 0) {
                            mBeans[idx] = it
                            holder.bindLike(it)
                            holder.bindInfo(it)
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Article>>, t: Throwable) {

                }

            })
        }
    }

    fun getTopTime(): Long {
        return if (mBeans.size > 0) {
            mBeans[0].createTime.time
        } else {
            Long.MAX_VALUE
        }
    }

    fun getBottomTime(): Long {
        return if (mBeans.size > 0) {
            mBeans[mBeans.size - 1].createTime.time
        } else {
            0L
        }
    }

    private fun indexOfArticle(articleId: String): Int {
        for (i in mBeans.indices.reversed()) {
            if (mBeans[i].id == articleId) {
                return i
            }
        }
        return -1
    }


    fun refreshItem(articleId: String) {
        val index = indexOfArticle(articleId)
        var holder: AHolder? = null
        if (index >= 0) {
            holder = list?.findViewHolderForAdapterPosition(index) as AHolder?
        }
        if (holder == null) {
            dirtyIds.add(articleId)
            return
        }
        ArticleWebSource.getArticleInfoCall(
            localUserRepo.getLoggedInUser().token ?: "",
            articleId, false
        ).enqueue(object : Callback<ApiResponse<Article>> {
            override fun onResponse(
                call: Call<ApiResponse<Article>>,
                response: Response<ApiResponse<Article>>
            ) {
                response.body()?.data?.let {
                    val idx = indexOfArticle(articleId)
                    if (idx >= 0) {
                        mBeans[idx] = it
                        holder.bindLike(it)
                        holder.bindInfo(it)
                    }
                }

            }

            override fun onFailure(call: Call<ApiResponse<Article>>, t: Throwable) {

            }

        })

    }
}