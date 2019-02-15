package com.xbyoung.fingerprint.constact


object Constact {
    val KEY_USER = "user"
    val TAG = "FINGERINPUT"
    val VERIFY_TIME = "verify_time"
    var isFingerCodeOpen: Boolean = false
    var isPinCodeSet: Boolean = false
    val CHECK_PINCODE_REQUEST_CODE = 5
    val CHECK_PINCODE_RESULT_SUCCESS = 5
    val CHECK_PINCODE_RESULT_FAIL = 6
    var errorCount = 5
}
