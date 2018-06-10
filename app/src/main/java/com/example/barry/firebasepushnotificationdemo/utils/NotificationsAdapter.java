package com.example.barry.firebasepushnotificationdemo.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.barry.firebasepushnotificationdemo.R;
import com.example.barry.firebasepushnotificationdemo.models.Notifications;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notifications> mNotifList;

    private FirebaseFirestore mFirestore; //firebaseFirestore; //?Todo check: why not mFirestore we've been using?
    private Context context;

    //add a constructor
    public NotificationsAdapter(Context context, List<Notifications> mNotifList) {
        this.mNotifList = mNotifList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.single_notification, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        mFirestore = FirebaseFirestore.getInstance();

        String from_id = mNotifList.get(position).getFrom();

        holder.mNotifMessage.setText(mNotifList.get(position).getMessage());

        mFirestore.collection("Users").document(from_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String name = documentSnapshot.getString("name");
                        String image = documentSnapshot.getString("image");

                        holder.mNotifName.setText(name);

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.ic_profile_img); // replacing (R.mipmap.ic_launcher)

                        Glide.with(context).setDefaultRequestOptions(requestOptions)
                                .load(image)
                                .into(holder.mNotifImage);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return mNotifList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public CircleImageView mNotifImage;
        public TextView mNotifName;
        public TextView mNotifMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mNotifImage = mView.findViewById(R.id.notification_list_image);
            mNotifMessage = mView.findViewById(R.id.notification_list_msg);
            mNotifName = mView.findViewById(R.id.notification_list_name);
        }
    }
}
