
package com.agvahealthcare.ventilator_ext


import android.os.Bundle
import android.view.Window
import com.agvahealthcare.ventilator_ext.dashboard.BaseActivity


class ShutDownActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        hideSystemUI()
        setContentView(R.layout.activity_shut_down)
    }
}