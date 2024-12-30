package com.anniezhang.textfromimage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private ArrayList<Upload> mList; // List of uploads to display
    private Context context; // Context for accessing resources and layout inflater

    // Constructor to initialize context and list of uploads
    public ImageAdapter(Context context , ArrayList<Upload> mList){
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout and create a ViewHolder
        View v = LayoutInflater.from(context).inflate(R.layout.activity_image_item , parent ,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind data to the ViewHolder
        holder.textView.setText(mList.get(position).getImageName()); // Set image name
        holder.date.append(mList.get(position).getDate()); // Append date to date TextView
        holder.confidence.append(mList.get(position).getConfidence()); // Append confidence level
        Glide.with(context).load(mList.get(position).getImageUrl()).into(holder.imageView); // Load image using Glide
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list
        return mList.size();
    }

    // ViewHolder class to hold item views
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView; // Image view for displaying the image
        TextView textView;  // Text view for displaying the image name
        TextView date;      // Text view for displaying the upload date
        TextView confidence; // Text view for displaying the confidence level

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views using their IDs
            imageView = itemView.findViewById(R.id.image_view_upload);
            textView = itemView.findViewById(R.id.text_view_name);
            date = itemView.findViewById(R.id.tv_date);
            confidence = itemView.findViewById(R.id.tv_confidence);
        }
    }
}
