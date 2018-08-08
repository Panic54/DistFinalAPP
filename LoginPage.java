package com.example.madsfinnerup.distfinalapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class LoginPage extends AppCompatActivity {

    ///rest/rest2/javabog

    private EditText password;
    private EditText userName;
    private Button butLogin;


    private RequestQueue mRequestQueue;
    String mRequestBody;

    private int statusCode;

    private JSONObject obj = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        password = (EditText) findViewById(R.id.Password);
        userName = (EditText) findViewById(R.id.UserName);
        butLogin = (Button) findViewById(R.id.butLogin);

        butLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

               String usernameString = userName.getText().toString();
               String passwordString = password.getText().toString();

                if (usernameString.matches("") || passwordString.matches("")) {
                    Toast.makeText(getApplicationContext(),"Username or password is empty", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Locating your account", Toast.LENGTH_LONG).show();

                    try {
                        obj.put("username",usernameString);
                        obj.put("password",passwordString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Login(obj);
                }
            }
        });

    }

    public void Build (Context context, JSONObject obj) {
        mRequestQueue = Volley.newRequestQueue(context);
        mRequestBody = obj.toString();

        String URL = GetBaseUrl() + "/build";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("LOG_RESPONSE", response);
                WriteToFile(response);
                startActivity(new Intent(getApplicationContext(),SellPage.class));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_RESPONSE", error.toString());
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
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

        // System.out.print(mRequestQueue.add(jsonRequest));
    }

    public void Login (final JSONObject obj) {

        String url = GetBaseUrl() + "/javabog";
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.start();

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
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
                    Build(getApplicationContext(),obj);
                }
                return super.parseNetworkResponse(response);
            }
        };
        mRequestQueue.add(stringRequest);
    }

    public void WriteToFile (String string){

        SharedPreferences sharedPref = getSharedPreferences("jwt", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("jwt", string);
        editor.apply();
    }

    private String GetBaseUrl () {

        return "http://18.188.46.76/DistFinalMaven/rest/rest2";
    }
}
