package com.itempire.citytocitytravelandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.models.OffersCategorise;

import java.util.List;

public class AdapterCategoriesList extends RecyclerView.Adapter<AdapterCategoriesList.Holder> {

    Context context;
    List<OffersCategorise> list;
    Intent intent;

    public AdapterCategoriesList(Context context, List<OffersCategorise> list, String action) {
        this.context = context;
        this.list = list;
        intent = new Intent(action);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_categorise_recycler, null);
        return new AdapterCategoriesList.Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        OffersCategorise category = list.get(position);
        holder.text_category_type_name.setText(category.getOfferTypeName());

        holder.card_category_type_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(Constant.OFFER_OR_POST_STATUS, list.get(holder.getAdapterPosition()).getOfferTypeId());
                context.sendBroadcast(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CardView card_category_type_name;
        TextView text_category_type_name;
        public Holder(@NonNull View itemView) {
            super(itemView);
            card_category_type_name = itemView.findViewById(R.id.card_category_type_name);
            text_category_type_name = itemView.findViewById(R.id.text_category_type_name);
        }
    }
}
