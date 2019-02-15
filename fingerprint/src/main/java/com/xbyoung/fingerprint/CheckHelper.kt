package com.xbyoung.fingerprint

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog

/**
 * 开启验证
 */
class CheckHelper private constructor() : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
    private var checkListener: CheckListener? = null
    /**
     * 输入指纹或pin的dialog
     */
    private var fingerDialog: MaterialDialog? = null
    private object Inner {
        internal val helper = CheckHelper()
    }


    /**
     * 选择应该弹出的选择框
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun startCheck(activity: Activity, checkListener: CheckListener) {
        verifyTime = VerifyTime.instance()
        //清空，防止多重回调
        verifyTime!!.clear()
        this.checkListener = checkListener
        verifyTime!!.setVerifyListener(checkListener)
        SoftKeyBoardListener.setListener(activity, this)
        val checkCode = FingerprintUtils.instance().isFingerFunNormal
        if (checkCode == FingerprintUtils.FINGER_FUN_NORMAL) {
            showFingerInputDialog(activity)
        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    fun onPause(activity: Activity) {
        if (null != fingerDialog && fingerDialog!!.isShowing) {
            fingerDialog!!.dismiss()
            this@CheckHelper.checkListener!!.checkFailed(CHECK_CANCEL, "取消验证")
            FingerprintUtils.instance().cancelListening()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun showFingerInputDialog(activity: Activity) {
        val view = LayoutInflater.from(activity).inflate(R.layout.layout_select_pin_finger_dialog, null, false)
        val tv_remian = view.findViewById<View>(R.id.tv_remian) as TextView
        tv_remian.visibility = View.INVISIBLE
        fingerDialog = MaterialDialog.Builder(activity).cancelable(false).customView(view, false).build()
        view.findViewById<View>(R.id.tv_cancel).setOnClickListener {
            this@CheckHelper.checkListener!!.checkFailed(CHECK_CANCEL, "取消验证")
            FingerprintUtils.instance().cancelListening()
            fingerDialog!!.dismiss()
        }
        FingerprintUtils.instance().startListening(activity, object : VerifyListener() {
            override fun lock( isLock: Boolean, errorCount: Int) {
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode==5){
                    verifyTime?.verifyCancel()
                }else{
                    verifyTime?.verifyFail()
                }
            }

            override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                verifyTime!!.verifySuccess()
                this@CheckHelper.checkListener!!.checkSuccess()
                fingerDialog!!.dismiss()
            }

            override fun onAuthenticationFailed(remian_finger: Int, remian_pin: Int) {
                super.onAuthenticationFailed(remian_finger, remian_pin)
                Log.d("XBYoung", "onAuthenticationFailed    ")
                verifyTime?.verifyFail()
            }

            override fun onAuthenticationFailed() {
                Log.d("XBYoung", "onAuthenticationFailed    ")
                verifyTime?.verifyFail()
                super.onAuthenticationFailed()
            }
        })
        fingerDialog!!.show()
    }


    override fun keyBoardShow(height: Int) {}

    override fun keyBoardHide(height: Int) {

    }

    companion object {
        val CHECK_FAILED = 400
        val CHECK_CANCEL = -1
        private var verifyTime: VerifyTime? = null
        private var application: Application? = null
        fun init(mApplication: Application) {
            application = mApplication
            VerifyTime.init(application!!)
            FingerprintUtils.init(application!!)

        }

        fun instance(): CheckHelper {
            return Inner.helper
        }
    }
}
