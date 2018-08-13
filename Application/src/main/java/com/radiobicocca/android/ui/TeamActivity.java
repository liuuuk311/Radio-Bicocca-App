package com.radiobicocca.android.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.radiobicocca.android.Adapter.ProgrammiAdapter;
import com.radiobicocca.android.Adapter.TeamAdapter;
import com.radiobicocca.android.R;

import java.util.ArrayList;
import java.util.List;

public class TeamActivity extends AppCompatActivity {
    private static final String TAG ="TEAM";
    private RecyclerView recyclerView;
    private TeamAdapter adapter;
    private FirebaseFirestore db;

    private ProgressBar progressBar;

    private List<TeamAdapter.Person> teamList = new ArrayList<TeamAdapter.Person>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle(getTitle());

        recyclerView = findViewById(R.id.team_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TeamAdapter(this, teamList);
        recyclerView.setAdapter(adapter);

        loadTeam();
    }

    private void loadTeam() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        db.collection("team")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            teamList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String name = (String) document.getString("name");
                                String role = (String) document.getString("role");
                                String desc = (String) document.getString("desc");
                                String imgUrl = (String) document.getString("picture");
                                Log.d(TAG, imgUrl);
                                TeamAdapter.Person p = new TeamAdapter.Person(name, desc, imgUrl, role);
                                teamList.add(p);
                            }

                            adapter.notifyDataSetChanged();

                            if(task.getResult().size() != 0) {
//                                noMessagesTextView.setVisibility(View.GONE);
//                                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            else{
//                                noMessagesTextView.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
