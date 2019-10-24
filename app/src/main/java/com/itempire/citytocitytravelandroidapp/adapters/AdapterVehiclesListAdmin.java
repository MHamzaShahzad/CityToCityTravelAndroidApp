package com.itempire.citytocitytravelandroidapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.FragmentVehicleDescription;
import com.itempire.citytocitytravelandroidapp.models.Vehicle;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterVehiclesListAdmin extends RecyclerView.Adapter<AdapterVehiclesListAdmin.Holder> {
    Context context;
    List<Vehicle> list;
    private static Bundle bundle;

    public AdapterVehiclesListAdmin(Context context, List<Vehicle> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_vehicle_card_design, null);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        Vehicle vehicle = list.get(position);
        if (vehicle.getVehicleImage() != null)
            try {
                Picasso.get().load(vehicle.getVehicleImage()).placeholder(R.drawable.placeholder_photos).centerInside().fit().into(holder.vehicleImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        holder.vehicleNumberPlace.setText(vehicle.getVehicleNumber());
        holder.vehicleModelPlace.setText(vehicle.getVehicleModel());


        holder.cardRecyclerVehiclesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentVehicleDescription vehicleDescription = new FragmentVehicleDescription();
                bundle = new Bundle();
                bundle.putBoolean(Constant.VEHICLE_DESCRIPTION_ADMIN , true);
                bundle.putSerializable(Constant.VEHICLE_DESCRIPTION_NAME, list.get(holder.getAdapterPosition()));
                vehicleDescription.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, vehicleDescription).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        CardView cardRecyclerVehiclesList;
        ImageView vehicleImage;
        TextView vehicleModelPlace, vehicleNumberPlace;

        public Holder(@NonNull View itemView) {
            super(itemView);

            cardRecyclerVehiclesList = (CardView) itemView.findViewById(R.id.cardRecyclerVehiclesList);

            vehicleImage = (ImageView) itemView.findViewById(R.id.vehicleImage);

            vehicleModelPlace = (TextView) itemView.findViewById(R.id.vehicleModelPlace);
            vehicleNumberPlace = (TextView) itemView.findViewById(R.id.vehicleNumberPlace);

        }
    }
}
