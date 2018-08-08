package com.example.madsfinnerup.distfinalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class SellPage extends AppCompatActivity  {

    private TextView mTextMessage;
    private String textName,textPrice, getSavedData;
    private JSONObject obj = new JSONObject();
    private JSONObject jsonData = new JSONObject();

    private EditText itemName;
    private EditText priceTag;
    private Button sellBtn;
    private ListView listView;

    private RequestQueue mRequestQueue;
    private String mRequestBody;

    private int statusCode;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    startActivity(new Intent(getApplicationContext(),BuyPage.class));
                    finish();
                    return true;
                case  R.id.navigation_logout:
                    startActivity(new Intent(getApplicationContext(),LoginPage.class));
                    finish();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_page);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        sellBtn = findViewById(R.id.sell);
        itemName = findViewById(R.id.itemName);
        priceTag = findViewById(R.id.itemPrice);
        listView = findViewById(R.id.itemList);

        ReadFile();
        UpdateList(getSavedData);

        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textName = String.valueOf(itemName.getText());
                textPrice = String.valueOf(priceTag.getText());
                SellVerify(getSavedData);
            }
        });

    }

    public void SellVerify (String str) {
        String url = "http://18.188.46.76/DistFinalMaven/rest/rest2/getName";
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestBody = str;
        mRequestQueue.start();

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("LOG_RESPONSE", response);
                try {
                    obj.put("item",textName);
                    obj.put("name", response);
                    obj.put("price",textPrice);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SellRequest(obj);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_RESPONSE", error.toString());
            }
        }){
            @Override
            public String getBodyContentType() {
                return "text/plain; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {

                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = new String(response.data, StandardCharsets.UTF_8);
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        mRequestQueue.add(stringRequest);
    }

    public void SellRequest ( JSONObject obj) {


        String URL =  "http://18.188.46.76/DistFinalMaven/rest/rest2/sell";

        mRequestQueue.start();

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, URL, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Result handling
                        Log.d("STATE",response.toString() );
                        System.out.println(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                System.out.println("Something went wrong!");
               //Toast.makeText(getApplicationContext(),"Something went wrong, check name or price", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                Log.d("StatusCode", String.valueOf(statusCode));
                if (statusCode >=200 && statusCode <300) {
                    finish();
                    startActivity(new Intent(getApplicationContext(),SellPage.class));
                }
                else {
                    Toast.makeText(getApplicationContext(),"Something went wrong, check name or price", Toast.LENGTH_LONG).show();
                }
                return super.parseNetworkResponse(response);
            }
        };
        mRequestQueue.add(stringRequest);
    }

    private void UpdateList (String str) {

        String url = "http://18.188.46.76/DistFinalMaven/rest/rest2/yourStuff";

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestBody = str;
        mRequestQueue.start();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                HashMap<String, String> hashMapTest = new HashMap<>();

                String hashmapString01, hashmaoString02;
                for(int i = 0; i < response.length();i++){
                    try {
                        jsonData = response.getJSONObject(i);


                        hashmapString01=jsonData.getString("item");
                        hashmaoString02 = jsonData.getString("price");

                        hashMapTest.put(hashmapString01,hashmaoString02);

                        //ArrayList with class

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                List<HashMap<String,String>> listItems = new ArrayList<>();
                SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),listItems,R.layout.listview,
                        new String[]{"First Line","Second Line"},
                        new int[]{R.id.text1,R.id.text2});

                Iterator iterator = hashMapTest.entrySet().iterator();

                while(iterator.hasNext()) {
                    HashMap<String, String> resultMap = new HashMap<>();

                    Map.Entry pair = (Map.Entry) iterator.next();
                    resultMap.put("First Line",pair.getKey().toString());
                    resultMap.put("Second Line",pair.getValue().toString());
                    listItems.add(resultMap);
                }
                    listView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "text/plain; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {

                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
        };


        mRequestQueue.add(jsonArrayRequest);


    }

    public void ReadFile() {
        SharedPreferences sharedPref = getSharedPreferences("jwt", Context.MODE_PRIVATE);
        getSavedData = sharedPref.getString("jwt", "" );
    }


}
