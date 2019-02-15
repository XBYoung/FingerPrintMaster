package com.xbyoung.fingerprintmaster

import android.app.Application
import com.xbyoung.fingerprint.CheckHelper

/**
Young.Lew
2019/2/14 17:16
com.xbyoung.fingerprintmaster
 */
class FingerApplication : Application (){
    override fun onCreate() {
        super.onCreate()
        CheckHelper.init(this)
    }
}