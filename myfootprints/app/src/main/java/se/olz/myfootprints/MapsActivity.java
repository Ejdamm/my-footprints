package se.olz.myfootprints;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback {
    public static final String TAG = MapsActivity.class.getSimpleName();
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db = new DBHelper(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        ArrayList<CoordinatesContainer> allRows = db.getAllEntries();
        LatLng position;
        int size = allRows.size();
        for(int i = 0; i < size; i++) {
            position = new LatLng(allRows.get(i).getLatitude(), allRows.get(i).getLongitude());
            if (allRows.get(i).getOccurance() > 1) {
                map.addMarker(new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            } else {
                map.addMarker(new MarkerOptions()
                        .position(position));
            }
        }
        if (size > 0) {
            position = new LatLng(allRows.get(size - 1).getLatitude(), allRows.get(size - 1).getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        }
    }
}