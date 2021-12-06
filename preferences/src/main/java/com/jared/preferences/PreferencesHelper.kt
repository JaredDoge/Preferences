package com.jared.preferences

import android.content.Context
import android.util.Log

abstract class PreferencesHelper(val context: Context) {

    companion object {
        private const val TAG="PreferencesHelper"
    }


    private val preferences=
        VersionPreferences(context)



    @Synchronized
     fun checkVersion(newVersion: Int) {

        if (newVersion < 1) {
            // negative versions are illegal.
            // 0 is reserved to detect the initial state
            throw IllegalArgumentException("Version must be >= 1, was $newVersion")
        }

        val current=preferences.currentVersion
        if (newVersion!=current) {
            when {
                current == 0 -> {
                    Log.v(TAG,"Preference create with initial version $newVersion")
                    onCreate(newVersion)
                }
                current > newVersion -> {
                    Log.v(TAG,"Preference onDowngrade from $newVersion to $current")
                    onDowngrade(current, newVersion)
                }
                current < newVersion -> {
                    Log.v(TAG,"Preference onUpgrade from $current to $newVersion")
                    onUpgrade(current,newVersion)
                }
            }
            preferences.currentVersion=newVersion
        }
    }


    /**
     * Called when this Preference is created for the first time. This is where the initial
     * migration from other data source should happen.
     *
     * @param initialVersion the version set in the constructor, always &gt; 0
     * @see .onUpgrade
     * @see .onDowngrade
     */
    abstract fun onCreate(initialVersion: Int)

    /**
     * works inverse to the [.onUpgrade] method
     *
     * @param oldVersion version before downgrade
     * @param newVersion version to downgrade to, always &gt; 0
     * @see .onCreate
     * @see .onUpgrade
     */
    open fun onDowngrade(oldVersion: Int, newVersion: Int) {
        throw IllegalStateException("Can't downgrade " + this + " from version " +
                oldVersion + " to " + newVersion)
    }

    /**
     * Called when the Preference needs to be upgraded. Use this to migrate data in this Preference
     * over time.
     *
     *
     * Once the version in the constructor is increased the next constructor call to this
     * Preference
     * will trigger an upgrade.
     *
     * @param oldVersion version before upgrade, always &gt; 0
     * @param newVersion version after upgrade
     * @see .onCreate
     * @see .onDowngrade
     */
    abstract  fun onUpgrade(oldVersion: Int, newVersion: Int)



}