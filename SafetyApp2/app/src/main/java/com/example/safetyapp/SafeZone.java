package com.example.safetyapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SafeZone extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_zone);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        googleMap.setMyLocationEnabled(true);

        // Get the user's current location and move the camera to it
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(currentLocation).title("My Location");
            googleMap.addMarker(markerOptions);

            // Update the camera position to zoom in on the marker
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(10).build();
//            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            Places.initialize(getApplicationContext(),"AIzaSyCCpUAdKjs8ocEL0tMcZBXCWP4WQyYkSGI");

// Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);

// Perform a nearby search for police stations.
            String query = "police station";
            int radius = 5000; // in meters

            double radiusInMeters = 1000; // 1km radius
            LatLngBounds searchBounds = toBounds(currentLocation, radiusInMeters);

// Perform the search
            PlacesClient placesClient = Places.createClient(this);
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setOrigin(currentLocation)
                    .setLocationBias(RectangularBounds.newInstance(searchBounds))
                    .setQuery("police station")
                    .build();
            placesClient = Places.createClient(this);
            PlacesClient finalPlacesClient = placesClient;
            placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    String placeId = prediction.getPlaceId();
                    List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
                    FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, fields).build();
                    finalPlacesClient.fetchPlace(placeRequest).addOnSuccessListener(place -> {
                        String placeName = place.getPlace().getName();
                        LatLng placeLatLng = place.getPlace().getLatLng();
                        MarkerOptions markerOptions1 = new MarkerOptions().position(Objects.requireNonNull(placeLatLng)).title(placeName);
                        googleMap.addMarker(markerOptions1);
                    });
                }
            });


        }
    }
    private LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225);
        LatLng northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }




    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}