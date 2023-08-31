package ke.co.xently.shopping

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class Xently : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

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
                    if (priority in CRASHLYTICS_LOGGABLE_ERROR_TAGS) {
                        t?.also(Firebase.crashlytics::recordException)
                            ?: tag?.also {
                                Firebase.crashlytics.setCustomKey(it, message)
                            }
                            ?: Firebase.crashlytics.log(message)
                    }
                }
            }
        }

        Timber.plant(tree)
    }

    companion object {
        private val CRASHLYTICS_LOGGABLE_ERROR_TAGS = setOf(Log.ERROR, Log.ASSERT, Log.WARN)
    }
}