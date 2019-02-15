package com.xbyoung.fingerprint


import android.app.Application
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.Expose

/**
 * 单例
 * 错误次数 -> 用于试错次数限制
 * 被锁定时间 -> 提供锁定接口
 */
class VerifyTime// private static long LOCK_TIME = 10*1000;
private constructor() {
    //错误次数
     var errorCount = 0
    //被锁定时间
    private var lockTime: Long = 0
    @Expose
    private var checkListener: CheckListener? = null



    object Inner {
        @RequiresApi(Build.VERSION_CODES.M)
        val verifyTimeString = GlightCacheHelper.getInstance().getString(application.baseContext, key, "")
        @RequiresApi(Build.VERSION_CODES.M)
        val verifyTime: () -> VerifyTime = {
            Log.d("XBYoung",verifyTimeString)
            if (verifyTimeString.isNullOrEmpty()) {
                VerifyTime()
            } else {
                Gson().fromJson(GlightCacheHelper.getInstance().getString(application.baseContext, key, ""), VerifyTime::class.java)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun setVerifyListener(verifyListener: CheckListener) {
        this.checkListener = verifyListener
    }

    fun clear(){
        this.checkListener = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun verifySuccess() {
        reset()
    }

    /**
     * 验证失败
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun verifyFail(){
        errorCount+=1
        saveToSp()
        checkListener?.checkFailed(errorCount,"")
    }

    fun verifyCancel(){
        checkListener?.checkFailed(CheckHelper.CHECK_CANCEL,"取消验证")
    }


    /**
     * 重置
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun reset() {
        lockTime = 0
        errorCount = 0
        saveToSp()
    }


    /**
     * 锁定用户
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun lockUser(lockTime: Long) {
        this.lockTime = lockTime
        saveToSp()
    }



    /**
     * 保存到sp,每次保存检查是否锁定
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun saveToSp() {
        GlightCacheHelper.getInstance().setString(application.baseContext, key, toJson())
    }

    private fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        private lateinit var application: Application
        @JvmStatic
        fun init(app: Application) {
            application = app
        }

        /**
         * 登录后初始化
         *
         * @return
         */
        @JvmStatic
        @RequiresApi(Build.VERSION_CODES.M)
        fun instance(): VerifyTime {
            val string = GlightCacheHelper.getInstance().getString(application.baseContext, key, "")
            return if ("" == string) {
                VerifyTime()
            } else {
                Inner.verifyTime().apply {
                    errorCount = 0
                }
            }
        }
        private val key = "fingerVerify"
    }

}
