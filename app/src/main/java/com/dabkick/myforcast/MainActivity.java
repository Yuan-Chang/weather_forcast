package com.dabkick.myforcast;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.goebl.david.Webb;
import com.goebl.david.WebbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    final String API_KEY = "203bf0976335ed98863b556ed9f61f79";
    final String URL = "https://api.forecast.io/forecast/";

    final String DEFAULT_LONGITUDE = "-122.03821";
    final String DEFAULT_LATITUDE = "37.3417416";

    double longitude;
    double latitude;

    EditText mLongtitudeEdit;
    EditText mLatitudeEdit;
    Button mButton;
    Button mClear;
    ListView mListview;
    ListviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLongtitudeEdit = (EditText) findViewById(R.id.longtitudeEdit);
        mLatitudeEdit = (EditText) findViewById(R.id.latitudeEdit);
        mButton = (Button) findViewById(R.id.button);
        mListview = (ListView) findViewById(R.id.listview);
        mClear = (Button) findViewById(R.id.clear);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                String s1 = longitude + "";
                String s2 = latitude + "";

                mLongtitudeEdit.setText(s1.length() > 10 ? s1.substring(0, 10) : s1);
                mLatitudeEdit.setText(s2.length() > 10 ? s2.substring(0, 10) : s2);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //If the phone has built-in GPS, try to get Longtitude and Latitude
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //set default value
                if (mLatitudeEdit.getText().toString().isEmpty()) {
                    mLatitudeEdit.setText(DEFAULT_LATITUDE);
                    latitude = Double.parseDouble(DEFAULT_LATITUDE);
                }
                if (mLongtitudeEdit.getText().toString().isEmpty()) {
                    mLongtitudeEdit.setText(DEFAULT_LONGITUDE);
                    longitude = Double.parseDouble(DEFAULT_LONGITUDE);
                }

                dismissKeyboard(MainActivity.this);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String path = URL + API_KEY + "/" + latitude + "," + longitude;

                        Webb webb = Webb.create();

                        try {
                            String result = webb.get(path).asString().getBody();
                            try {
                                JSONObject jsonObject = new JSONObject(result);

                                final JSONObject jsonCurrent = jsonObject.getJSONObject("currently");
                                final JSONObject jsonDaily = jsonObject.getJSONObject("daily");

                                ArrayList<ForcastData> list = new ArrayList<ForcastData>();

                                ForcastData current = new ForcastData("Current");
                                current.temparature = jsonCurrent.getString("temperature");
                                current.summary = jsonCurrent.getString("summary");
                                list.add(current);

                                JSONArray jsonArray = jsonDaily.getJSONArray("data");
                                for (int i = 1; i < 6; i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Long timestamp = object.getLong("time");
                                    Date date = new java.util.Date(timestamp * 1000);
                                    String ds = date.toString();

                                    ForcastData forcastData = new ForcastData(ds.substring(0, ds.indexOf(" 00")));
                                    forcastData.max_temparature = object.getString("temperatureMax");
                                    forcastData.min_temparature = object.getString("temperatureMin");
                                    list.add(forcastData);
                                }

                                mAdapter = new ListviewAdapter(getApplication(), list);

                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mListview.setAdapter(mAdapter);
                                        mListview.setVisibility(View.VISIBLE);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (WebbException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLatitudeEdit.setText("");
                mLongtitudeEdit.setText("");
                mListview.setVisibility(View.GONE);
            }
        });
    }

    static public void dismissKeyboard(Activity activity) {

        if (activity == null)
            return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) { // verify if the soft keyboard is open
            if (activity.getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


}
