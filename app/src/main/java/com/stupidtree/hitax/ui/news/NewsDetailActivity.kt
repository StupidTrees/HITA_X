package com.stupidtree.hitax.ui.news

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.webkit.*
import com.stupidtree.hita.theta.utils.ActivityTools
import com.stupidtree.hitax.databinding.ActivityNewsDetailBinding
import com.stupidtree.style.base.BaseActivity

class NewsDetailActivity : BaseActivity<NewsViewModel, ActivityNewsDetailBinding>() {
    override fun initViewBinding(): ActivityNewsDetailBinding {
        return ActivityNewsDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarActionBack(binding.toolbar)
    }

    override fun getViewModelClass(): Class<NewsViewModel> {
        return NewsViewModel::class.java
    }

    /**
     * Js調用Java接口
     */
    private interface JsCallJavaObj {
        fun showBigImg(url: String)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews() {        //支持javascript
        //支持javascript
        binding.webview.webChromeClient = WebChromeClient()
        binding.webview.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        binding.webview.settings.javaScriptEnabled = true
        //支持自适应
        //支持自适应
        binding.webview.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        binding.webview.settings.loadWithOverviewMode = true
//java回调js代码，不要忘了@JavascriptInterface这个注解，不然点击事件不起作用
        //java回调js代码，不要忘了@JavascriptInterface这个注解，不然点击事件不起作用
        binding.webview.addJavascriptInterface(object : JsCallJavaObj {
            @JavascriptInterface
            override fun showBigImg(url: String) {
                ActivityTools.showMultipleImages(getThis(), listOf(url), 0, abs = true)
            }
        }, "jsCallJavaObj")
        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                setWebImageClick(view)
            }
        }
        binding.collapse.setExpandedTitleColor(Color.TRANSPARENT)
        binding.collapse.setCollapsedTitleTextColor(Color.TRANSPARENT)
        viewModel.metaData.observe(this) {
            it.data?.let { m ->
                if (m["time"] != null) binding.detailTime.text = Html.fromHtml(m["time"].toString())
                binding.webview.loadUrl("http://www.hitsz.edu.cn" + intent.getStringExtra("link"))
            }
        }
    }


    private fun setWebImageClick(view: WebView) {
        val jsCode = "javascript:(function(){" +
                "var imgs=document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<imgs.length;i++){" +
                "imgs[i].onclick=function(){" +
                "window.jsCallJavaObj.showBigImg(this.src);" +
                "}}})()"
        view.loadUrl(jsCode)
    }

    override fun onStart() {
        super.onStart()
        //wv.setBackgroundColor(0);
        binding.detailTitle.text = intent.getStringExtra("title")
        viewModel.refresh(intent.getStringExtra("link") ?: "")
    }
}