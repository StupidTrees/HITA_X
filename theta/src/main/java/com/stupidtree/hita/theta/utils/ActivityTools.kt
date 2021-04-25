package com.stupidtree.hita.theta.utils

import android.content.Context
import android.content.Intent
import com.stupidtree.hita.theta.ui.list.activity.ArticleListActivity
import com.stupidtree.hita.theta.ui.user.activity.UserListActivity

object ActivityTools {

    fun startUserListActivity(context: Context, title: String, mode: String, extra: String?) {
        val i = Intent(context, UserListActivity::class.java)
        i.putExtra("mode", mode)
        i.putExtra("title", title)
        i.putExtra("extra", extra)
        context.startActivity(i)
    }

    fun startArticleListActivity(context: Context, title: String, mode: String, extra: String?) {
        val i = Intent(context, ArticleListActivity::class.java)
        i.putExtra("mode", mode)
        i.putExtra("title", title)
        i.putExtra("extra", extra)
        context.startActivity(i)
    }

    fun startUserActivity(context: Context,userId:String){
        val c = Class.forName("com.stupidtree.hitax.ui.profile.ProfileActivity")
        val i = Intent(context, c)
        i.putExtra("id", userId)
        context.startActivity(i)
    }
}