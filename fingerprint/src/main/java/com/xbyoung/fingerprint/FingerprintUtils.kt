package com.xbyoung.fingerprint

import android.Manifest
import android.app.Application
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import com.xbyoung.fingerprint.constact.Constact


class FingerprintUtils @RequiresApi(api = Build.VERSION_CODES.M)
private constructor() {
    internal var manager: FingerprintManager
    private val mKeyManager: KeyguardManager
    private var mCancellationSignal: CancellationSignal? = null
    private val currentapiVersion: Int
    private var currentStatu = FINGER_FUN_NORMAL
    private val verifyTime: VerifyTime
    var notices = arrayOf("正常使用", "您的手机系统版本不支持指纹识别功能", "您的手机不支持指纹识别功能", "应用没有指纹识别权限,请手动开启", "手机没有开启锁屏密码,请前往系统设置界面开启", "手机未录入指纹,请前往系统设置界面录入")
    /**
     * 判断指纹功能是否能正常使用
     * @return
     */
    val isFingerFunNormal: Int
        @RequiresApi(api = Build.VERSION_CODES.M)
        get() {
            if (currentapiVersion < Build.VERSION_CODES.M) {
                Log.d(Constact.TAG, "版本号 < 23")
                currentStatu = BUILDVERSION_TOO_SMALL
            }

            if (!manager.isHardwareDetected) {
                Log.d(Constact.TAG, "版本号 >= 23")
                currentStatu = CELLPHONE_NOT_SUCCESS
            }

            if (ActivityCompat.checkSelfPermission(application!!, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Log.d(Constact.TAG, "有指纹模块")
                currentStatu = WITHOUT_PERMISSION
            }

            if (!mKeyManager.isKeyguardSecure) {
                Log.d(Constact.TAG, "有指纹权限")
                currentStatu = HAVE_NOT_OPEN_FUNC
            }
            if (!manager.hasEnrolledFingerprints()) {
                Log.d(Constact.TAG, "已开启锁屏密码")
                currentStatu = HAVE_NOT_INPUT_FINGER
            }
            if (currentStatu == FINGER_FUN_NORMAL) {
                Log.d(Constact.TAG, "已录入指纹")
            } else {
                Toast.makeText(application, notices[currentStatu], Toast.LENGTH_SHORT).show()
            }
            return currentStatu
        }

    init {
        manager = application!!.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        mCancellationSignal = CancellationSignal()
        mKeyManager = application!!.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        currentapiVersion = android.os.Build.VERSION.SDK_INT
        verifyTime = VerifyTime.instance()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private object Inner {
        internal val utils = FingerprintUtils()
    }

    /**
     * 取消监听
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun cancelListening() {
        mCancellationSignal!!.cancel()
    }

    /**
     * 开始监听指纹
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun startListening(context: Context, verifyListener: VerifyListener) {
        mCancellationSignal = CancellationSignal()
        if (isFingerFunNormal == FINGER_FUN_NORMAL) {
            manager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            manager.authenticate(null, mCancellationSignal, 0, setListener(verifyListener), null)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setListener(verifyListener: VerifyListener): FingerprintManager.AuthenticationCallback {
        return object : FingerprintManager.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                //指纹验证失败，不可再验
                verifyListener.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
                super.onAuthenticationHelp(helpCode, helpString)
                //指纹验证失败，可再验，可能手指过脏，或者移动过快等原因。
                verifyListener.onAuthenticationHelp(helpCode, helpString)
            }

            override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                //指纹验证成功
                verifyListener.onAuthenticationSucceeded(result)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                verifyListener.onAuthenticationFailed(verifyTime.errorCount, 0)
                //指纹验证失败，指纹识别失败，可再验，该指纹不是系统录入的指纹。
            }
        }
    }

    companion object {
        val BUILDVERSION_TOO_SMALL = 1
        val CELLPHONE_NOT_SUCCESS = 2
        val WITHOUT_PERMISSION = 3
        val HAVE_NOT_OPEN_FUNC = 4
        val HAVE_NOT_INPUT_FINGER = 5
        val FINGER_FUN_NORMAL = 0
        private var application: Application? = null

        fun init(mApplication: Application) {
            application = mApplication
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        fun instance(): FingerprintUtils {
            return Inner.utils
        }
    }


}
