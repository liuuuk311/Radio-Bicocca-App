package com.radiobicocca.android.ui;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.radiobicocca.android.AppCompatPreferenceActivity;
import com.radiobicocca.android.Firebase.MyFirebaseMessagingService;
import com.radiobicocca.android.R;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();


    private static FirebaseAuth mAuth;
    private static FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        // Show the Up button in the action bar.
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        int easterEggCount;
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            easterEggCount = 0;

            // gallery EditText change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_username)));

            // notification preference change listener
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.notifications_new_message)));


            Preference loginPref = findPreference(getString(R.string.key_username));
            loginPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null){

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Sei sicuro di volere effettuare il logout?")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // FIRE ZE MISSILES!
                                    }
                                })
                                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                        // Create the AlertDialog object and return it
                        builder.create();

                        Log.d(TAG, "Logged out");
//                        user.delete()
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Log.d(TAG, "User account deleted.");
//                                        }
//                                    }
//                                });
                        LoginManager.getInstance().logOut();
                        FirebaseAuth.getInstance().signOut();
                        preference.setSummary(R.string.summary_username);
                    }
                    else{
                        Intent intent = new Intent(preference.getContext(), ProfileActivity.class);
                        startActivityForResult(intent, 0);
                    }
                    return false;
                }
            });


            Preference switchNotification = findPreference(getString(R.string.notifications_new_message));
            switchNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final boolean enable = (boolean) newValue;
                    Log.d("SETTINGS", "bool " + enable);
                    if(!enable){
                        PackageManager pm  = preference.getContext().getPackageManager();
                        ComponentName componentName = new ComponentName(preference.getContext(), MyFirebaseMessagingService.class);
                        pm.setComponentEnabledSetting(componentName,
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);
                    }else {
                        PackageManager pm  = preference.getContext().getPackageManager();
                        ComponentName componentName = new ComponentName(preference.getContext(), MyFirebaseMessagingService.class);
                        pm.setComponentEnabledSetting(componentName,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                    }

                    return true;
                }
            });

            // feedback preference click listener
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

            Preference myEasterPref = findPreference(getString(R.string.key_dev));
            myEasterPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Bundle params = new Bundle();
                    params.putString("dev_name", "Dev name Tapped");
                    mFirebaseAnalytics.logEvent("tap_dev_name", params);
                    if(easterEggCount < 5){
                        easterEggCount++;
                    }
                    else{
                        Bundle params2 = new Bundle();
                        params2.putString("easter_egg", "View easter egg");
                        mFirebaseAnalytics.logEvent("easter_egg_opened", params2);
                        easterEggCount = 0;
//                        Toast.makeText(preference.getContext(), "Easter Egg!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(preference.getContext(), EasterEggActivity.class);
                        startActivity(intent);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            Log.d("SETTINGS", stringValue);

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

//            } else if (preference instanceof SwitchPreference) {
//

            } else if (preference instanceof Preference) {
                if (preference.getKey().equals("key_username")) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user == null){
                        preference.setSummary(R.string.summary_username);
                    }
                    else{
                        String show = preference.getContext().getString(R.string.summary_username2) + " " +
                                user.getDisplayName();
                        preference.setSummary(show);
                    }
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPer favore non cancellare queste informazioni.\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"radiobicocca.backend@gmail.com"}); // inserire la mail
        intent.putExtra(Intent.EXTRA_SUBJECT, "App Radio Bicocca - Feedback utente");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}
