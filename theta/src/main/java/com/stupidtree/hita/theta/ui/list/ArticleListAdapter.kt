package com.stupidtree.hita.theta.ui.list

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
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
import com.stupidtree.hita.theta.ui.user.UserListAdapter
import com.stupidtree.hita.theta.utils.ActivityTools
import com.stupidtree.hita.theta.utils.TextTools
import com.stupidtree.stupiduser.data.repository.LocalUserRepository
import com.stupidtree.stupiduser.util.ImageUtils
import com.stupidtree.style.base.BaseListAdapter
import com.stupidtree.style.base.BaseViewHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            binding.content.text = data?.content
            if (!data?.images.isNullOrEmpty()) {
                binding.imgLayout.visibility = View.VISIBLE
                binding.img1.visibility = View.VISIBLE
                if (data?.images?.size ?: 0 >= 3) {
                    com.stupidtree.hita.theta.utils.ImageUtils.loadArticleImageInto(
                        mContext,
                        data?.images?.get(2),
                        binding.img3
                    )
                    binding.img3.visibility = View.VISIBLE
                } else {
                    binding.img3.visibility = View.GONE
                }
                if (data?.images?.size ?: 0 >= 2) {
                    com.stupidtree.hita.theta.utils.ImageUtils.loadArticleImageInto(
                        mContext,
                        data?.images?.get(1),
                        binding.img2
                    )
                    binding.img2.visibility = View.VISIBLE
                } else {
                    binding.img2.visibility = View.GONE
                }
                com.stupidtree.hita.theta.utils.ImageUtils.loadArticleImageInto(
                    mContext,
                    data?.images?.get(0),
                    binding.img1
                )

            } else {
                binding.imgLayout.visibility = View.GONE
            }
            binding.author.text = data?.authorName
            binding.likeNum.text = data?.likeNum.toString()
            binding.commentNum.text = data?.commentNum.toString()
            binding.time.text = TextTools.getArticleTimeText(mContext, data?.createTime)
            if(data?.topicId.isNullOrEmpty()){
                binding.topicLayout.visibility = View.GONE
            }else{
                binding.topicName.text = data?.topicName
                binding.topicLayout.visibility = View.VISIBLE
            }
            ImageUtils.loadAvatarInto(mContext, data?.authorAvatar, binding.avatar)
            if (data?.repostId.isNullOrEmpty()) {
                binding.repostLayout.visibility = View.GONE
            } else {
                binding.repostLayout.visibility = View.VISIBLE
                binding.repostLayout.setOnClickListener {
                    ActivityTools.startArticleDetail(mContext,data?.repostId?:"")
                }
                binding.repostAuthor.text = data?.repostAuthorName
                binding.repostContent.text = data?.repostContent
                binding.repostTime.text =
                    TextTools.getArticleTimeText(mContext, data?.repostTime)
                ImageUtils.loadAvatarInto(mContext, data?.repostAuthorAvatar, binding.repostAvatar)
                if (!data?.repostImages.isNullOrEmpty()) {
                    binding.repostImgLayout.visibility = View.VISIBLE
                    binding.repostImg1.visibility = View.VISIBLE
                    binding.repostImg2.visibility = View.VISIBLE
                    binding.repostImg3.visibility = View.VISIBLE
                    if (data?.repostImages?.size ?: 0 < 3) {
                        binding.repostImg3.visibility = View.GONE
                    } else {
                        com.stupidtree.hita.theta.utils.ImageUtils.loadArticleImageInto(
                            mContext,
                            data?.repostImages?.get(2),
                            binding.repostImg3
                        )
                    }
                    if (data?.repostImages?.size ?: 0 < 2) {
                        binding.repostImg2.visibility = View.GONE
                    } else {
                        com.stupidtree.hita.theta.utils.ImageUtils.loadArticleImageInto(
                            mContext,
                            data?.repostImages?.get(1),
                            binding.repostImg2
                        )
                    }
                    com.stupidtree.hita.theta.utils.ImageUtils.loadArticleImageInto(
                        mContext,
                        data?.repostImages?.get(0),
                        binding.repostImg1
                    )

                } else {
                    binding.repostImgLayout.visibility = View.GONE
                }
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
                        likeCall = ArticleWebSource.getInstance(mContext).likeOrUnlike(
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
            ArticleWebSource.getInstance(mContext).getArticleInfoCall(
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
        ArticleWebSource.getInstance(mContext).getArticleInfoCall(
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


    fun removeItems(articleId: List<String>) {
        if (articleId.isNullOrEmpty()) return
        val newList = mBeans.toMutableList()
        newList.removeAll {
            articleId.contains(it.id)
        }
        notifyItemChangedSmooth(newList)
    }
}