package com.itempire.citytocitytravelandroidapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseCurrentUserClass;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsersAndAdmins extends RecyclerView.Adapter<AdapterUsersAndAdmins.Holder> {

    private static final String TAG = AdapterUsersAndAdmins.class.getName();
    Context context;
    List<User> list;

    private static final String ACCOUNT_TYPE_USER_TEXT = "Remove Admin";
    private static final String ACCOUNT_TYPE_ADMIN_TEXT = "Make Admin";

    public AdapterUsersAndAdmins(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_users_admins, null);
        return new AdapterUsersAndAdmins.Holder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        final User user = list.get(position);

        if (user.getUserImageUrl() != null)
            try {
                Picasso.get().load(user.getUserImageUrl()).placeholder(R.drawable.user_avatar).centerInside().fit().into(holder.profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        holder.name.setText(user.getUserName());
        holder.phoneNumber.setText(user.getUserPhoneNumber());

        if (user.getUserType().equals(Constant.USER_TYPE_NON_ADMIN)) {
            holder.btnToggleAdminUser.setText(ACCOUNT_TYPE_ADMIN_TEXT);
        }
        if (user.getUserType().equals(Constant.USER_TYPE_ADMIN)) {
            holder.btnToggleAdminUser.setText(ACCOUNT_TYPE_USER_TEXT);
        }

        holder.btnToggleAdminUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User clickedUser = list.get(holder.getAdapterPosition());

                if (clickedUser.getUserType().equals(Constant.USER_TYPE_NON_ADMIN)) {
                    changeAccountType(clickedUser, Constant.USER_TYPE_ADMIN);
                }
                if (clickedUser.getUserType().equals(Constant.USER_TYPE_ADMIN)) {
                    changeAccountType(clickedUser, Constant.USER_TYPE_NON_ADMIN);
                }

            }
        });

        if (user.getUserId() != null)
            if (user.getUserId().equals(MyFirebaseCurrentUserClass.mUser.getUid()))
                holder.btnToggleAdminUser.setVisibility(View.GONE);
            else
                holder.btnToggleAdminUser.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView name, phoneNumber;
        Button btnToggleAdminUser;

        public Holder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);

            name = itemView.findViewById(R.id.name);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);

            btnToggleAdminUser = itemView.findViewById(R.id.btnToggleAdminUser);

        }
    }

    private void changeAccountType(User user, String type) {

        MyFirebaseDatabaseClass.USERS_PROFILE_REFERENCE.child(user.getUserId()).child("userType").setValue(type);

    }

}
