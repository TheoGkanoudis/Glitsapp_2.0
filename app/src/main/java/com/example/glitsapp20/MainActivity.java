package com.example.glitsapp20;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        if(MapsActivity.poiList.size()==0) {
            poisFromJson();
            trailsFromJson();
        }

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);

        ImageView menu = toolbar.findViewById(R.id.menu_icon);
        menu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(GravityCompat.END);
            }
        });

        ImageView map = toolbar.findViewById(R.id.map_icon);
        map.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);
            }
        });

        Class fragmentClass;
        fragmentClass = HomeFragment.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.END);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void selectDrawerItem(MenuItem menuItem) {

        // Create a new fragment and specify the fragment to show based on nav item clicked

        Fragment fragment = null;

        Class fragmentClass;

        switch(menuItem.getItemId()) {
            case R.id.home:
                fragmentClass = HomeFragment.class;
                break;
            /*case R.id.apano_meria:
                fragmentClass = ApanoActivity.class;
                break;
            case R.id.favs:
                fragmentClass = FavsActivity.class;
                break;
            case R.id.account:
                fragmentClass = AccountActivity.class;
                break;
            case R.id.settings:
                fragmentClass = SettingsActivity.class;
                break;*/
            default:
                fragmentClass = MainActivity.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }

                });

    }

    public void poisFromJson(){
        String title;
        String description;
        String info;
        String image;
        LatLng coords;
        int trail;

        try {
            JSONObject poi = new JSONObject(jsonFromAssets("pois.json"));
            JSONArray poiArray = poi.getJSONArray("points");

            for (int i = 0; i < poiArray.length(); i++) {
                JSONObject itemData = poiArray.getJSONObject(i);

                title = itemData.getString("title");
                description = itemData.getString("description");
                info = itemData.getString("info");
                image = itemData.getString("image");
                trail = itemData.getInt("trail");
                coords = new LatLng(itemData.getJSONArray("coordinates").getDouble(1),itemData.getJSONArray("coordinates").getDouble(0));

                Poi currentPoi = new Poi(title, description, info, image, trail, coords, i);
                MapsActivity.poiList.add(currentPoi);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trailsFromJson(){

        try {
            JSONObject trail = new JSONObject(jsonFromAssets("trails.json"));
            JSONArray trailArray = trail.getJSONArray("features");

            //new
            String name;
            String info;
            String image;
            int difficulty;
            int time;

            for (int i = 0; i < trailArray.length(); i++) {
                JSONObject trailData = trailArray.getJSONObject(i);

                name = trailData.getString("name");
                info = trailData.getString("info");
                image = trailData.getString("image");
                difficulty = trailData.getInt("difficulty");
                time = trailData.getInt("time");

                JSONArray rockArray = trailData.getJSONArray("rocks");
                JSONArray coordsArray = trailData.getJSONArray("coordinates");

                char[] rocks= new char[rockArray.length()];
                for (int j = 0; j < rockArray.length(); j++) {
                    rocks[j] = rockArray.getString(j).charAt(0);
                }

                JSONArray coordsDoubleArray;
                LatLng currentCoords;
                LatLng[] path = new LatLng[coordsArray.length()];

                for (int j = 0; j < coordsArray.length(); j++) {
                    coordsDoubleArray = coordsArray.getJSONArray(j);
                    currentCoords = new LatLng(coordsDoubleArray.getDouble(1), coordsDoubleArray.getDouble(0));
                    path[j] = currentCoords;
                }

                Trail currentTrail = new Trail(name, info, image, difficulty, time, rocks, path, i);
                MapsActivity.trailList.add(currentTrail);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
