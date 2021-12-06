package com.jared.preferences

import android.content.Context
import com.jared.preferences.Preferences


class VersionPreferences(context: Context): Preferences(context){

    var currentVersion by intPref(defaultValue = 0)

    override fun getSharedPreferencesName()="version"

}