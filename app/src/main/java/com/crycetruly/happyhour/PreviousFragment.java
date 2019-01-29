package com.crycetruly.happyhour;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crycetruly.happyhour.model.HappyHour;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreviousFragment extends Fragment {
    private RecyclerView mList;
    FirebaseRecyclerAdapter adapter;
    private ProgressBar mProgress;
    private FirebaseFirestore firebaseFirestore;

    public PreviousFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_previous, container, false);
        mProgress = v.findViewById(R.id.progress_bar);

        mList = v.findViewById(R.id.list);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        RelativeLayout relativeLayout=v.findViewById(R.id.main);
        Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("owneridd")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

query.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()){

            mProgress.setVisibility(View.INVISIBLE);


try {
    Snackbar.make(relativeLayout,"You have no happy hour history",Snackbar.LENGTH_LONG)

            .show();
}catch (IllegalArgumentException ee){

}
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});
        FirebaseRecyclerOptions<HappyHour> options =
                new FirebaseRecyclerOptions.Builder<HappyHour>()
                        .setQuery(query, HappyHour.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<HappyHour, HappyHourHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HappyHourHolder holder, int position, @NonNull HappyHour model) {
                holder.setDetail(model.getDescription());
                holder.setName(model.getStartTime() + " " + model.getEndTime());

                mProgress.setVisibility(View.INVISIBLE);

                holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Snackbar.make(relativeLayout,"Invalidate Offer",Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               try {
                                   adapter.getRef(position).removeValue();
                                   adapter.notifyDataSetChanged();
                                   Toast.makeText(getContext(), "Happy Hour Invalidated", Toast.LENGTH_SHORT).show();

                               }catch (Exception ee){
                                   Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                               }

                            }
                        }).show();
                        return true;
                    }
                });

            }

            @Override
            public HappyHourHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_happyhour_layout, parent, false);

                return new HappyHourHolder(view);
            }

        };

          mList.setAdapter(adapter);
        return v;
    }

    public static class HappyHourHolder extends RecyclerView.ViewHolder {
        private View view;

        public HappyHourHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setName(String name) {
            TextView textView = view.findViewById(R.id.name);
            textView.setText(name);
        }

        public void setDetail(String desc) {
            TextView textView = view.findViewById(R.id.desc);
            textView.setText(desc);
        }
    }

    @Override
    public void onStart() {
        adapter.startListening();
        super.onStart();
    }

    @Override
    public void onStop() {
        adapter.stopListening();
        super.onStop();
    }

}
