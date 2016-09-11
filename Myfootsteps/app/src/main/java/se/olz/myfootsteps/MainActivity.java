package se.olz.myfootsteps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            String message = intent.getStringExtra(MapsActivity.MISSING_GPS_MESSAGE);
            TextView textView = new TextView(this);
            textView = (TextView) findViewById(R.id.Display_Error);
            textView.setText(message);
        }
    }

    public void startMapActivity(View view)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
