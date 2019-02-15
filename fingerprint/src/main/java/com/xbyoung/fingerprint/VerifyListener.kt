package com.xbyoung.fingerprint

import android.hardware.fingerprint.FingerprintManager
import android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
import android.os.Build
import android.support.annotation.RequiresApi

@RequiresApi(api = Build.VERSION_CODES.M)
abstract class VerifyListener : AuthenticationCallback() {
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        super.onAuthenticationError(errorCode, errString)
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
        super.onAuthenticationHelp(helpCode, helpString)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        super.onAuthenticationSucceeded(result)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    open fun onAuthenticationFailed(remian_finger: Int, remian_pin: Int) {
        super.onAuthenticationFailed()
    }

    /**
     * 用户是否锁定
     * @param isLock
     * 错误次数
     * @param errorCount
     */
    abstract fun lock(isLock: Boolean, errorCount: Int)
}
