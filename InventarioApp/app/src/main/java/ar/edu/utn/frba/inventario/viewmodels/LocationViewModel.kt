package ar.edu.utn.frba.inventario.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import ar.edu.utn.frba.inventario.api.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(private val locationRepository: LocationRepository) :
    ViewModel() {
    val location: StateFlow<Location?> = locationRepository.location

    private val _locationPermissionGranted = MutableStateFlow(false)
    var locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted

    fun setLocationPermissionGranted(flag: Boolean) {
        _locationPermissionGranted.value = flag
    }

    fun startLocationUpdates() {
        locationRepository.startLocationUpdates()
    }

    override fun onCleared() {
        super.onCleared()
        locationRepository.stopLocationUpdates()
    }

    fun hasLocationPermission(context: Context): Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED

    suspend fun getAddressFromLocation(
        context: Context,
        latitude: Double,
        longitude: Double,
    ): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
