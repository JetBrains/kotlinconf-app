package org.jetbrains.kotlinconf.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.kotlinconf.R

/**
 * Fragment class that handles the display of a Mapbox map via the Mapbox Maps SDK for Android.
 * This class extends the Maps SDK's SupportMapFragment and implements the Mapbox Core Library's
 * PermissionsListener interface.
 *
 * More info at https://www.mapbox.com/android-docs/maps/overview and https://docs.mapbox.com/android/core/overview/
 */
class MapboxMapFragment : SupportMapFragment(), PermissionsListener, MapboxMap.OnMapClickListener {

    private lateinit var permissionsManager: PermissionsManager
    private lateinit var mapFragment: SupportMapFragment
    private var mapboxMap: MapboxMap? = null
    private val copenhagenConferenceLocation = LatLng(55.637626, 12.579898)

    companion object {
        const val TAG = "MapboxMap"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapFragment = activity?.supportFragmentManager?.findFragmentByTag(TAG) as SupportMapFragment
        mapFragment.getMapAsync { mapboxMap ->

            // Set the map style. If you want to set a custom map style, replace
            // Style.MAPBOX_STREETS with Style.Builder().fromUrl("customStyleUrl")
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {

                // Map is set up and the style has loaded. Now we can add data
                // and/or make other map adjustments.
                this.mapboxMap = mapboxMap

                // Move the map camera to a position where the KotlinConf location icon
                // and downtown Copenhagen are both visible.
                mapboxMap.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(copenhagenConferenceLocation.latitude,
                                copenhagenConferenceLocation.longitude))
                        .zoom(15.116720)
                        .build()

                // Add the conference icon to the map
                it.addImage("conference-icon-id", BitmapFactory.decodeResource(resources, R.drawable.kotlin_conf_2019_icon))

                // Add source for the conference icon
                it.addSource(GeoJsonSource("conference-icon-source-id",
                        Feature.fromGeometry(Point.fromLngLat(
                                copenhagenConferenceLocation.longitude,
                                copenhagenConferenceLocation.latitude
                        ))))

                // Add SymbolLayer for the conference icon
                val conferenceIconSymbolLayer = SymbolLayer("conference-icon-layer-id",
                        "conference-icon-source-id")
                        .withProperties(
                                iconImage("conference-icon-id"),
                                iconSize(1f),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                        )
                conferenceIconSymbolLayer.maxZoom = 14.3f
                it.addLayer(conferenceIconSymbolLayer)

                // Start the process of showing the device location icon
                enableLocationComponent(it)

                // Add map click listener interface
                mapboxMap.addOnMapClickListener(this@MapboxMapFragment)
            }
        }
    }

    /**
     * Move the camera to the conference site if the conference icon is tapped on
     */
    override fun onMapClick(point: LatLng): Boolean {
        // Use the map click point to query whether the conference icon was tapped on
        val conferenceIconLayerFeatureList = mapboxMap?.queryRenderedFeatures(
                mapboxMap!!.projection.toScreenLocation(point),
                "conference-icon-layer-id")

        // Animate the camera to zoom into the conference location if the conference icon is tapped on
        if (conferenceIconLayerFeatureList != null && conferenceIconLayerFeatureList.size > 0) {
            mapboxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                    .target(LatLng(copenhagenConferenceLocation.latitude,
                                            copenhagenConferenceLocation.longitude))
                                    .zoom(15.6)
                                    .build()
                    ), 2000)
        }
        return true
    }

    /**
     * Enable the Maps SDK's LocationComponent to display the device's location the map
     */
    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {

        // Check whether the location permission has been granted
        if (PermissionsManager.areLocationPermissionsGranted(context)) {

            // Activate and set the preferences for the Maps SDK's LocationComponent
            mapboxMap?.locationComponent?.apply {
                activateLocationComponent(activity!!.applicationContext, loadedMapStyle)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.NONE
                renderMode = RenderMode.NORMAL
            }

        } else {
            // Use the Mapbox Core Library to request location permissions
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    /**
     * Handle the codes associated with requesting the device's location
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Provide an explanation if/when needed to explain why the app is requesting the
     * location permission
     */
    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        toast(R.string.user_location_permission_not_granted)
    }

    /**
     * Handle the results of the location permission request
     */
    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            // Now that user has granted location permissions, once again
            // start the process of showing the device location icon
            enableLocationComponent(mapboxMap!!.style!!)
        } else {
            toast(R.string.user_location_permission_not_granted)
        }
    }
}