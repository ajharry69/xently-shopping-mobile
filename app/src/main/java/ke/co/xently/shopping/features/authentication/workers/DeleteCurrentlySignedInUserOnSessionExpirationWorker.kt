package ke.co.xently.shopping.features.authentication.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ke.co.xently.shopping.features.authentication.datasources.AuthenticationDataSource
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.time.Instant
import javax.inject.Named


@HiltWorker
class DeleteCurrentlySignedInUserOnSessionExpirationWorker @AssistedInject constructor(
    @Assisted
    appContext: Context,
    @Assisted
    workerParams: WorkerParameters,
    @Named("localAuthenticationDataSource")
    private val authenticationDataSource: AuthenticationDataSource,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        try {
            authenticationDataSource.getCurrentlySignedInUser().collectLatest {
                if (it == null) {
                    Timber.tag(TAG)
                        .i("User signed out...")
                } else {
                    val durationSinceAddition = Instant.now().epochSecond - it.dateAddedEpochSecond
                    Timber.tag(TAG)
                        .i("Checking currently signed in user's session is expired...")
                    if (durationSinceAddition > it.expiry) {
                        Timber.tag(TAG)
                            .i("Session expired, deleting currently signed in user. Time difference: $durationSinceAddition seconds; Session expiry: ${it.expiry} seconds")
                        authenticationDataSource.deleteCurrentlySignedInUserOnSessionExpiration()
                    }
                }
            }
        } catch (ex: Exception) {
            return Result.failure()
        }
        return Result.success()
    }

    companion object {
        val TAG: String =
            DeleteCurrentlySignedInUserOnSessionExpirationWorker::class.java.simpleName
    }
}

