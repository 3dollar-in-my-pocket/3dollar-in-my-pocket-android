package com.zion830.threedollars

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("keyhash", Utility.getKeyHash(applicationContext))
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
