package com.itempire.citytocitytravelandroidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.models.Post;

import java.util.List;

public class AdapterAllPosts extends RecyclerView.Adapter<AdapterAllPosts.Holder> {

    Context context;
    List<Post> list;

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
    public void onBindViewHolder(@NonNull AdapterAllPosts.Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }

    }
}
