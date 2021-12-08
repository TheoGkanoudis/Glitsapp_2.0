package com.example.glitsapp20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.util.LogPrinter;
import android.widget.Toast;

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
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.kml.KmlLayer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    //TODO ||FUTURE|| MAYBE ADD MORE COLORS - maximum paths: number of colors below

    private static final int[] PATH_COLORS = {0xffff1744,0xff00acc1,0xff9c27b0,0xffc51162,0xff5e35b1,0xff304ffe};
    private static final double MIN_PATHS_VISIBLE_ZOOM = 11.2;
    private static final double MIN_MARKERS_VISIBLE_ZOOM = 12.3;
    private static final int PATH_UNSELECTED_W = 4;
    private  static final int PATH_SELECTED_W = 7;

    ArrayList<String> pathNames = new ArrayList<>();
    ArrayList<String> POIPathNames = new ArrayList<>();
    List<LatLng[]> Prows = new ArrayList<LatLng[]>();
    LatLng[] Pline;
    List<LatLng[]> Mrows = new ArrayList<LatLng[]>();
    LatLng[] Mline;
    ArrayList<Polyline> polylines = new ArrayList<>();
    ArrayList<Marker> markers = new ArrayList<>();
    public boolean[] visibleMarkers;
    public int pathSelected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        makeFeature("paths.json", Prows, Pline, pathNames);
        makeFeature("markers.json", Mrows, Mline, POIPathNames);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng apanoMeria = new LatLng(37.49913, 24.907264);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(apanoMeria));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnPolylineClickListener(this);
        mMap.setOnMapClickListener(this);
        drawPaths();
        drawMarkers();
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

    //TODO incorporate user location
    //TODO make paths clickable - center on path when touched

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

}