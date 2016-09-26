package se.olz.myfootprints;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
        for(int i = 0; i < allRows.size(); i++) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(allRows.get(i).getLatitude(), allRows.get(i).getLongitude())));
        }

    }
}