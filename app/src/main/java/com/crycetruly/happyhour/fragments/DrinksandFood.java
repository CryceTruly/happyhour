package com.crycetruly.happyhour.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crycetruly.happyhour.R;
import com.crycetruly.happyhour.adapters.AdapterListAnimation;
import com.crycetruly.happyhour.model.HappyHour;
import com.crycetruly.happyhour.utils.GetCurTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elia on 17/06/2018.
 */

public class DrinksandFood extends Fragment {
    private List<HappyHour> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private static final String TAG = "DrinksandFood";
    FirestoreRecyclerAdapter adapter;
    public DrinksandFood() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_happy_hour, container, false);
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        progressBar=view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Query query = FirebaseFirestore.getInstance()
                .collection("posts").whereEqualTo("showsIn", "Western Region");
//                .whereGreaterThanOrEqualTo("sortDate", 20180626);;

                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
try {
    if (!documentSnapshots.isEmpty())
        Log.d(TAG, "onEvent: "+documentSnapshots.getDocuments().size());
}catch (NullPointerException e1){

}
                    }
                });

        FirestoreRecyclerOptions<HappyHour> options = new FirestoreRecyclerOptions.Builder<HappyHour>()
                .setQuery(query, HappyHour.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<HappyHour, ViewHolder>(options) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position, HappyHour model) {
                // Bind the Chat object to the ChatHolder
                // ...


                holder.name.setText(model.getTitle());

                holder.dsc.setText(model.getDescription());
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_happyhour_layout, group, false);

                return new ViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name,dsc;
        public View lyt_parent;

        public ViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);

            dsc=v.findViewById(R.id.desc);
        }
    }
}
