package ar.edu.utn.frba.inventario.api.repository

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/*
@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun locationUpdatesFlow(intervalMillis: Long = 5000L): Flow<Location> = callbackFlow {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, intervalMillis
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    trySend(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        // Cancelación segura del flujo
        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }.flowOn(Dispatchers.IO)
}*/
