package com.example.glitsapp20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationProvider;
import android.os.Bundle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.JsonWriter;
import android.util.Log;
import android.util.LogPrinter;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.internal.ICancelToken;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.kml.KmlLayer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMapLongClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    ArrayList<String> pathNames = new ArrayList<>();
    ArrayList<String> POIPathNames = new ArrayList<>();
    List<LatLng[]> Prows = new ArrayList<LatLng[]>();
    LatLng[] Pline;
    List<LatLng[]> Mrows = new ArrayList<LatLng[]>();
    LatLng[] Mline;
    ArrayList<Polyline> polylines = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();

    public boolean permissionDenied = false;
    public boolean[] visibleMarkers;
    private boolean locationPermissionsGranted = false;
    public int pathSelected = -1;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationClient;

    LatLng apanoMeria = new LatLng(37.49913, 24.907264);
    LatLng cameraPosition = apanoMeria;
    Location userLocation;

    // FINALS //

    private static final int[] PATH_COLORS = {0xffff1744, 0xff00acc1, 0xff9c27b0, 0xffc51162, 0xff5e35b1, 0xff304ffe};
    private static final double MIN_PATHS_VISIBLE_ZOOM = 11.2;
    private static final double MIN_MARKERS_VISIBLE_ZOOM = 12.3;
    private static final int PATH_UNSELECTED_W = 4;
    private static final int PATH_SELECTED_W = 7;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //keys for storing activity state
    public static final String KEY_LOCATION = "location";
    public static final String KEY_CAMERA_POSITION = "camera_position";


    // DEFAULT //

    //operational
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            userLocation = lastKnownLocation;
            Toast.makeText(this, "idk", Toast.LENGTH_SHORT).show();
        }



        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        makeFeature("paths.json", Prows, Pline, pathNames);
        makeFeature("markers.json", Mrows, Mline, POIPathNames);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }
    //map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition,13));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnPolylineClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        enableMyLocation();
        drawPaths();
        drawMarkers();

        getDeviceLocation();
    }

    @Override
    public void onCameraMove()
    {
        zoomRethink();
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        pathRethink(polyline);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if(pathSelected>=0){pathRethink(polylines.get(pathSelected));}
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }


    // LOCATION //

    private void enableMyLocation(){
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

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionsGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            lastKnownLocation = task.getResult();
                            if(lastKnownLocation.getLatitude()<37.514961&&lastKnownLocation.getLatitude()>37.479590&&
                                    lastKnownLocation.getLongitude()<24.946950&&lastKnownLocation.getLongitude()>24.875893){
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()),(float) 14.7));
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    // FEATURES //

    private void makeFeature(String filename, List<LatLng[]> rows, LatLng[] line, ArrayList<String> names){
        try {
            JSONObject path = new JSONObject(jsonFromAssets(filename));
            JSONArray pathArray = path.getJSONArray("features");

            for (int i=0; i<pathArray.length(); i++){
                JSONObject pathData = pathArray.getJSONObject(i);
                //fot the name
                names.add(pathData.getString("name"));
                //for the coordinates
                JSONArray coordsArray = pathData.getJSONArray("coordinates");
                JSONArray coordsDoubleArray;
                LatLng coords;
                line = new LatLng[coordsArray.length()];
                for(int j=0; j<coordsArray.length(); j++)
                {
                    coordsDoubleArray = coordsArray.getJSONArray(j);
                    //geojson provides coordinates in long - lat format (not lat - long):
                    coords = new LatLng(coordsDoubleArray.getDouble(1), coordsDoubleArray.getDouble(0));
                    line[j] = coords;
                }
                rows.add(line);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawPaths(){
        List<LatLng> list = new ArrayList<>();
        Polyline polyline;

        for(int i = 0; i < Prows.size(); i++){
            polyline = mMap.addPolyline(new PolylineOptions()
            .clickable(true)
            .color(PATH_COLORS[i])
            .width(PATH_UNSELECTED_W));
            for(int j = 0; j < Prows.get(i).length; j++){
                list.add(Prows.get(i)[j]);
            }
            polyline.setPoints(list);
            polylines.add(polyline);
            list.clear();
        }
    }

    private void drawMarkers(){
        Marker marker;
        for(int i = 0; i < Mrows.size(); i++){
            for(int j = 0; j < Mrows.get(i).length; j++){
                marker = mMap.addMarker(new MarkerOptions()
                .position((Mrows.get(i)[j]))
                .title("POI "+(j+1))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)));
                marker.setVisible(false);
                markers.add(marker);
            }
        }
        visibleMarkers = new boolean[markers.size()];
    }

    // ADJUSTMENTS //

    private void zoomRethink(){
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
                if(visibleMarkers[i]){
                    markers.get(i).setVisible(true);
                }
            } else {
                markers.get(i).setVisible(false);
            }
        }
    }

    private void pathRethink(Polyline polyline){
        if(polyline.getWidth()==PATH_UNSELECTED_W){
            polyline.setWidth(PATH_SELECTED_W);
            for(int i=0; i<polylines.size(); i++){
                if(!polylines.get(i).getId().equals(polyline.getId())){
                    polylines.get(i).setWidth(PATH_UNSELECTED_W);
                }
                else{
                    pathSelected = i;
                }

            }
            markersRethink();
            cameraRethink();
        }
        else{
            for(int i=0; i<polylines.size(); i++){
                polylines.get(i).setWidth(PATH_UNSELECTED_W);
                pathSelected = -1;
                markersRethink();
            }

        }
    }

    private void markersRethink(){
        int id = 0;
        for(int i = 0; i < Mrows.size(); i++){
            for(int j = 0; j < Mrows.get(i).length; j++){
                if(i==pathSelected){
                    markers.get(id).setVisible(true);
                    visibleMarkers[id] = true;
                }
                else{
                    markers.get(id).setVisible(false);
                    visibleMarkers[id] = false;
                }
                id++;
            }
        }
    }

    private void cameraRethink(){
        List<LatLng> temp = polylines.get(pathSelected).getPoints();
        double lat = temp.get(0).latitude;
        double lng = temp.get(0).longitude;
        lat += temp.get(temp.size()-1).latitude;
        lng += temp.get(temp.size()-1).longitude;
        lat /= 2; lng /= 2;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), (float) 13.65));
    }

    // OPERATIONAL //

    private String jsonFromAssets(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] bufferData = new byte[size];
            is.read(bufferData);
            is.close();
            json = new String(bufferData, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


}