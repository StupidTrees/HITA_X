package com.stupidtree.hita.theta.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.stupidtree.hita.theta.ui.comment.reply.CommentReplyActivity
import com.stupidtree.hita.theta.ui.detail.ArticleDetailActivity
import com.stupidtree.hita.theta.ui.list.activity.ArticleListActivity
import com.stupidtree.hita.theta.ui.message.MessagesActivity
import com.stupidtree.hita.theta.ui.topic.detail.TopicDetailActivity
import com.stupidtree.hita.theta.ui.topic.search.SearchTopicActivity
import com.stupidtree.hita.theta.ui.user.activity.UserListActivity
import com.stupidtree.hita.theta.ui.widgets.PhotoDetailActivity

object ActivityTools {
    const val CHOOSE_TOPIC = 233
    const val CHOOSE_TOPIC_RESULT = 244
    fun startUserListActivity(context: Context, title: String, mode: String, extra: String?) {
        val i = Intent(context, UserListActivity::class.java)
        i.putExtra("mode", mode)
        i.putExtra("title", title)
        i.putExtra("extra", extra)
        context.startActivity(i)
    }

    fun startTopicDetailActivity(context: Context, topicId: String) {
        val i = Intent(context, TopicDetailActivity::class.java)
        i.putExtra("topicId", topicId)
        context.startActivity(i)
    }

    fun startSearchTopicActivity(context: Activity) {
        val i = Intent(context, SearchTopicActivity::class.java)
        context.startActivityForResult(i, CHOOSE_TOPIC)
    }

    fun startArticleListActivity(context: Context, title: String, mode: String, extra: String?) {
        val i = Intent(context, ArticleListActivity::class.java)
        i.putExtra("mode", mode)
        i.putExtra("title", title)
        i.putExtra("extra", extra)
        context.startActivity(i)
    }

    fun startArticleDetail(context: Context, articleId: String) {
        val i = Intent(context, ArticleDetailActivity::class.java)
        i.putExtra("articleId", articleId)
        context.startActivity(i)
    }

    fun startCommentDetail(context: Context, articleId: String="", commentId: String) {
        val i = Intent(context, CommentReplyActivity::class.java)
        i.putExtra("articleId",articleId)
        i.putExtra("commentId", commentId)
        context.startActivity(i)
    }

    fun startUserActivity(context: Context, userId: String) {
        val c = Class.forName("com.stupidtree.hitax.ui.profile.ProfileActivity")
        val i = Intent(context, c)
        i.putExtra("id", userId)
        context.startActivity(i)
    }

    fun startMessagesActivity(context: Context, title: String, mode: String) {
        val i = Intent(context, MessagesActivity::class.java)
        i.putExtra("mode", mode)
        i.putExtra("title", title)
        context.startActivity(i)
    }

    /**
     * 显示多张大图
     * @param from 上下文
     * @param urls 图片链接
     * @param index 初始显示的下必
     */
    fun showMultipleImages(from: Activity, ids: List<String>, index: Int) {
        val it = Intent(from, PhotoDetailActivity::class.java)
        //  ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(from,view,"image");
        val urlsArr = arrayOfNulls<String>(ids.size)
        for (i in urlsArr.indices) urlsArr[i] = ids[i]
        it.putExtra("ids", urlsArr)
        it.putExtra("init_index", index)
        from.startActivity(it) //,activityOptionsCompat.toBundle());
    }
}