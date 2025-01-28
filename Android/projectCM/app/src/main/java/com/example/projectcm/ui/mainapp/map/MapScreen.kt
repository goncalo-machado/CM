package com.example.projectcm.ui.mainapp.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val defaultLocation = GeoPoint(37.7749, -122.4194)
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    val mapViewRef = remember { mutableStateOf<MapView?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val provider = LocationManager.GPS_PROVIDER
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.getLastKnownLocation(provider)?.let { location ->
                    userLocation = GeoPoint(location.latitude, location.longitude)
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { context ->
            MapView(context).apply {
                setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                minZoomLevel = 5.0
                maxZoomLevel = 20.0
                controller.setZoom(15.0)
                controller.setCenter(userLocation ?: defaultLocation)
                mapViewRef.value = this
            }
        }, modifier = Modifier.fillMaxSize(), update = { mapView ->
            mapView.controller.setCenter(userLocation ?: defaultLocation)
        })

        // Coordinates display in bottom-right corner
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(
                text = if (userLocation != null) {
                    "Latitude: %.6f\nLongitude: %.6f".format(
                        userLocation!!.latitude, userLocation!!.longitude
                    )
                } else {
                    "Latitude: %.6f\nLongitude: %.6f".format(
                        defaultLocation.latitude, defaultLocation.longitude
                    )
                }, style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
            )
        }

        // Button to center the map in bottom-left corner
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Button(onClick = {
                userLocation?.let { location ->
                    mapViewRef.value?.controller?.animateTo(location)
                } ?: Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()

            }) {
                Text(text = "Center on Me")
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            // Zoom In Button with Background
            Box(
                modifier = Modifier
                    .background(
                        Color.White, shape = CircleShape
                    ) // Background for the button
                    .padding(8.dp) // Padding inside the button
            ) {
                IconButton(
                    onClick = {
                        mapViewRef.value?.controller?.zoomIn()
                    }, modifier = Modifier.size(35.dp) // Button size
                ) {
                    Icon(
                        imageVector = Icons.Filled.ZoomIn,
                        contentDescription = "Zoom In",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
            Spacer(Modifier.size(8.dp))

            // Zoom Out Button with Background
            Box(
                modifier = Modifier
                    .background(
                        Color.White, shape = CircleShape
                    ) // Background for the button
                    .padding(8.dp) // Padding inside the button
            ) {
                IconButton(
                    onClick = {
                        mapViewRef.value?.controller?.zoomOut()
                    }, modifier = Modifier.size(35.dp) // Button size
                ) {
                    Icon(
                        imageVector = Icons.Filled.ZoomOut,
                        contentDescription = "Zoom Out",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }
}



