package com.sujityadav.flickrtest;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sujityadav.flickrtest.Adapter.ImageAdapter;
import com.sujityadav.flickrtest.Model.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.card_recycler_view);

   init();

    }

    private void init() {

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading...");
        //show dialog
        dialog.show();
        String url = "https://www.flickr.com/services/rest/?method=flickr.galleries.getPhotos&api_key=59f48a8d8dfbb2f9a2e365a94ec2d27b&gallery_id=72157677539266623&format=json&extras=url_sq&nojsoncallback=1";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }

                        ArrayList<Photo> Photos= new ArrayList<>();

                        try {
                            JSONObject jsonObject= response.getJSONObject("photos");
                            JSONArray jsonArray= jsonObject.getJSONArray("photo");

                           for(int i=0;i<jsonArray.length();++i){
                               if(!jsonArray.getJSONObject(i).has("url_sq")) continue;
                               if(!jsonArray.getJSONObject(i).has("id")) continue;
                               if(!jsonArray.getJSONObject(i).has("title")) continue;
                               Photo photo =new Photo();

                               photo.id=jsonArray.getJSONObject(i).getString("id");
                               photo.urlO=jsonArray.getJSONObject(i).getString("url_sq");
                               photo.title=jsonArray.getJSONObject(i).getString("title");

                               Photos.add(photo);
                           }

                            recyclerView.setHasFixedSize(true);
                            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
                            recyclerView.setLayoutManager(layoutManager);
                            ImageAdapter adapter = new ImageAdapter(getApplicationContext(),Photos);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(dialog != null && dialog.isShowing()){
                                dialog.dismiss();
                            }
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }

                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
