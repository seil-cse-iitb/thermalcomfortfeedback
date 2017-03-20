package com.example.sid.testu;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sid on 5/1/17.
 */

public class CustomAdapter extends ArrayAdapter<String> {


    private final Activity context;
    private static LayoutInflater inflater=null;
    private final List<String> feeling , preferred , time;

    public CustomAdapter(Activity context, List<String> feeling , List<String> preferred , List<String> time) {
        super(context, R.layout.list_item,feeling);
        this.context = context;


        this.feeling = feeling;
        this.preferred = preferred;
        this.time = time;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item, null, true);

        TextView feelingTV = (TextView) rowView.findViewById(R.id.feeling_list_tv);
        feelingTV.setText(this.feeling.get(position));

        TextView preferredTV = (TextView) rowView.findViewById(R.id.preferred_list_tv);
        preferredTV.setText(this.preferred.get(position));


        TextView timeTV = (TextView) rowView.findViewById(R.id.time_list_tv);
        timeTV.setText(this.time.get(position));


        return rowView;






    }
}
