package se.olz.myfootprints;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        LocationProvider.LocationCallback {

    public static final String TAG = MainActivity.class.getSimpleName();
    private LocationProvider mLocationProvider;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int RESET_ERROR = 0;
    private static final int MISSING_GPS_ERROR = 1;
    private static final int ROWS = 2;
    private boolean trackingStarted = false;
    private long sessionid = 0;
    private DBHelper db;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationProvider = new LocationProvider(this, this, this);
        db = new DBHelper(this);
        db.clean();
    }

    private void appendLatestEntry() {
        TextView textView;
        textView = (TextView) findViewById(R.id.textViewEntries);
        ArrayList<CoordinatesContainer> allRows = db.getAllEntries();
        textView.append(allRows.get(allRows.size()-1).toString() + "\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                    displayError(RESET_ERROR);
                    mLocationProvider.connect();
                    toggleTrackingText();
                    trackingStarted = true;
                }
                else {
                    mLocationProvider.disconnect();
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

    public void toggleTracking(View view)
    {
        if (trackingStarted) {
            mLocationProvider.disconnect();
            toggleTrackingText();
            trackingStarted = false;
        }
        else {
            mLocationProvider.connect();
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                toggleTrackingText();
                trackingStarted = true;
                date = new Date();
                sessionid = date.getTime()/1000;
            }
        }
    }

    private void toggleTrackingText() {
        Button button;
        button = (Button)findViewById(R.id.toggle_tracking_button);
        if (trackingStarted) {
            String label = getResources().getString(R.string.tracking_button_start);
            button.setText(label);
        }
        else {
            String label = getResources().getString(R.string.tracking_button_stop);
            button.setText(label);
        }
    }

    public void handleNewLocation(Location location) {
        TextView textView;
        textView = (TextView)findViewById(R.id.coordinates);
        if (location != null) {
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            LatLng myPosition = new LatLng(currentLatitude, currentLongitude);
            textView.setText(myPosition.toString());

            date = new Date();
            long timestamp = date.getTime()/1000;
            db.insertCoordinates(sessionid, timestamp, currentLatitude, currentLongitude);
            appendLatestEntry();
            displayError(ROWS);

        }
        else {
            String empty = getResources().getString(R.string.empty);
            textView.setText(empty);
        }

    }

    public void displayError(int errorCode) {
        TextView textView;
        textView = (TextView) findViewById(R.id.display_error);
        switch (errorCode) {
            case RESET_ERROR: {
                String error = getResources().getString(R.string.empty);
                textView.setText(error);
                break;
            }
            case MISSING_GPS_ERROR: {
                String error = getResources().getString(R.string.missing_gps_error);
                textView.setText(error);
                break;
            }
            case ROWS: {
                Integer interror = db.numberOfRows();
                Log.i(TAG, "Nr of rows in database: " + interror.toString());
                String error = "Nr of rows in database: " + interror.toString();
                textView.setText(error);
                break;
            }

        }

    }
}
