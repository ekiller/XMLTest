package com.natiki.xmltest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

    import java.util.ArrayList;
    import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
    import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

    import android.app.ListActivity;
    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
    import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
    import android.widget.ListView;
    import android.widget.SimpleAdapter;
    import android.widget.TextView;

    public class MainActivity extends ListActivity {

        // All static variables
        static final String URL = "http://opendap.co-ops.nos.noaa.gov/stations/stationsXML.jsp";
        static final String dURL = "http://tidesandcurrents.noaa.gov/api/datagetter?date=latest&station=8726724&product=water_temperature&units=metric&time_zone=gmt&application=ports_screen&format=json";
        // XML node keys
        static final String KEY_STATION = "station"; // parent node
        static final String KEY_ID = "ID";
        static final String KEY_NAME = "name";
        static final String KEY_LAT = "lat";
        static final String KEY_WTEMP = "v";
        EditText editsearch;
        JSONArray user = null;
        private String wTemp = null;



        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


// get xml data
            new GetXMLAsyncTask().execute();
            new JSONParse().execute();



// selecting single ListView item
            ListView lv = getListView();
            lv.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
// getting values from selected ListItem
                    String name = ((TextView)view.findViewById(R.id.name)).getText().toString();
                    String ID = ((TextView)view.findViewById(R.id.ID)).getText().toString();
                    String lat = ((TextView)view.findViewById(R.id.lat)).getText().toString();
                    String wTemp = ((TextView)view.findViewById(R.id.wTemp)).getText().toString();

// Starting new Intent
                    Intent in = new Intent(getApplicationContext(), SingleMenuItemActivity.class);
                    in.putExtra(KEY_NAME, name);
                    in.putExtra(KEY_ID, ID);
                    in.putExtra(KEY_LAT, lat);
                    in.putExtra(KEY_WTEMP, wTemp);
                     startActivity(in);
                }
            });
        }

        private class GetXMLAsyncTask extends AsyncTask<Void, Void, String> {
            private ArrayList<HashMap<String, String>> menuItems = null;
            private XMLParser parser = null;

            String stName = null;
            String stID = null;
            public GetXMLAsyncTask() {
                super();
                menuItems = new ArrayList<HashMap<String, String>>();
                parser = new XMLParser();
            }

            @Override
            protected void onPostExecute(String xml) {
                super.onPostExecute(xml);

                if (null != xml) {
                    Document doc = parser.getDomElement(xml);

                    NodeList nl = doc.getElementsByTagName(KEY_STATION);


// looping through all item nodes <item>
                    for (int i = 0; i < nl.getLength(); i++) {
// creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
                        Element e = (Element) nl.item(i);

                        Node stationNode = doc.getElementsByTagName(KEY_STATION).item(i);
                        Element stationElement = (Element)stationNode;

                        NamedNodeMap attrs = stationNode.getAttributes();
// adding each child node to HashMap key => value
                        for (int a=0; a<attrs.getLength();a++) {
                            Node theAttr = attrs.item(0);
                            Node theAttr1 = attrs.item(1);

                            stName = theAttr.getNodeValue();
                            stID = theAttr1.getNodeValue();

                        }

                            map.put(KEY_NAME, stName);
                            map.put(KEY_ID,stID);
                            map.put(KEY_LAT, parser.getValue(e, KEY_LAT));
                      //  map.put(KEY_ID, parser.getValue(e, KEY_ID));


// adding HashMap to ArrayList
                        menuItems.add(map);
                    }

// Adding menuItems to ListView
                    final ListAdapter adapter = new SimpleAdapter(getApplicationContext(), menuItems
                            , R.layout.list_item, new String[] { KEY_NAME, KEY_ID, KEY_LAT }
                            , new int[]{R.id.name, R.id.ID, R.id.lat});

                    // updating listview
                    setListAdapter(adapter);

                    EditText inputSearch = (EditText) findViewById(R.id.search);

                    inputSearch.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                            // When user changed the Text
                            ((SimpleAdapter)adapter).getFilter().filter(cs);

                        }

                        @Override
                        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                      int arg3) {

                            // TODO Auto-generated method stub


                        }

                        @Override
                        public void afterTextChanged(Editable arg0) {
                            // TODO Auto-generated method stub

                        }
                    });



                }
            }






            @Override
            protected String doInBackground(Void... params) {
                return parser.getXmlFromUrl(URL); // getting XML
            }
        }

        private class JSONParse extends AsyncTask<String, String, JSONObject> {
            private ProgressDialog pDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Getting Data ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
            @Override
            protected JSONObject doInBackground(String... args) {
                JSONParser jParser = new JSONParser();
                // Getting JSON from URL
                JSONObject json = jParser.getJSONFromUrl(dURL);
                return json;
            }
            @Override
            protected void onPostExecute(JSONObject json) {
                pDialog.dismiss();
                try {

                    JSONObject dataObj = json.getJSONObject("data");
                    String wTemp1 = dataObj.getString("v");


                    JSONArray jArr = json.getJSONArray("list");

                    for (int i=0; i < jArr.length(); i++) {
                       JSONObject obj = jArr.getJSONObject(i);
                        String wTemp2 = obj.getString("v");
                        TextView wTEMP = (TextView) findViewById(R.id.wTemp);
                        wTEMP.setText(wTemp2);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }

    }