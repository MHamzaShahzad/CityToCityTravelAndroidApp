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

import com.itempire.citytocitytravelandroidapp.CommonFeaturesClass;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.user.FragmentMyPostsDescription;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMyPosts extends RecyclerView.Adapter<AdapterMyPosts.Holder> {

    Context context;
    List<Post> list;
    private static Bundle bundle;
    String category;

    public AdapterMyPosts(Context context, List<Post> list, String category) {
        this.context = context;
        this.list = list;
        this.category = category;
    }

    @NonNull
    @Override
    public AdapterMyPosts.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_posts_card_design, null);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterMyPosts.Holder holder, int position) {

        final Post post = list.get(position);
        CommonFeaturesClass.loadPostImage(holder.image_post_recycler, post);

        holder.place_departure_date.setText( post.getDepartureTime() + "\n" +post.getDepartureDate());
        holder.place_total_seats_available.setText(post.getTotalNoOfSeatsAvailable());
        holder.place_post_date.setText(post.getPostTime());
        holder.place_post_cost_per_seat.setText(post.getCostPerHead());

        holder.card_post_recycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMyPostsDescription postDescription = new FragmentMyPostsDescription();
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
        TextView place_post_cost_per_seat ,place_post_date, place_total_seats_available, place_departure_date;

        public Holder(@NonNull View itemView) {
            super(itemView);

            card_post_recycler = itemView.findViewById(R.id.card_post_recycler);
            image_post_recycler = itemView.findViewById(R.id.image_post_recycler);

            place_post_cost_per_seat = itemView.findViewById(R.id.place_post_cost_per_seat);
            place_post_date = itemView.findViewById(R.id.place_post_date);
            place_total_seats_available = itemView.findViewById(R.id.place_total_seats_available);
            place_departure_date = itemView.findViewById(R.id.place_departure_date);
        }

    }
}
