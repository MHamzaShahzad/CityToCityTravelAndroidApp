package com.itempire.citytocitytravelandroidapp.adapters;

import android.content.Context;
import android.media.Image;
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
import com.itempire.citytocitytravelandroidapp.FragmentPostDescription;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAllPosts extends RecyclerView.Adapter<AdapterAllPosts.Holder> {

    Context context;
    List<Post> list;
    private static Bundle bundle;

    public AdapterAllPosts(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterAllPosts.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_posts_card_design, null);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterAllPosts.Holder holder, int position) {

        final Post post = list.get(position);
        try {

            if (post.getVehicleImage() != null)
                Picasso.get().load(post.getVehicleImage()).into(holder.image_post_recycler);

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.post_data.setText(
                post.getProviderFirebaseUId() + "\n" +
                        post.getTotalNoOfSeatsAvailable() + "\n" +
                        post.getArrivalLocation() + "\n" +
                        post.getDepartureCity() + "\n" +
                        post.getDepartureLocation() + "\n" +
                        post.getEstimatedArrivalTime()
        );

        holder.card_post_recycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentPostDescription postDescription = new FragmentPostDescription(context);
                bundle = new Bundle();
                bundle.putSerializable(Constant.POST_OBJECT_DESCRIPTION, list.get(holder.getAdapterPosition()));
                postDescription.setArguments(bundle);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, postDescription).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        CardView card_post_recycler;
        ImageView image_post_recycler;
        TextView post_data;

        public Holder(@NonNull View itemView) {
            super(itemView);

            card_post_recycler = itemView.findViewById(R.id.card_post_recycler);
            image_post_recycler = itemView.findViewById(R.id.image_post_recycler);
            post_data = itemView.findViewById(R.id.post_data);
        }

    }
}
