package com.xbyoung.fingerprint

/**
 * @author XBYoung
 */
interface CheckListener {
    /**
     * 验证成功
     */
    fun checkSuccess()

    /**
     * 验证失败
     */
    fun checkFailed(reMainCount: Int, errMsg: String)

    /**
     *
     */
}
