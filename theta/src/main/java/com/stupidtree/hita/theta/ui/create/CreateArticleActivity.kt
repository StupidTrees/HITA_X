package com.stupidtree.hita.theta.ui.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.stupidtree.component.data.DataState
import com.stupidtree.hita.theta.R
import com.stupidtree.hita.theta.databinding.ActivityCreateArticleBinding
import com.stupidtree.hita.theta.ui.DirtyArticles
import com.stupidtree.hita.theta.utils.ActivityTools
import com.stupidtree.hita.theta.utils.ActivityTools.CHOOSE_TOPIC
import com.stupidtree.hita.theta.utils.ActivityTools.CHOOSE_TOPIC_RESULT
import com.stupidtree.hita.theta.utils.ImageUtils
import com.stupidtree.style.base.BaseActivity
import com.stupidtree.style.picker.FileProviderUtils
import com.stupidtree.style.picker.GalleryPicker.RC_CHOOSE_PHOTO

class CreateArticleActivity : BaseActivity<CreateArticleViewModel, ActivityCreateArticleBinding>() {

    lateinit var imageListAdapter: CreateImageListAdapter
    override fun initViewBinding(): ActivityCreateArticleBinding {
        return ActivityCreateArticleBinding.inflate(layoutInflater)
    }

    override fun getViewModelClass(): Class<CreateArticleViewModel> {
        return CreateArticleViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshRepostArticle(intent.getStringExtra("repostId"))

    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        popUpKeyboard()
    }

    private fun popUpKeyboard() {
        binding.content.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun bindLiveData() {
        viewModel.postResult.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                DirtyArticles.addAction(DataState.LIST_ACTION.PUSH_HEAD)

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    binding.done.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                } else {
                    binding.done.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                }
                val bitmap =
                    ImageUtils.getResourceBitmap(getThis(), R.drawable.ic_baseline_done_24)
                binding.done.doneLoadingAnimation(
                    getColorPrimary(), bitmap
                )
                binding.done.postDelayed({
                    binding.done.revertAnimation()
                }, 600)
                Toast.makeText(getThis(), R.string.post_success, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    binding.done.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                } else {
                    binding.done.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                }
                val bitmap =
                    ImageUtils.getResourceBitmap(getThis(), R.drawable.ic_baseline_error_24)
                binding.done.doneLoadingAnimation(
                    getColorPrimary(), bitmap
                )
                binding.done.postDelayed({
                    binding.done.revertAnimation()
                }, 600)
                Toast.makeText(getThis(), R.string.post_failed, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.repostArticleLiveData.observe(this) {
            if (it.state == DataState.STATE.SUCCESS) {
                binding.repost.visibility = View.VISIBLE
                if (it.data?.repostId.isNullOrEmpty()) {
                    com.stupidtree.stupiduser.util.ImageUtils.loadAvatarInto(
                        getThis(),
                        it.data?.authorAvatar,
                        binding.repostAvatar
                    )
                    binding.repostAuthor.text = it.data?.authorName
                    binding.repostContent.text = it.data?.content
                } else {
                    com.stupidtree.stupiduser.util.ImageUtils.loadAvatarInto(
                        getThis(),
                        it.data?.repostAuthorAvatar,
                        binding.repostAvatar
                    )
                    binding.repostAuthor.text = it.data?.repostAuthorName
                    binding.repostContent.text = it.data?.repostContent
                }
                if(it.data?.repostId.isNullOrEmpty()){
                    binding.content.setText("")
                }else{
                    binding.content.setText("//[u${it.data?.authorId}//@${it.data?.authorName}$]: ${it.data?.content}")
                }

            } else {
                binding.repost.visibility = View.GONE
            }
        }
        viewModel.imageUriLiveData.observe(this) {
            imageListAdapter.notifyItemChangedSmooth(it)
        }
        viewModel.topicIdLiveData.observe(this) { pair ->
            pair?.let {
                binding.topicName.text = it.second
                binding.topicCancel.visibility = View.VISIBLE
            } ?: run {
                binding.topicName.text = getString(R.string.choose_topic)
                binding.topicCancel.visibility = View.GONE
            }
        }

        viewModel.topicIdLiveData.value = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            //Toast.makeText(this, "操作取消", Toast.LENGTH_SHORT).show();
            return
        }
        if (requestCode == CHOOSE_TOPIC) {
            data?.getStringExtra("id")?.let { id ->
                data.getStringExtra("name")?.let { name ->
                    viewModel.setTopicInfo(id, name)
                }
            }
            return
        } else if (requestCode == RC_CHOOSE_PHOTO) {
            if (null == data) {
                Toast.makeText(this, R.string.no_image_fetched, Toast.LENGTH_SHORT).show()
                return
            }
            val uri = data.data
            if (null == uri) { //如果单个Uri为空，则可能是1:多个数据 2:没有数据
                val clipData = data.clipData
                if (clipData == null || clipData.itemCount == 0) { //没有数据，弹出提示
                    Toast.makeText(this, R.string.no_image_selected, Toast.LENGTH_SHORT).show()
                } else { //否则，加入多个图片
                    val vacancy: Int = 9 - (viewModel.imageUriLiveData.value?.size ?: 9)
                    var i = 0
                    while (i < clipData.itemCount && i < vacancy) {
                        val item = clipData.getItemAt(i)
                        FileProviderUtils.getFilePathByUri(this, item.uri)?.let {
                            viewModel.addImage(
                                it
                            )
                        }
                        i++
                    }
                }
                return
            } else {
                FileProviderUtils.getFilePathByUri(this, uri)?.let { viewModel.addImage(it) }
            }
        }
    }

    override fun initViews() {
        bindLiveData()
        binding.toolbar.title = ""
        binding.collapse.title = ""
        imageListAdapter = CreateImageListAdapter(this, viewModel, mutableListOf())
        binding.list.adapter = imageListAdapter
        binding.list.layoutManager = GridLayoutManager(this, 3)
        binding.title.setText(
            if (intent.getStringExtra("repostId")
                    .isNullOrEmpty()
            ) R.string.create_article else R.string.repost
        )
        // binding.done.setCardBackgroundColor(Color.GRAY)
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scale = 1.0f + verticalOffset / appBarLayout.height.toFloat()
            binding.title.translationX =
                (binding.toolbar.contentInsetStartWithNavigation + com.stupidtree.stupiduser.util.ImageUtils.dp2px(
                    getThis(),
                    8f
                )) * (1 - scale)
            binding.title.scaleX = 0.5f * (1 + scale)
            binding.title.scaleY = 0.5f * (1 + scale)
            binding.title.translationY =
                (binding.title.height / 2) * (1 - binding.title.scaleY)

            binding.done.translationY =
                com.stupidtree.stupiduser.util.ImageUtils.dp2px(getThis(), 24f) * (1 - scale)
            binding.done.scaleX = 0.7f + 0.3f * scale
            binding.done.scaleY = 0.7f + 0.3f * scale
            binding.done.translationX =
                (binding.done.width / 2) * (1 - binding.done.scaleX)
        })
        binding.chooseTopic.setOnClickListener {
            ActivityTools.startSearchTopicActivity(getThis())
        }
        binding.topicCancel.setOnClickListener {
            viewModel.topicIdLiveData.value = null
        }
        binding.done.alpha = 0.3f
        binding.done.isEnabled = false
        binding.content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    // binding.done.setCardBackgroundColor(getColorPrimary())
                    binding.done.alpha = 0.3f
                    binding.done.isEnabled = false
                } else {
                    // binding.done.setCardBackgroundColor(Color.GRAY)
                    binding.done.alpha = 1f
                    binding.done.isEnabled = true

                }
            }

        })

        binding.asAttitude.setOnCheckedChangeListener { _, isChecked ->
            viewModel.asAttitudeLiveData.value = isChecked
        }

        binding.anonymous.setOnCheckedChangeListener { _, isChecked ->
            viewModel.anonymousLiveData.value = isChecked
        }
        binding.done.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
            binding.done.startAnimation()
            viewModel.createArticle(binding.content.text.toString())
        }
    }
}