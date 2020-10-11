package com.app.tracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 123;
    RecyclerView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main_temp);
        // startService();
        listView = findViewById(R.id.list);
        listView.setAdapter(new Adapter(Preference.get(this).getStats()));
        //getToken();
        if(!Preference.get(this).isSendToken()){
            getToken();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (!isGranted(requestCode, grantResults)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                } else {
                    Util.showPermissionAlert(this, R.string.location_alert);
                }
            } else {
                startService();
            }
        }
    }

    private boolean isGranted(int requestCode, int[] grantResults) {
        return requestCode == REQUEST_CODE && grantResults != null && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private void startService() {
        if (TrackerService.getService() == null) {
            if (checkForPermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, new Intent(this, TrackerService.class));
                } else {
                    startService(new Intent(this, TrackerService.class));
                }
            }
        }
    }

    private boolean checkForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Util.isPermissionGranted(this, Util.requiredPermissions())) {
                return true;
            }
            requestPermissions(Util.requiredPermissions(), REQUEST_CODE);
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                listView.setAdapter(new Adapter(Preference.get(this).getStats()));
                break;
        }
        return true;
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Tracker", "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Token "+token);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                    Preference.get(this).setSendToken();
                    // Log and toast
                    //  String msg = getString(R.string.msg_token_fmt, token);
                    Log.d("Tracker", token);
                });
    }

}
