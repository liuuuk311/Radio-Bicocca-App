package com.radiobicocca.android.ui;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.radiobicocca.android.Model.Message;
import com.radiobicocca.android.R;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgrammiActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgrammiAdapter adapter;

    private static String TAG = "PALINSESTO";

    private List<ProgrammiAdapter.Programma> palinsestoList = new ArrayList<ProgrammiAdapter.Programma>();
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmi);

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

        recyclerView = findViewById(R.id.programmi_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ProgrammiAdapter(this, palinsestoList);
        recyclerView.setAdapter(adapter);

        loadPalinsesto();



    }

    private void loadPalinsesto() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        db.collection("palinsesto").orderBy("ordine", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            palinsestoList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String name = (String) document.getString("nome");
                                String hosts = (String) document.getString("desc");
                                String day = (String) document.getString("giorno");
                                String time = (String) document.getString("ora");
                                ProgrammiAdapter.Programma p = new ProgrammiAdapter.Programma(name,
                                hosts, day, time, "", "");
                                palinsestoList.add(p);
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
