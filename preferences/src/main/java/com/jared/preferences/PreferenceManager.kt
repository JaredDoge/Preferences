package com.jared.preferences

import android.content.Context


class  PreferenceManager(context:Context): PreferencesHelper(context){

    companion object{
        private const val VERSION=1
    }


    fun checkVersion()=checkVersion(VERSION)


    override fun onCreate(initialVersion: Int) {

    }

    override fun onUpgrade(oldVersion: Int, newVersion: Int) {


    }

}