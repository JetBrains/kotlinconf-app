package org.jetbrains.kotlinconf.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import android.support.v4.content.ContextCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import org.jetbrains.kotlinconf.R
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

class MapboxMapFragment : SupportMapFragment(), PermissionsListener {

    private lateinit var permissionsManager: PermissionsManager
    private lateinit var mapFragment: SupportMapFragment
    private var mapboxMap: MapboxMap? = null
    private var amsterdamConferenceLocation = LatLng(52.375008, 4.896243)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapFragment = activity?.supportFragmentManager?.findFragmentByTag("MapboxMap") as SupportMapFragment
        mapFragment.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            mapboxMap.setStyleUrl("mapbox://styles/langsmith/cjnxtuknn0xg12sql21zj3r21")
            mapboxMap.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(amsterdamConferenceLocation.latitude,
                            amsterdamConferenceLocation.longitude))
                    .zoom(14.toDouble())
                    .build()
            enableLocationComponent()
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            val locationComponent = mapboxMap?.locationComponent
            locationComponent?.activateLocationComponent(activity!!.applicationContext)
            locationComponent?.isLocationComponentEnabled = true
            locationComponent?.cameraMode = CameraMode.NONE
            locationComponent?.renderMode = RenderMode.NORMAL
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(context, context?.getString(R.string.user_location_permission_not_granted), Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent()
        } else {
            Toast.makeText(context, context?.getString(R.string.user_location_permission_not_granted), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val TAG = "MapboxMap"
    }
}