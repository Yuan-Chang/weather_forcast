package com.dabkick.myforcast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by developer3 on 12/19/15.
 */
public class ListviewAdapter extends BaseAdapter {

    ArrayList<ForcastData> list;
    Context c;

    public ListviewAdapter(Context c,ArrayList<ForcastData> data) {
        list = data;
        this.c = c;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ForcastData getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView;

        if (i == 0)
        {
            rowView = LayoutInflater.from(c).inflate(R.layout.current_row, null);
            TextView value1 = (TextView)rowView.findViewById(R.id.value1);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView summary = (TextView) rowView.findViewById(R.id.value2);
            value1.setText(getItem(i).temparature);
            title.setText(getItem(i).title);
            summary.setText(getItem(i).summary);
        }
        else {
            rowView = LayoutInflater.from(c).inflate(R.layout.listview_row, null);
            TextView title = (TextView)rowView.findViewById(R.id.title);
            TextView value1 = (TextView)rowView.findViewById(R.id.value1);
            TextView value2 = (TextView)rowView.findViewById(R.id.value2);

            title.setText(getItem(i).title);
            value1.setText(getItem(i).max_temparature);
            value2.setText(getItem(i).min_temparature);
        }
        return rowView;
    }
}
