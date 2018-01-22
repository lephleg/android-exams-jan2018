package com.example.lephleg.androidexams;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FetchPlacesTask extends AsyncTask<String[], Void, List<Place>> {

    private final String LOG_TAG = FetchPlacesTask.class.getSimpleName();
    private WeakReference<Activity> mActivityRef;

    private boolean DEBUG = true;

    private static final String RESULTS_LIST_KEY = "results";
    private static final String PLACE_ID_KEY = "id";
    private static final String PLACE_NAME_KEY = "name";
    private static final String PLACE_OPEN_HOURS_KEY = "opening_hours";
    private static final String PLACE_OPEN_NOW_KEY = "open_now";
    private static final String PLACE_RATING_KEY = "rating";
    private static final String PLACE_FULL_ADRESS_KEY = "formatted_address";

    private PlacesAdapter placesAdapter;

    private static final String API_KEY_VALUE = BuildConfig.GOOGLE_API_KEY;

    public FetchPlacesTask(PlacesAdapter adapter) {
        placesAdapter = adapter;
    }


    private List<Place> getPlacesDataFromJson(String forecastJsonStr)  throws JSONException {
        List<Place> placeList = new ArrayList<>();

        try {
            // Instantiate a JSON object from the request response
            JSONObject jsonObject = new JSONObject(forecastJsonStr);
            JSONArray places = jsonObject.getJSONArray("results");

            for (int i = 0; i < places.length(); i++) {

                JSONObject item = places.getJSONObject(i);
                String placeId = item.getString("place_id");
                String placeName = item.getString("name");
                String placeAddress = item.getString("formatted_address");
                Double placeRating = item.getDouble("rating");

                Place place;
                if (item.has("opening_hours")) {
                    JSONObject openingHours = item.getJSONObject("opening_hours");
                    if (openingHours.length() > 0) {
                        Boolean openNow = openingHours.getBoolean("open_now");
                        place = new Place(placeId, placeName, placeAddress, openNow, placeRating);
                    } else {
                        place = new Place(placeId, placeName, placeAddress, placeRating);
                    }
                } else {
                    place = new Place(placeId, placeName, placeAddress, placeRating);
                }

                placeList.add(place);
            }

            Log.d(LOG_TAG, "Fetch Places Task Complete. "+placeList.size()+" places Inserted");

            return placeList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    protected List<Place> doInBackground(String[]... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String[] locationQuery = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String placesString = null;

        try {
            final String QUERY_PARAM = "query";
            final String API_KEY = "key";

            String queryParam = TextUtils.join("+", locationQuery);

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("maps.googleapis.com")
                    .appendPath("maps")
                    .appendPath("api")
                    .appendPath("place")
                    .appendPath("textsearch")
                    .appendPath("json")
                    .appendQueryParameter(API_KEY, API_KEY_VALUE)
                    .appendQueryParameter(QUERY_PARAM, queryParam);

            URL url = new URL(builder.build().toString());

            Log.d(LOG_TAG, "QUERY URI "+url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }

            placesString = buffer.toString();

            Log.d(LOG_TAG, "Places RAW JSON String \n"+placesString);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getPlacesDataFromJson(placesString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(List<Place> places) {

        if(places != null && places.size() > 0){
            placesAdapter.addAll(places);
        }
    }

}