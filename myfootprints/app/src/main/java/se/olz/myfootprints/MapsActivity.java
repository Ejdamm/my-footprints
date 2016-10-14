package se.olz.myfootprints;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {
    public static final String TAG = MapsActivity.class.getSimpleName();
    private ArrayList<RawPositions> allRows;
    private float zoomLevel;
    private GoogleMap mMap;
    LatLng lastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DBHelper db = new DBHelper(this, User.getEmail());
        allRows = db.getAllEntries();
        zoomLevel = 15;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setOnCameraIdleListener(this);

        update();

        if (lastPosition != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, zoomLevel));
        }
    }

    @Override
    public void onCameraIdle() {
        float prevZoomLevel = zoomLevel;
        float rawZoomLevel = mMap.getCameraPosition().zoom;
        Log.d(TAG, valueOf(rawZoomLevel));
        if (rawZoomLevel >= 16)
            zoomLevel = 16;
        else if (rawZoomLevel >= 11)
            zoomLevel = 11;
        else if (rawZoomLevel >= 9)
            zoomLevel = 9;
        else
            zoomLevel = 8;
        if (prevZoomLevel != zoomLevel) {
            mMap.clear();

            update();
        }

    }

    public void update() {
        ZoomPosContainer toMark = new ZoomPosContainer(allRows, zoomLevel, -1);
        LatLng position = null;
        int size = toMark.zoomedPositions.size();
        for(int i = 0; i < size; i++) {
            position = new LatLng(toMark.get(i).getLatitude(), toMark.get(i).getLongitude());
            MarkerOptions options = new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_marker))
                    .position(position);

            if (toMark.zoomedPositions.get(i).getOccurance() > 50) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_yellow_marker));
                options.zIndex(1.0f);
                options.alpha(1.0f);
            } else if (toMark.zoomedPositions.get(i).getOccurance() > 10) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dorange_marker));
                options.zIndex(0.8f);
                options.alpha(1.0f);
            } else if (toMark.zoomedPositions.get(i).getOccurance() > 2) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_lorange_marker));
                options.zIndex(0.5f);
                options.alpha(0.5f);
            } else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_marker));
                options.zIndex(0.1f);
                options.alpha(0.3f);
            }

            mMap.addMarker(options);
        }
        lastPosition = position;
    }
}