package com.itempire.citytocitytravelandroidapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentPostDescription;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.AvailOffer;
import com.itempire.citytocitytravelandroidapp.models.Post;
import com.itempire.citytocitytravelandroidapp.user.FragmentRequestPostDescription;

import java.util.List;

public class AdapterMyRequests extends RecyclerView.Adapter<AdapterMyRequests.Holder> {

    List<AvailOffer> list;
    Context context;
    String selectedCat;
    private static Bundle bundle;

    public AdapterMyRequests(Context context, List<AvailOffer> list, String selectedCat) {
        this.list = list;
        this.context = context;
        this.selectedCat = selectedCat;
    }

    @NonNull
    @Override
    public AdapterMyRequests.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_offers_recycler, null);
        return new AdapterMyRequests.Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterMyRequests.Holder holder, int position) {

        AvailOffer offer = list.get(position);

        holder.place_offers_seats.setText(offer.getNumberOfSeats());
        holder.place_offers_message.setText(offer.getMessage());


        getPostFromId(list.get(holder.getAdapterPosition()).getPostId(), holder);




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        CardView card_offers_list;
        TextView place_offers_date, place_offers_time, place_offers_arrival_city,
                place_offers_seats, place_offers_message;

        public Holder(@NonNull View itemView) {
            super(itemView);

            card_offers_list = itemView.findViewById(R.id.card_offers_list);

            place_offers_date = itemView.findViewById(R.id.place_offers_date);
            place_offers_time = itemView.findViewById(R.id.place_offers_time);
            place_offers_arrival_city = itemView.findViewById(R.id.place_offers_arrival_city);
            place_offers_seats = itemView.findViewById(R.id.place_offers_seats);
            place_offers_message = itemView.findViewById(R.id.place_offers_message);

        }
    }

    private void getPostFromId(final String postId, final Holder holder) {
        MyFirebaseDatabaseClass.POSTS_REFERENCE.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    try {
                        final Post post = dataSnapshot.getValue(Post.class);
                        if (post != null) {

                            holder.place_offers_arrival_city.setText(post.getArrivalCity());
                            holder.place_offers_date.setText(post.getDepartureDate());
                            holder.place_offers_time.setText(post.getDepartureTime());

                            holder.card_offers_list.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    FragmentRequestPostDescription postDescription = new FragmentRequestPostDescription();
                                    bundle = new Bundle();
                                    bundle.putString(Constant.OFFER_OR_POST_STATUS, selectedCat);
                                    bundle.putSerializable(Constant.POST_OBJECT_DESCRIPTION, post);
                                    postDescription.setArguments(bundle);
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, postDescription).addToBackStack(null).commit();

                                }
                            });

                        } else
                            Toast.makeText(context, "Something went wrong please try again!", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
