package com.stupidtree.hitax.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.stupidtree.hitax.ui.main.MainActivity

class ActivitySplash : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}