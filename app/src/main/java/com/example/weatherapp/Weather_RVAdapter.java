package com.example.weatherapp;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Weather_RVAdapter extends RecyclerView.Adapter<Weather_RVAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Weather_RVModal> weatherRvModalArrayList;

    public Weather_RVAdapter(Context context, ArrayList<Weather_RVModal> weatherRvModalArrayList) {
        this.context = context;
        this.weatherRvModalArrayList = weatherRvModalArrayList;
    }

    @NonNull
    @Override
    public Weather_RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item1,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Weather_RVAdapter.ViewHolder holder, int position) {

        Weather_RVModal modal = weatherRvModalArrayList.get(position);
        holder.tempTv.setText(modal.getTemp()+"°c");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conTv);
        holder.windTv.setText(modal.getWindSpeed()+"Km/h");
        SimpleDateFormat input =  new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat output =  new SimpleDateFormat("hh:mm aa");

        try{
            Date t = input.parse(modal.getTime());
            holder.TimeTv.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherRvModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windTv,tempTv,TimeTv;
        private ImageView conTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTv = itemView.findViewById(R.id.idWindSpeed);
            tempTv = itemView.findViewById(R.id.idTemp);
            TimeTv= itemView.findViewById(R.id.idTime);
            conTv = itemView.findViewById(R.id.idCondition);
        }
    }
}
