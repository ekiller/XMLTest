package com.natiki.xmltest;


        import android.app.Activity;
        import android.content.Intent;
        import android.os.Bundle;
        import android.widget.TextView;

public class SingleMenuItemActivity  extends Activity {

    // XML node keys
    static final String KEY_NAME = "name";
    static final String KEY_ID = "ID";
    static final String KEY_LAT = "lat";
    static final String KEY_WTEMP = "v";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_item);

        // getting intent data
        Intent in = getIntent();

        // Get XML values from previous intent
        String name = in.getStringExtra(KEY_NAME);
        String idi = in.getStringExtra(KEY_ID);
        String lat = in.getStringExtra(KEY_LAT);
        String wTemp = in.getStringExtra(KEY_WTEMP);

        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.name_label);
        TextView lblId = (TextView) findViewById(R.id.id_label);
        TextView lblLat = (TextView) findViewById(R.id.lat_label);
        TextView lblwTemp = (TextView) findViewById(R.id.wTemp_label);


        lblName.setText(name);
        lblId.setText(idi);
        lblLat.setText(lat);
        lblwTemp.setText(wTemp);

    }
}
