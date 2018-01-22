package com.example.lephleg.androidexams;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlacesAdapter extends ArrayAdapter<Place> {

    private List<Place> places;

    private static class ViewHolder {
       TextView placeName;
       TextView placeRating;
       TextView placeAddress;
       TextView placeOpenNow;
    }

    public PlacesAdapter(Context context, ArrayList<Place> places) {
        super(context, 0, places);
        this.places = places;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        Place place = places.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if(rowView == null){
            // If there's no view to re-use, inflate a brand new view for row
            LayoutInflater inflater = LayoutInflater.from(getContext());
            rowView = inflater.inflate(R.layout.places_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.placeName =  (TextView) rowView.findViewById(R.id.place_name);
            viewHolder.placeAddress = (TextView) rowView.findViewById(R.id.place_address);
            viewHolder.placeRating = (TextView) rowView.findViewById(R.id.place_rating);
            viewHolder.placeOpenNow = (TextView) rowView.findViewById(R.id.place_status);
            // Cache the viewHolder object inside the fresh view
            rowView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)rowView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.placeName.setText(place.getName());
        viewHolder.placeAddress.setText(place.getAddress());
        viewHolder.placeRating.setText(place.getRatingDescr());
        viewHolder.placeOpenNow.setText(place.getOpenNowDescr());

        return  rowView;
    }


}