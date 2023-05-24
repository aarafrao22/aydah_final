package com.example.otlcse;

import static java.lang.Float.parseFloat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;

public class googlemap extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, LocationListener {

    private GoogleMap mMap;
    private Button btnFindPath;
    private LinearLayout btnroute1, btnroute2;
    Marker mCurrLocationMarker;
    private TextView etOrigin;
    private TextView etDestination;
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/geocode/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyBw8BeBoVk25ZgZY5w2WyXNd4MLTX7Of3Y";
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;


    Location mLastLocation;
    String latitude, longitude;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    List<Route> routeList = new ArrayList<>();
    LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        btnroute1 = (LinearLayout) findViewById(R.id.btnroute1);
        btnroute2 = (LinearLayout) findViewById(R.id.btnroute2);
        etOrigin = (TextView) findViewById(R.id.etOrigin);
        etDestination = (TextView) findViewById(R.id.etDestination);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
        getnotification();

//        Current Location


        btnroute1.setOnClickListener(v -> {
            if (routeList.size() > 0) {
                int count = 0;
                if (polylinePaths != null) {
                    for (Polyline polyline : polylinePaths) {
                        polyline.remove();
                    }
                }
                for (Route route : routeList) {
                    if (count == 0) {

                        PolylineOptions polylineOptions = new PolylineOptions().
                                geodesic(true).
                                color(Color.BLUE).
                                width(10);

                        for (int i = 0; i < route.points.size(); i++) {
                            polylineOptions.add(route.points.get(i));

                        }
                        polylinePaths.add(mMap.addPolyline(polylineOptions));

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng latLng : polylineOptions.getPoints()) {
                            builder.include(latLng);
                        }
                        LatLngBounds bounds = builder.build();
                        int padding = 50; // Padding around the polyline in pixels
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cameraUpdate);

                    }
                    count += 1;

                }


            } else {
                Toast.makeText(googlemap.this, "Please Select Source and destination to find route", Toast.LENGTH_SHORT);

            }
        });

        btnroute2.setOnClickListener(v -> {
            if (routeList.size() > 0) {
                int count = 0;
                if (polylinePaths != null) {
                    for (Polyline polyline : polylinePaths) {
                        polyline.remove();
                    }
                }
                for (Route route : routeList) {
                    if (count == 1) {

                        PolylineOptions polylineOptions = new PolylineOptions().
                                geodesic(true).
                                color(Color.BLUE).
                                width(10);

                        for (int i = 0; i < route.points.size(); i++) {
                            polylineOptions.add(route.points.get(i));

                        }
                        polylinePaths.add(mMap.addPolyline(polylineOptions));


                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng latLng : polylineOptions.getPoints()) {
                            builder.include(latLng);
                        }
                        LatLngBounds bounds = builder.build();
                        int padding = 50; // Padding around the polyline in pixels
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cameraUpdate);
                    }
                    count += 1;
                }
            } else {
                Toast.makeText(googlemap.this, "Please Select Source and destination to find route", Toast.LENGTH_SHORT);

            }
        });


        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeList.clear();
//                ((TextView) findViewById(R.id.tvDuration)).setText("0 min");
                ((TextView) findViewById(R.id.tvDistance)).setText("O km");

//                ((TextView) findViewById(R.id.tvDuration1)).setText("0 min");
                ((TextView) findViewById(R.id.tvDistance1)).setText("O km");

//                etOrigin.setText("Source Address:  Waiting for Source Address");
                etDestination.setText("Destination Address:  Wait for Destination Address");
                if (originMarkers != null) {
                    for (Marker marker : originMarkers) {
                        marker.remove();
                    }
                }

                if (destinationMarkers != null) {
                    for (Marker marker : destinationMarkers) {
                        marker.remove();
                    }
                }

                if (polylinePaths != null) {
                    for (Polyline polyline : polylinePaths) {
                        polyline.remove();
                    }
                }
            }
        });

    }

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        btnroute1.setVisibility(View.VISIBLE);
        btnroute2.setVisibility(View.VISIBLE);
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }
        origin = latitude + "," + longitude;
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getnotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(googlemap.this, "My Notification").setSmallIcon(R.drawable.logo)
                        .setContentTitle("Location")
                        .setContentText("You reached a Destination");
        builder.setAutoCancel(true);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());


    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                double Lat = latLng.latitude;
                double lng = latLng.longitude;

                String link = DIRECTION_URL_API + "lat=" + Lat + "&lng=" + lng + "&key=" + GOOGLE_API_KEY;

                Geocoder geocoder = new Geocoder(googlemap.this, Locale.getDefault());
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocation(Lat, lng, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (routeList.size() == 0) {
                    String maddress = address.get(0).getAddressLine(0);
                    System.out.println(maddress);
                    if (etOrigin.getText().toString().trim().equals("Source Address:  Waiting for Source Address")) {
                        etOrigin.setText("Source Address: " + maddress);

                        originMarkers.add(mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
                                .title(maddress)
                                .position(latLng)));

                    } else {
                        etDestination.setText("Destination Address: " + maddress);

                        destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end))
                                .title(maddress)
                                .position(latLng)));
                        sendRequest();
                    }

                }

            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        int count = 0;
        routeList = routes;
        float dis1 = 0;
        float dis2 = 0;

        for (Route route : routes) {
            if (count == 2) {
                break;
            }


            if (count == 0) {
                String[] parts = route.distance.text.split(" ");
                dis1 = parseFloat(parts[0]);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
//                ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
                ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            } else {
                String[] parts = route.distance.text.split(" ");
                dis2 = parseFloat(parts[0]);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
//                ((TextView) findViewById(R.id.tvDuration1)).setText(route.duration.text);
                ((TextView) findViewById(R.id.tvDistance1)).setText(route.distance.text);
            }
            count++;

//            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
//                    .title(route.startAddress)
//                    .position(route.startLocation)));

            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < route.points.size(); i++) {

                builder.include(route.points.get(i));

            }
            LatLngBounds bounds = builder.build();
            int padding = 50; // Padding around the polyline in pixels
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cameraUpdate);

        }

        if (dis2 == 0) {
            ((TextView) findViewById(R.id.route1)).setText("Shortest route");
        } else {

            if (dis1 < dis2) {
                ((TextView) findViewById(R.id.route1)).setText("Shortest route");
                ((TextView) findViewById(R.id.route2)).setText("Alternate route");
            } else {
                ((TextView) findViewById(R.id.route2)).setText("Shortest route");
                ((TextView) findViewById(R.id.route1)).setText("Alternate route");
            }
        }


    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                googlemap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                googlemap.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {

            List<String> providers = locationManager.getProviders(true);
            Location locationGPS = null;

            //  Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (locationGPS == null || l.getAccuracy() < locationGPS.getAccuracy()) {
                    // Found best last known location: %s", l);
                    locationGPS = l;
                }
            }


            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                LatLng latLng = new LatLng(parseFloat(latitude), parseFloat(longitude));
//
                Geocoder geocoder = new Geocoder(googlemap.this, Locale.getDefault());
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocation(parseFloat(latitude), parseFloat(longitude), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String maddress = address.get(0).getAddressLine(0);
                etOrigin.setText("Source Address: " + maddress);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parseFloat(latitude), parseFloat(longitude)), 16));

//                originMarkers.add(mMap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start))
//                        .title("a")
//                        .position(latLng)));
//                showLocation.setText("Your Location: " + "
//                        " + "Latitude: " + latitude + "
//                " + "Longitude: " + longitude);
            } else {
                Toast.makeText(googlemap.this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}