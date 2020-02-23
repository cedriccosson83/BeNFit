package isen.CedricLucieFlorent.benfit

import android.app.Application
import android.content.Context

class ApplicationContext : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: ApplicationContext? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        val context: Context = applicationContext()
    }
}