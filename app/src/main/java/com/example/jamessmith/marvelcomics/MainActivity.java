package com.example.jamessmith.marvelcomics;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.jamessmith.marvelcomics.comics.ComicsFragment;
import com.example.jamessmith.marvelcomics.description.ComicDescription;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isStoragePermissionGranted()){
            Toast toast = Toast.makeText(this,"Viewing of images is not available. To view images please grant permission.",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        IntentFilter intentFilter = new IntentFilter("updateMain");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null){
                    swapFragment(1, intent.getIntExtra("indexValue", 0));
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        swapFragment(0, 0);
    }

    private void swapFragment(int i, int index){
        Fragment fragment;
        Bundle bundle = new Bundle();

        switch(i){
            case 0:
                fragment = new ComicsFragment();
                break;
            case 1:
                fragment = new ComicDescription();
                bundle.putInt("index", index);
                fragment.setArguments(bundle);
                fragment.setRetainInstance(true);
                break;
            default:
                fragment = new ComicsFragment();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, fragment, null).addToBackStack(null).commit();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission has been granted");
                return true;
            } else {

                Log.v(TAG,"Permission has been revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //Automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission has been granted");
            return true;
        }
    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try{
            unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try{
            unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }
}
