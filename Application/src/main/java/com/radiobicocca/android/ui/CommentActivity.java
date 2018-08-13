package com.radiobicocca.android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.radiobicocca.android.Adapter.EndlessRecyclerViewScrollListener;
import com.radiobicocca.android.Adapter.MessageListAdapter;
import com.radiobicocca.android.Common.ConnectionDetector;
import com.radiobicocca.android.Common.Utils;
import com.radiobicocca.android.Interface.CommentService;
import com.radiobicocca.android.Model.Message;
import com.radiobicocca.android.Model.WPComment;
import com.radiobicocca.android.R;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.Html.fromHtml;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "COMMENT ACTVVITY";
    private static final String SPLITTER = "___eofeofeof___"; //Concatenato con un uuid per rendere unico il messaggio
    //Serve a evitare errore 409 = Conflict

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private List<Message> messageList = new ArrayList<Message>();
    private CommentService mService;
    private ImageButton sendButton;
    private TextView textView;
    private ProgressBar progressBar;
    private ProgressBar sendProgressBar;
    private TextView noMessagesTextView;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;


    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
            startActivityForResult(intent, 0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle(getTitle());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Access a Cloud Firestore instance
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progress_bar);
        sendProgressBar = findViewById(R.id.sendProgressBar);
        noMessagesTextView = findViewById(R.id.err_no_messages);
//        mService = Common.getCommentService();

        mMessageRecycler = findViewById(R.id.message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(linearLayoutManager);

        mMessageRecycler.setAdapter(mMessageAdapter);

        mMessageRecycler.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        mMessageRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mMessageRecycler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMessageRecycler.smoothScrollToPosition(
                                    mMessageRecycler.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

//        loadComment();


        loadFirestoreMessages();


        textView = findViewById(R.id.edittext_chatbox);

        sendButton = findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = textView.getText().toString().trim();
                if(message.equals("") || message == null)
                    return;

                FirebaseUser user = mAuth.getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                    startActivity(intent);
                }
                else {
                    String username = user.getDisplayName();


                    sendProgressBar.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);


                    Date d = new Date();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM HH:mm");

                    Map<String, Object> msg = new HashMap<>();
                    msg.put("uid", user.getUid());
                    msg.put("username", username);
                    msg.put("date", d);
                    msg.put("text", message);

                    db.collection("messaggi-diretta").document(df.format(d) + " " + username + ": " + message)
                            .set(msg)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    textView.setText("");
                                    sendProgressBar.setVisibility(View.GONE);
                                    sendButton.setVisibility(View.VISIBLE);
                                    loadFirestoreMessages();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                }
            }
        });

//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String message = textView.getText().toString().trim();
//                message = StringEscapeUtils.escapeJava(message);
//                if(message.equals("") || message == null)
//                    return;
//
//                message += SPLITTER + UUID.randomUUID().toString();
//                textView.setText("");
//
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                String username = prefs.getString(getString(R.string.key_username), "");
//
//                username = StringEscapeUtils.escapeJava(username);
//
//                if(username.equals(getBaseContext().getString(R.string.default_username))){
//                    // Fai cambiare l'username di default
//                    changeUsernameAlert();
//                    return;
//                }
//                    sendProgressBar.setVisibility(View.VISIBLE);
//                    sendButton.setVisibility(View.GONE);
//
//                    mService.postComment(username, "mail@mail.com",
//                            message.trim(), 39498, "password").enqueue(new Callback<WPComment>() {
//                        @Override
//                        public void onResponse(Call<WPComment> call, Response<WPComment> response) {
//                            Log.d(TAG, response.toString());
//                            if (response.code() == 201)
//                                loadComment();
//                            else {
//                                Bundle params = new Bundle();
//                                params.putString("Error", "response: " + response.toString());
//                                mFirebaseAnalytics.logEvent("error_send_messages", params);
//                                alert(response.message() + "\nCode: " + response.code());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<WPComment> call, Throwable t) {
//
//                        }
//                    });
//            }
//        });
    }

    private void changeUsername(){
//        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        alertDialog.setTitle(getString(R.string.title_alert_change_default_username));
//        alertDialog.setMessage(getString(R.string.message_alert_change_default_username));
//        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
//                        startActivity(intent);
//                        dialog.dismiss();
//                    }
//                });
//        alertDialog.show();
    }

    private void alert(String report){
        //TODO modificare le stringhe
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Errore");
        alertDialog.setMessage("C'è qualcosa che non va. Questo errore non dipende dall'applicazione, riprova più tardi.\n\nMessaggio: " + report);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void loadFirestoreMessages(){
        db.collection("messaggi-diretta").orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            messageList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String user = StringEscapeUtils.unescapeJava((String) document.get("username"));
                                String msg = (String) document.get("text");
                                Date date = (Date) document.get("date");
                                String uid = (String) document.get("uid");

                                Message m = new Message(uid, user, msg, date);
                                messageList.add(m);
                            }

                            mMessageAdapter.notifyDataSetChanged();

                            if(task.getResult().size() != 0) {
                                noMessagesTextView.setVisibility(View.GONE);
                                mMessageRecycler.scrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1);
                                mMessageRecycler.setVisibility(View.VISIBLE);
                            }
                            else{
                                noMessagesTextView.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void loadComment(){
        if(ConnectionDetector.isConnected(this)) {

            mService.getComments(39498, "password").enqueue(new Callback<List<WPComment>>() {
                @Override
                public void onResponse(Call<List<WPComment>> call, Response<List<WPComment>> response) {
                    Log.d(TAG, response.toString());
                    if(response.code() == 200) {
                        messageList.clear();

                        for (int i = 0; i < response.body().size(); i++) {

                            WPComment comment = response.body().get(i);

                            String content = StringEscapeUtils.unescapeJava(fromHtml(comment.getContent().getRendered()).toString());
                            Log.d(TAG, content);
                            content = content.split(SPLITTER)[0];
                            Log.d(TAG, content);
                            comment.setAuthorName(StringEscapeUtils.unescapeJava(comment.getAuthorName()));
                            comment.getContent().setRendered(content.trim());
                            comment.setDate(Utils.cleanStringDate(comment.getDate()));

//                            messageList.add(comment);
                        }

                        mMessageAdapter.notifyDataSetChanged();
                        if(response.body().size() != 0) {
                            noMessagesTextView.setVisibility(View.GONE);
                            mMessageRecycler.scrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1);
                            mMessageRecycler.setVisibility(View.VISIBLE);
                        }
                        else{
                            noMessagesTextView.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        Bundle params = new Bundle();
                        params.putString("Error", "response: " + response.toString());
                        mFirebaseAnalytics.logEvent("error_load_messages", params);
                        alert(response.message() + "\nCode: " + response.code());
                    }
                    progressBar.setVisibility(View.GONE);
                    sendProgressBar.setVisibility(View.GONE);
                    sendButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<List<WPComment>> call, Throwable t) {

                }
            });
        }
        else{
            ConnectionDetector.showNoConnectionError(this);
        }
    }

}
