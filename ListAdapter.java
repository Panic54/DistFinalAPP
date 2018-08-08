package com.example.madsfinnerup.distfinalapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madsfinnerup on 01/08/2018.
 */

public class ListAdapter extends ArrayAdapter<ItemsForSale> {

    private Context mContext;
    private int mResource;

    public ListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ItemsForSale> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).personName;
                String itemName = getItem(position).itemName;
                        String price = getItem(position).price;

                        ItemsForSale itemsForSale = new ItemsForSale(name,itemName,price);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);
        TextView textName = (TextView) convertView.findViewById(R.id.textView1);
        TextView textItemName = (TextView) convertView.findViewById(R.id.textView2);
        TextView textPrice = (TextView) convertView.findViewById(R.id.textView3);

        textName.setText(name);
        textItemName.setText(itemName);
        textPrice.setText(price);
        return convertView;
    }
}
