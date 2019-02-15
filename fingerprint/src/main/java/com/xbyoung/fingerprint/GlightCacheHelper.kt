package com.xbyoung.fingerprint

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


import java.util.ArrayList


class GlightCacheHelper private constructor() {

    /**
     * 往集合里面插入一条数据
     *
     * @param mContext
     * @param insertValue
     * @param key
     */
    fun insetData(mContext: Context, insertValue: String, key: String) {
        val historyResult = queryArrayList(mContext, key)
        val result = ArrayList<String>()
        result.add(insertValue)
        result.addAll(historyResult)
        setString(mContext, key, GsonHelper.getInstance().toJson(result))
    }

    /**
     * 集合里面删除一条数据
     *
     * @param mContext
     * @param deletValue
     * @param key
     */
    fun deleteData(mContext: Context, deletValue: String, key: String) {
        var result: ArrayList<String>? = ArrayList()
        result = queryArrayList(mContext, key)
        if (result != null) {
            for (i in result.indices) {
                if (result[i] == deletValue) {
                    result.removeAt(i)
                    break
                }
            }
        }
        if (result == null || result.size == 0) {
            setString(mContext, key, null)
        } else {
            setString(mContext, key, GsonHelper.getInstance().toJson(result))
        }
    }

    /**
     * 清除指定键的缓存数据
     *
     * @param mContext
     * @param key
     */
    fun clearData(mContext: Context, key: String) {
        setString(mContext, key, null)
    }

    /**
     * 查询缓存的集合数据
     *
     * @param mContext
     * @param key
     * @return
     */
    fun queryArrayList(mContext: Context, key: String): ArrayList<String> {

        var result = ArrayList<String>()

        val searchString = getString(mContext, key, null)
        if (searchString != null) {
            result = GsonHelper.getInstance().fromJSONArray(searchString, String::class.java)
        }

        return result
    }

    /**
     * 获取指定键的String类型值
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    fun getString(context: Context, key: String,
                  defaultValue: String?): String? {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(context)
        return settings.getString(key, defaultValue)
    }

    /**
     * 设置指定键的缓存String数据
     *
     * @param context
     * @param key
     * @param value
     */
    fun setString(context: Context, key: String,
                  value: String?) {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(context)
        settings.edit().putString(key, value).commit()
    }

    /**
     * 获取指定键的T泛型对象
     *
     * @param context
     * @param key
     * @param cls
     * @return
     */
    fun <T> fromJSON(context: Context, key: String,
                     cls: Class<T>): T? {
        val result = getString(context, key, null)
        return if (result.isNullOrEmpty()) null else GsonHelper.getInstance().fromJSONObject<T>(result, cls)
    }


    /**
     * 获取指定键的T泛型对象集合
     *
     * @param context
     * @param key
     * @param cls
     * @return
     */
    fun <T> fromJSONArray(context: Context, key: String,
                          cls: Class<T>): ArrayList<T> {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(context)
        val result = settings.getString(key, "")
        return GsonHelper.getInstance().fromJSONArray(result, cls)
    }

    /**
     * 获取指定键的boolean类型值
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    fun getBoolean(context: Context, key: String,
                   defaultValue: Boolean): Boolean {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(context)
        return settings.getBoolean(key, defaultValue)
    }

    /**
     * 设置指定键的缓存boolean数据
     *
     * @param context
     * @param key
     * @param value
     */
    fun setBoolean(context: Context, key: String,
                   value: Boolean?) {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(context)
        settings.edit().putBoolean(key, value!!).commit()
    }

    /**
     * 获取指定键的Int类型值
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    fun getInt(context: Context, key: String,
               defaultValue: Int): Int {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(context)
        return settings.getInt(key, defaultValue)
    }

    /**
     * 设置指定键的缓存Int数据
     *
     * @param context
     * @param key
     * @param value
     */
    fun setInt(context: Context, key: String,
               value: Int) {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(context)
        settings.edit().putInt(key, value).commit()
    }

    companion object {
        @JvmStatic
        fun getInstance(): GlightCacheHelper {
            return Inner.instance
        }
    }


    class Inner {
        companion object {
            val instance = GlightCacheHelper()
        }
    }
}
