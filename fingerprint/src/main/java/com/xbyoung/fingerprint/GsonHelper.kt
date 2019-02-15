package com.xbyoung.fingerprint


import com.google.gson.Gson
import com.google.gson.JsonParser
import java.util.ArrayList

class GsonHelper private constructor() {
    private val mGson: Gson = Gson()

    /**
     * 解析json成集合返回
     * @param json
     * @param cls
     * @return T
     */
    fun <T> fromJSONArray(json: String, cls: Class<T>): ArrayList<T> {
        val mList = ArrayList<T>()
        val array = JsonParser().parse(json).asJsonArray
        for (elem in array) {
            mList.add(mGson.fromJson(elem, cls))
        }
        return mList
    }

    /**
     * 解析json成对象返回
     * @param json
     * @param t
     * @return T
     */
    fun <T> fromJSONObject(json: String, t: Class<T>): T? {
        return if (!json.isNullOrEmpty()) mGson.fromJson(json, t) else null
    }

    /**
     * 任何对象转换成json
     * @param mObject
     * @return String
     */
    fun <T> toJson(mObject: T): String {
        return mGson.toJson(mObject)
    }

    companion object {
        fun getInstance(): GsonHelper {
            return Inner.mInstance
        }
    }
     class Inner{
        companion object {
            val mInstance = GsonHelper()
        }
    }
}
