package com.example.barry.firebasepushnotificationdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.barry.firebasepushnotificationdemo.R;
import com.example.barry.firebasepushnotificationdemo.SendActivity;
import com.example.barry.firebasepushnotificationdemo.models.Users;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 1. create a var List<Users> usersList;
 * 2. set the page to the list
 * 3. return all items on the list in getItemCount
 * 4. create a single list view for the userRecycler
 *      - set the height to wrap_content otherwise it will take the whole page
 * 5. Infaate the views in the ViewHolder onCreateHolder
 * 6. Init the views in ViewHolder class
 * 7. In onBindViewHolder, Get the values from the list and set the holder to display the views
 *
 * 8. Now go to UsersFragment and init things
 *
 * =======================================
 * To get the user name to send notification
 *      get the user name in the string
 *      add it to the intent
 *      define string mUserName, init it in the SendActivity
 *      and retrieve it in the onCreate
 */
public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder> {

    private List<Users> usersList;
    private Context context;

    // set this page to the list
    public  UsersRecyclerAdapter(Context context, List<Users> usersList) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // string to store the user name
        final String user_name = usersList.get(position).getName();

        holder.user_name_view.setText(user_name); // UNUNUNUN USER NAME
        // set the user name to the holder (can also hold user id), NEXT put as extra to the intent

        // again we use glide to get our image
        CircleImageView user_image_view = holder.user_image_view;
        Glide.with(context).load(usersList.get(position).getImage()) // load the image
                .into(user_image_view); // into user image view
        // Now go to UsersFragment and init things

        //UIDUIDUIDUIDUID
        final String user_id = usersList.get(position).userId;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // send user to the send activity
                Intent sendIntent = new Intent(context, SendActivity.class);
                sendIntent.putExtra("user_id", user_id); // put the user_id, then go get this user_id in sendActivity()
                sendIntent.putExtra("user_name", user_name); // put the user name // UNUNUNUN USER NAME, go get it in SendActivity
                context.startActivity(sendIntent); // context,startActivity() since working with a fragment
            }
        });
        //UIDUIDUIDUIDUID
    }

    @Override
    public int getItemCount() {
        return usersList.size(); // return all items on the list
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // create the views
        private View mView;

        private CircleImageView user_image_view;
        private TextView user_name_view;


        public ViewHolder(View itemView) {
            super(itemView);

            // init the views
            mView = itemView;

            user_image_view = mView.findViewById(R.id.user_list_image);
            user_name_view = mView.findViewById(R.id.user_list_name);
        }
    }
}
