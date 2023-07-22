package ke.co.xently

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Xently : Application() {
    override fun onCreate() {
        super.onCreate()
        val tree = if (BuildConfig.DEBUG) {
            object : Timber.DebugTree() {
                /*override fun createStackElementTag(element: StackTraceElement): String {
                    return "Class:${super.createStackElementTag(element)}: Line: ${element.lineNumber}, Method: ${element.methodName}"
                }*/
            }
        } else {
            object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority == Log.ERROR || priority == Log.WARN) {
                        /*t?.also(Firebase.crashlytics::recordException)
                            ?: tag?.also {
                                Firebase.crashlytics.setCustomKey(it, message)
                            }
                            ?: Firebase.crashlytics.log(message)*/
                        TODO("Send to crashlytics")
                    }
                }
            }
        }

        Timber.plant(tree)
    }
}