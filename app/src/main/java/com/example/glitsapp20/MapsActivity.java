package com.example.glitsapp20;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.data.Style;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;


public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private static Context mContext;
    public boolean permissionDenied = false;
    public boolean[] visibleMarkers;
    private boolean locationPermissionsGranted = false;
    public int trailSelected = -1;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean locBut = true;
    private LatLng userPosition;
    private LatLng newPosition;
    private int icon = 0;
    private double minLocDist = 0.01;
    private double minLocDistNew;
    private boolean success = false;

    private String notificationPoi = "Κενό";
    CountDownTimer countDownTimer;


    public static ArrayList<Poi> poiList = new ArrayList<>();
    public static ArrayList<Trail> trailList = new ArrayList<>();
    public static int trailToPass;
    public static int poiToPass;

    Runnable checkProximity = new Runnable() {
        @Override
        public void run() {
            if (getDeviceLocation()) {
                for (Poi poi : poiList) {
                    if (getDistance(poi.getCoords(), new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())) < 0.0004) {

                        poiToPass = poi.getId();

                        Intent intent = new Intent(MapsActivity.this, PoiInfoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MapsActivity.this, 0, intent, 0);

                        notificationPoi = poi.getTitle();
                        defBuilder.setContentIntent(pendingIntent);
                        defBuilder.setContentText("Πλησιάζεις το: " + notificationPoi + "!\nΠάτα εδώ για τις σχετικές πληροφορίες!");
                        defBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Πλησιάζεις το: " + notificationPoi + "!\nΠάτα εδώ για τις σχετικές πληροφορίες!"));
                        showNotification();
                    }
                }
            }
        }
    };

    ArrayList<Polyline> polylines = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    int[] markersPerTrail;

    LatLng apanoMeria = new LatLng(37.49913, 24.907264);
    LatLng cameraPosition = apanoMeria;
    ConstraintLayout mainLayout;

    // FINALS //

    private static final int[] PATH_COLORS = {0xffff1744, 0xff00acc1, 0xff9c27b0, 0xffc51162, 0xff5e35b1, 0xff304ffe};
    private static final double MIN_PATHS_VISIBLE_ZOOM = 11.2;
    private static final double MIN_MARKERS_VISIBLE_ZOOM = 12.3;
    private static final int PATH_UNSELECTED_W = 4;
    private static final int PATH_SELECTED_W = 7;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float DEFAULT_MARKER_ALPHA = (float) 0.5;

    //keys for storing activity state
    public static final String KEY_LOCATION = "location";
    public static final String KEY_CAMERA_POSITION = "camera_position";

    // NOTIFICATIONS //
    private static final String CHANNEL_ID = "def";
    private static final int DEF_NOTIFICATION = 0;

    NotificationCompat.Builder defBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.flag)
            .setContentTitle("POI Alert")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);


    // DEFAULT //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mainLayout = (ConstraintLayout) findViewById(R.id.main_layout);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        newPosition = cameraPosition;

        createNotificationChannel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (permissionDenied) {
            //TODO Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }


    // MAP //
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, 13));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnPolylineClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        enableMyLocation();
        getDeviceLocation();
        drawPaths();
        drawMarkers();
        setOnClickListeners();
        usingCountDownTimer();

    }

    @Override
    public void onCameraMove() {
        zoomRethink();
        updateLocateIcon();
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        pathsRethink(polyline);
        PoiPopup.hidePoiPopup(mainLayout);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (mMap.getCameraPosition().zoom <= 14.5) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), (float) 14.5));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        }
        poiToPass = Integer.parseInt(marker.getTitle());
        TrailPopup.hideTrailPopup(mainLayout);
        markersAlpha();
        marker.setAlpha(1);
        PoiPopup.showPoiPopup(Integer.parseInt(Objects.requireNonNull(marker.getTitle())), mainLayout);
        return true;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (trailSelected >= 0) {
            pathsRethink(polylines.get(trailSelected));
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        locBut = false;
        if (locationPermissionsGranted && getDeviceLocation()) {
            if (mMap.getCameraPosition().zoom > 14) {
                //TODO - fix the power value maybe??
                minLocDistNew = minLocDist / Math.pow(mMap.getCameraPosition().zoom - 13, 1.25);
            } else {
                minLocDistNew = minLocDist;
            }
            if (getDistance(userPosition, newPosition) < minLocDistNew) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(apanoMeria, 13));
                locBut = true;
            }
        }
        return locBut;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        TrailPopup.refreshPopup(mainLayout);
        PoiPopup.refreshPopup(mainLayout);
    }


    // LOCATION //

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
            locationPermissionsGranted = true;
        } else {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    private boolean getDeviceLocation() {
        try {
            if (locationPermissionsGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation.getLatitude() < 37.514961 && lastKnownLocation.getLatitude() > 37.479590 &&
                                    lastKnownLocation.getLongitude() < 24.946950 && lastKnownLocation.getLongitude() > 24.875893) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), (float) 14.7));
                            }
                            success = true;
                            userPosition = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        }
                    }
                });
            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
        return success;
    }

    // PATHS //

    private void drawPaths() {
        List<LatLng> list = new ArrayList<>();
        Polyline polyline;

        for (int i = 0; i < trailList.size(); i++) {
            polyline = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .color(PATH_COLORS[i])
                    .width(PATH_UNSELECTED_W));
            for (int j = 0; j < trailList.get(i).getCoords().length; j++) {
                list.add(trailList.get(i).getCoords()[j]);
            }
            polyline.setPoints(list);
            polylines.add(polyline);
            list.clear();
        }
    }

    // POIS //

    private void drawMarkers() {
        Marker marker;
        int currentTrail = 0;
        int counter = 0;
        markersPerTrail = new int[trailList.size()];
        for (int i = 0; i < poiList.size(); i++) {
            marker = mMap.addMarker(new MarkerOptions()
                    .alpha((float) 0.5)
                    .position(poiList.get(i).getCoords())
                    .title("" + i)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)));
            marker.setVisible(false);
            markers.add(marker);

            if (poiList.get(i).getTrail() != currentTrail) {
                markersPerTrail[currentTrail] = counter;
                currentTrail++;
                counter = 0;
            }
            counter++;
        }
        markersPerTrail[currentTrail] = counter;
        visibleMarkers = new boolean[markers.size()];
    }

    // ADJUSTMENTS //

    private void zoomRethink() {
        //paths
        for (int i = 0; i < polylines.size(); i++) {
            if (mMap.getCameraPosition().zoom > MIN_PATHS_VISIBLE_ZOOM) {
                polylines.get(i).setVisible(true);
            } else {
                polylines.get(i).setVisible(false);
            }
        }
        //markers
        for (int i = 0; i < markers.size(); i++) {
            if (mMap.getCameraPosition().zoom > MIN_MARKERS_VISIBLE_ZOOM) {
                if (visibleMarkers[i]) {
                    markers.get(i).setVisible(true);
                }
            } else {
                markers.get(i).setVisible(false);
            }
        }
    }

    private void pathsRethink(Polyline polyline) {
        if (polyline.getWidth() == PATH_UNSELECTED_W) {
            polyline.setWidth(PATH_SELECTED_W);
            for (int i = 0; i < polylines.size(); i++) {
                if (!polylines.get(i).getId().equals(polyline.getId())) {
                    polylines.get(i).setWidth(PATH_UNSELECTED_W);
                } else {
                    trailSelected = i;
                }

            }
            cameraToTrail();
            TrailPopup.showTrailPopup(trailSelected, mainLayout);
        } else {
            if (mainLayout.findViewById(R.id.poi_popup).getVisibility() == GONE) {
                polyline.setWidth(PATH_UNSELECTED_W);
                TrailPopup.hideTrailPopup(mainLayout);
                trailSelected = -1;
            } else {
                cameraToTrail();
                markersAlpha();
                PoiPopup.hidePoiPopup(mainLayout);
                TrailPopup.showTrailPopup(trailSelected, mainLayout);
            }
        }
        markersRethink();
    }

    private void markersRethink() {
        int id = 0;
        markersAlpha();
        for (int i = 0; i < markersPerTrail.length; i++) {
            for (int j = 0; j < markersPerTrail[i]; j++) {
                if (i == trailSelected) {
                    markers.get(id).setVisible(true);
                    visibleMarkers[id] = true;
                } else {
                    markers.get(id).setVisible(false);
                    visibleMarkers[id] = false;
                }
                id++;
            }

        }

    }

    private void cameraToTrail() {
        List<LatLng> temp = polylines.get(trailSelected).getPoints();
        double lat1 = Math.min(temp.get(0).latitude, temp.get(temp.size() - 1).latitude);
        double lng1 = Math.min(temp.get(0).longitude, temp.get(temp.size() - 1).longitude);
        double lat2 = Math.max(temp.get(0).latitude, temp.get(temp.size() - 1).latitude);
        double lng2 = Math.max(temp.get(0).longitude, temp.get(temp.size() - 1).longitude);
        LatLng sw = new LatLng(lat1, lng1);
        LatLng ne = new LatLng(lat2, lng2);
        LatLngBounds bounds = new LatLngBounds(sw, ne);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    // OPERATIONAL //

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private double getDistance(LatLng a, LatLng b) {
        return Math.hypot(Math.abs(a.longitude - b.longitude), Math.abs(a.latitude - b.latitude));
    }

    private void updateLocateIcon() {
        newPosition = mMap.getCameraPosition().target;
        if (!locationPermissionsGranted || !getDeviceLocation()) {
            userPosition = newPosition;
        }
    }

    // LISTENERS //

    private void setOnClickListeners() {

        Intent ti = new Intent(mContext, TrailInfoActivity.class);
        View trailPopup = mainLayout.findViewById(R.id.trail_popup);
        trailPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trailToPass = trailSelected;
                startActivity(ti);
            }
        });

        Intent pi = new Intent(mContext, PoiInfoActivity.class);
        View poiPopup = mainLayout.findViewById(R.id.poi_popup);
        poiPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(pi);
            }
        });

        ImageView home = mainLayout.findViewById(R.id.home_icon);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView layers = mainLayout.findViewById(R.id.icon_layers);
        layers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMap.getMapType()==GoogleMap.MAP_TYPE_TERRAIN) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

    }


    // NOTIFICATIONS //

    private void showNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        defBuilder.setSound(alarmSound);
        notificationManager.notify(DEF_NOTIFICATION, defBuilder.build());
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    // MISC //

    public static Trail getTrail() {
        return trailList.get(trailToPass);
    }

    public static Poi getPoi() {
        return poiList.get(poiToPass);
    }

    public static ArrayList<String> getPoiTitles(Trail trail) {
        ArrayList<String> titles = new ArrayList<>();
        for (int i = 0; i < poiList.size(); i++) {
            if (poiList.get(i).getTrail() == trail.getId()) {
                titles.add(poiList.get(i).getTitle());
            }
        }
        return titles;
    }

    public void markersAlpha() {
        for (Marker marker : markers) {
            marker.setAlpha(DEFAULT_MARKER_ALPHA);
        }
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void usingCountDownTimer() {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 5000) {

            public void onTick(long millisUntilFinished) {
                checkProximity.run();
            }

            public void onFinish() {
                start();
            }
        }.start();
    }
}
