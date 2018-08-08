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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class BuyPage extends AppCompatActivity {

    private ListView listView;
    private int statusCode = 0;
    private String getSavedData = "";

    private JSONObject jsonData = new JSONObject();

    private RequestQueue mRequestQueue;
    private String mRequestBody;

    private JSONObject obj;
    private ItemsForSale itemsForSale;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(getApplicationContext(),SellPage.class));
                    finish();
                    return true;
                case R.id.navigation_dashboard:

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
        setContentView(R.layout.activity_buy_page);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        listView = findViewById(R.id.listView02);

        ReadFile();
        UpdateList(getSavedData);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Object o = listView.getItemAtPosition(position);
                itemsForSale = (ItemsForSale) listView.getItemAtPosition(position);

                obj = new JSONObject();
                try {
                    obj.put("name", itemsForSale.getPersonName());
                    obj.put("item", itemsForSale.getItemName());
                    obj.put("price", itemsForSale.getPrice());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("Test", String.valueOf(obj));
                BuyItem(obj);

            }
        });
    }

    private void UpdateList (String str) {

        String url = "http://18.188.46.76/DistFinalMaven/rest/rest2/otherStuff";
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestBody = str;
        mRequestQueue.start();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                ArrayList<ItemsForSale> arrayList = new ArrayList<>();

                for(int i = 0; i < response.length();i++){
                    try {
                        jsonData = response.getJSONObject(i);

                        itemsForSale = new ItemsForSale(jsonData.getString("name"),
                                jsonData.getString("item"),
                                jsonData.getString("price"));

                        arrayList.add(itemsForSale);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ListAdapter listAdapter = new ListAdapter(getApplicationContext(),R.layout.listview02,arrayList);

                listView.setAdapter(listAdapter);

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

    private void BuyItem (JSONObject obj) {

        String url = "http://18.188.46.76/DistFinalMaven/rest/rest2/buy2";

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.start();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
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
                    error.printStackTrace();

            }

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;

                if (statusCode >=200 && statusCode <300) {
                    finish();
                    startActivity(new Intent(getApplicationContext(),BuyPage.class));
                }
                return super.parseNetworkResponse(response);
            }
        };
        mRequestQueue.add(jsonRequest);

    }

    public void ReadFile() {
        SharedPreferences sharedPref = getSharedPreferences("jwt", Context.MODE_PRIVATE);
        getSavedData = sharedPref.getString("jwt", "" );
    }
}
