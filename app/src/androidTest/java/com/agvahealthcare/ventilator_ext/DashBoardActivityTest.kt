package com.agvahealthcare.ventilator_ext

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import com.agvahealthcare.ventilator_ext.dashboard.DashBoardActivity
import com.agvahealthcare.ventilator_ext.manager.PreferenceManager
import com.agvahealthcare.ventilator_ext.utility.utils.Configs
import org.junit.Before
import org.junit.Test

class DashBoardActivityTest {
    private lateinit var scenario0:ActivityScenario<SplashActivity>
    private lateinit var scenario1:ActivityScenario<MainActivity>
    private lateinit var scenario2:ActivityScenario<DashBoardActivity>
    private lateinit var  preferenceManager: PreferenceManager
    private val context by lazy {
        InstrumentationRegistry.getInstrumentation().getTargetContext()
    }
    @Before
    fun setup(){
        scenario0 = launchActivity()
        scenario0.moveToState(Lifecycle.State.STARTED)
        scenario1= launchActivity()
        scenario1.moveToState(Lifecycle.State.STARTED)
        preferenceManager=PreferenceManager(context)
        preferenceManager.setVentilationMode(12)
        scenario2= launchActivity()


        val something= Configs.MessageFactory.getAckMessage(context,"ACK_0324")
        println(something)
    }

    @Test
    fun checkRightAndLeftClick(){
        Espresso.onView(ViewMatchers.withId(R.id.button_logs)).perform(ViewActions.click())
    }
}
