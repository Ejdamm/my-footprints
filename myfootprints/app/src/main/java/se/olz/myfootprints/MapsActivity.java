package se.olz.myfootprints;

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
    private ArrayList<RawPositions> allRows;
    private float zoomLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DBHelper db = new DBHelper(this);
        allRows = db.getAllEntries();
        zoomLevel = 15;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        ZoomPosContainer toMark = new ZoomPosContainer(allRows, zoomLevel, -1);
        LatLng position;
        int size = toMark.zoomedPositions.size();
        for(int i = 0; i < size; i++) {
            position = new LatLng(toMark.get(i).getLatitude(), toMark.get(i).getLongitude());
            if (toMark.zoomedPositions.get(i).getOccurance() > 1) {
                map.addMarker(new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            } else {
                map.addMarker(new MarkerOptions()
                        .position(position));
            }
        }
        if (size > 0) {
            position = new LatLng(toMark.get(size - 1).getLatitude(), toMark.get(size - 1).getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));
        }
    }
}