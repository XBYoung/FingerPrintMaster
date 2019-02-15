package com.xbyoung.fingerprintmaster

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.xbyoung.fingerprint.CheckHelper
import com.xbyoung.fingerprint.CheckListener
import com.xbyoung.fingerprint.FingerprintUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        finger.setOnClickListener {
            CheckHelper.instance().startCheck(this,object :CheckListener{
                override fun checkSuccess() {
                Toast.makeText(this@MainActivity,"Check Success",Toast.LENGTH_SHORT).show()
                }
                override fun checkFailed(errorCount: Int, errMsg: String) {
                    if (errorCount != CheckHelper.CHECK_CANCEL){
                        Toast.makeText(this@MainActivity,"Check Fail $errorCount $errMsg",Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}
