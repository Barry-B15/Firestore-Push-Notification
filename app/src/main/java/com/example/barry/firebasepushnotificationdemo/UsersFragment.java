package com.example.barry.firebasepushnotificationdemo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.barry.firebasepushnotificationdemo.models.Users;
import com.example.barry.firebasepushnotificationdemo.utils.UsersRecyclerAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */

/**
 * 1 add a recyclerview to users.xml
 * 2. create a recycler var for the users here
 * 3. Create a model class Users for the users
 * 4. create an adapter for Users recycler
 * 5. Here create a "List<Users) usersList" for the model class we just created
 *      and init it so data can be stored in it
 *      create same List in the Recycler adapter
 *
 * 6. back from UsersAdapter
 *      - start by initializing the UsersRecyclerAdapter
 *
 * 7. get fb to display things
 * 8. Override onStart()
 *
 * 9. To add OnClick Listener to our user item we need a user id
 *      - define the user_id in onStart()
 *      - CREATE a new model class UserId(), have ie extend the UserId
 *      - Have the Users class extend the UserId
 *      - append .withId(user_id) to Users users = .... in onStart() here
 *      - In UsersRecyclerAdapter onBindViewHolder(),
 *              define the String user_id
 *              and set the onClick listener to it
 *      - Create a new SendActivity class to handle the send notifications
 *      - Add an intent in the onClick listener def above to send users to the sendActivity()
 * RUN
 */
public class UsersFragment extends Fragment {

    private RecyclerView mUsersListView;

    private List<Users> usersList; // 5 above

    private UsersRecyclerAdapter usersRecyclerAdapter;

    private FirebaseFirestore mFirestore;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        mUsersListView = view.findViewById(R.id.users_list_view); // not sure if this is the correct ref
        usersList = new ArrayList<>();

        usersRecyclerAdapter = new UsersRecyclerAdapter(container.getContext(), usersList);

        mUsersListView.setHasFixedSize(true);
        mUsersListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mUsersListView.setAdapter(usersRecyclerAdapter); // set the adapter to the recycler view


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // RRRRRRRRR FIX: Recycler doubling on Resume RRRRRRRRRRRRRRR
        // I was getting items in the recycler doubling when I click back from the send activity
        // or resume from pause. With this line of code added, it clears old data
        // and load new when I return. I WAS RIGHT, TEACHER SHOWED THIS LATER IN THE VIDEO

        usersList.clear();
        // RRRRRRRRR FIX: Recycler doubling on Resume RRRRRRRRRRRRRRR

        mFirestore.collection("Users").addSnapshotListener(getActivity(), // add getActivity() fix for signout crash
                new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        //UIDUIDUIDUIDUID create a user id
                        String user_id = doc.getDocument().getId();
                        //UIDUIDUIDUIDUID

                        Users users = doc.getDocument().toObject(Users.class)
                                .withId(user_id);   //UIDUIDUIDUIDUID append the user_id
                        usersList.add(users);
                       // usersList.clear();

                        usersRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

}
