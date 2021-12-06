package com.jared.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

/**
 * Represents a single [SharedPreferences] file.
 */
// Ignore unused warning. This class needs to handle all data types, regardless of whether the method is used.
// Allow unchecked casts - we can blindly trust that data we read is the same type we saved it as..
@SuppressLint("CommitPrefEdits")
@Suppress("UNCHECKED_CAST", "unused")
abstract class Preferences(val context:Context) {

    companion object {
        const val DEFAULT_NAME="preferences"
    }


    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(getSharedPreferencesName(), Context.MODE_PRIVATE)
    }

    private val editor:SharedPreferences.Editor by lazy {
        prefs.edit()
    }

    private var transaction=false

    abstract class PrefDelegate<T>(val prefKey: String?) {
        abstract operator fun getValue(thisRef: Any?, property: KProperty<*>): T
        abstract operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
    }

    abstract fun getSharedPreferencesName(): String


    fun registerListener(sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(sharedPrefsListener)
    }

    fun removeListener(sharedPrefsListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(sharedPrefsListener)
    }

    enum class AcceptType {
        String,
        Int,
        Float,
        Boolean,
        Long,
        StringSet
    }

    fun transactionStart(){
        if(transaction){
           throw RuntimeException("transactionStart() can't repeated !!")
        }
        transaction=true
    }

    fun transactionEnd(){
        if(!transaction){
            throw  RuntimeException("Must call transactionStart() before transactionEnd() !! ")
        }
        transaction=false
    }

    private fun <T> getPreValue(thisRef: Any?, property: KProperty<*>, prefKey: String?, defaultValue: T, type: AcceptType)=
        when (type) {
            AcceptType.String ->prefs.getString(prefKey ?: property.name, defaultValue as String) as T
            AcceptType.Int ->prefs.getInt(prefKey ?: property.name, defaultValue as Int) as T
            AcceptType.Float ->prefs.getFloat(prefKey ?: property.name, defaultValue as Float) as T
            AcceptType.Boolean ->prefs.getBoolean(prefKey ?: property.name, defaultValue as Boolean) as T
            AcceptType.Long ->prefs.getLong(prefKey ?: property.name, defaultValue as Long) as T
            AcceptType.StringSet ->prefs.getStringSet(prefKey ?: property.name, defaultValue as Set<String>) as T
        }

    @SuppressLint("CommitPrefEdits")
    private fun <T> setPreValueBatch(thisRef: Any?, property: KProperty<*>, prefKey: String?, value: T, type: AcceptType) {
        when (type) {
            AcceptType.String ->editor.putString(prefKey ?: property.name, value as String)
            AcceptType.Int ->editor.putInt(prefKey ?: property.name, value as Int)
            AcceptType.Float ->editor.putFloat(prefKey ?: property.name, value as Float)
            AcceptType.Boolean ->editor.putBoolean(prefKey ?: property.name, value as Boolean)
            AcceptType.Long ->editor.putLong(prefKey ?: property.name, value as Long)
            AcceptType.StringSet ->editor.putStringSet(prefKey ?: property.name, value as Set<String>)
        }

    }

    fun apply() {
        if(transaction){
          throw  RuntimeException("Must call transactionEnd() before apply() !! ")
        }
      editor.apply()
    }

    fun commit() {
        if(transaction){
            throw  RuntimeException("Must call transactionEnd() before commit() !! ")
        }
        editor.commit()
    }

    fun clear() {
        editor.clear()
        editor.commit()
    }

     fun  removeKey(){

    }


    inner class GenericPrefDelegate<T>(prefKey: String? = null, private val defaultValue: T, val type: AcceptType) :
        PrefDelegate<T>(prefKey) {


        override fun getValue(thisRef: Any?, property: KProperty<*>): T=
                getPreValue(thisRef,property,prefKey,defaultValue,type)


        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T)=
                setPreValueBatch(thisRef,property,prefKey,value,type).also {
                   if(!transaction) commit()
               }
    }


    fun stringPref(prefKey: String? = null, defaultValue: String = String()) =
        GenericPrefDelegate(prefKey, defaultValue,
            AcceptType.String
        )

    fun intPref(prefKey: String? = null, defaultValue: Int = 0) =
        GenericPrefDelegate(prefKey, defaultValue,
            AcceptType.Int
        )

    fun floatPref(prefKey: String? = null, defaultValue: Float = 0f) =
        GenericPrefDelegate(prefKey, defaultValue,
            AcceptType.Float
        )

    fun booleanPref(prefKey: String? = null, defaultValue: Boolean = false) =
        GenericPrefDelegate(prefKey, defaultValue,
            AcceptType.Boolean
        )

    fun longPref(prefKey: String? = null, defaultValue: Long = 0L) =
        GenericPrefDelegate(prefKey, defaultValue,
            AcceptType.Long
        )

    fun stringSetPref(prefKey: String? = null, defaultValue: Set<String> = HashSet()) =
        GenericPrefDelegate(prefKey, defaultValue,
            AcceptType.StringSet
        )


}
