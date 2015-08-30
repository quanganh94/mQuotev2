package com.example.image.mquotev2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.loopj.android.image.SmartImageView;

import java.util.List;

public class ImageResultsArrayAdapter extends ArrayAdapter<ImageResult> {

	public ImageResultsArrayAdapter(Context context, List<ImageResult> images) {
		super(context, R.layout.item_image_result, images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageResult imageInfo = this.getItem(position);

		if (convertView == null) {
            convertView = (RelativeLayout) LayoutInflater.from(getContext()).inflate(
                    R.layout.loading_image, parent, false);
		}
        SmartImageView ivImage = (SmartImageView) convertView.findViewById(R.id.img);

        if(imageInfo==null) ivImage.setImageResource(android.R.color.transparent);
        else {
            try{
                ivImage.setImageUrl(imageInfo.getFullUrl());
            } catch(Exception e){
                imageInfo = this.getItem(position+1);
                ivImage.setImageUrl(imageInfo.getFullUrl());
            }
        }
		return convertView;
	}

}
