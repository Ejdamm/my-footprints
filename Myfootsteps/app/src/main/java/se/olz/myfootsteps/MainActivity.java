package se.olz.myfootsteps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements
        LocationProvider.LocationCallback {

    private LocationProvider mLocationProvider;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int RESET_ERROR = 0;
    private static final int MISSING_GPS_ERROR = 1;
    private boolean mPermissionDenied = false;
    private boolean trackingStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            //displayError(MISSING_GPS_ERROR);
        }

        mLocationProvider = new LocationProvider(this, this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (trackingStarted) {
            //mLocationProvider.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionDenied = false;
                    displayError(RESET_ERROR);
                    mLocationProvider.connect();
                }
                else {
                    mPermissionDenied = true;
                    displayError(MISSING_GPS_ERROR);
                }
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

    public void startMapActivity(View view)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void startTracking(View view)
    {
        mLocationProvider.connect();
        trackingStarted = true;
    }

    public void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng myPosition = new LatLng(currentLatitude, currentLongitude);

        TextView textView = new TextView(this);
        textView = (TextView)findViewById(R.id.Coordinates);
        textView.setText(myPosition.toString());
    }

    public void displayError(int errorCode) {
        TextView textView = new TextView(this);
        textView = (TextView) findViewById(R.id.Display_Error);
        switch (errorCode) {
            case MISSING_GPS_ERROR: {
                String error = getResources().getString(R.string.missing_gps_error);
                textView.setText(error);
                break;
            }
            default: {
                String error = getResources().getString(R.string.empty);
                textView.setText(error);
            }
        }

    }
}
