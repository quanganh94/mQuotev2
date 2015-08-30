package com.example.image.mquotev2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;

public class SearchActivity {
    ArrayList<ImageResult> imageResults = new ArrayList<>();
    ImageResultsArrayAdapter imageAdapter;
    protected Context context;
    int start = 0;
    String keyword="";
    int position =0;
    SingleScrollListView listview;
    boolean isConnectInternet=true;
    SearchActivity(Context ct, SingleScrollListView lv) {
        this.listview = lv;
        this.context = ct;
        imageResults.clear();
        imageAdapter = new ImageResultsArrayAdapter(context, imageResults);
        imageAdapter.add(null);
        listview.setAdapter(imageAdapter);
    }


    public void Search(String keyword) {
        doSearch(keyword);
    }

    public void doSearch(String query) {
        start += 8;
        AsyncHttpClient client = new AsyncHttpClient();
        String queryString = "https://ajax.googleapis.com/ajax/services/search/images?rsz=8&"
                + "start=" + start + "&imgsz=large" + "&v=1.0&q=" + Uri.encode(query);
        client.get(queryString, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imageJsonResults;
                try {
                    imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");

                    imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
                    Log.d("DEBUG", imageResults.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response){
                Toast.makeText(context,"Check Internet Connection!",Toast.LENGTH_LONG).show();
                isConnectInternet=false;
            }
        });
    }

    public void newSearch(String keyword){
        imageAdapter.clear();
        imageAdapter.notifyDataSetChanged();
        imageResults.clear();
        imageResults.add(null);
        doSearch(keyword);
    }
    public void get_next(String query) {
            if (!keyword.equals(query)) {
                imageResults.clear();
                imageResults.add(null);
                keyword = query;
            }
            AsyncHttpClient client = new AsyncHttpClient();
            String queryString = "https://ajax.googleapis.com/ajax/services/search/images?rsz=1&"
                    + "start=" + start + "&imgsz=large" + "&v=1.0&q=" + Uri.encode(keyword);
            client.get(queryString, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray imageJsonResults;
                    Log.d("DEBUG", response.toString());

                    try {

                        imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
                        imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
                        Log.d("DEBUG", imageResults.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response){
                    Toast.makeText(context,"Check Internet Connection!",Toast.LENGTH_LONG).show();
                    isConnectInternet=false;
                }
            });
            start += 1;
            position = start;
    }


    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }
}
