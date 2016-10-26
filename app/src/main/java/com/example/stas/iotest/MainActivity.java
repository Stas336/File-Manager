package com.example.stas.iotest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import junit.framework.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    File file;
    String filename = "myfile";
    String string = "Hello world!";
    FileOutputStream outputStream;
    Toast toast;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestPermission();
        /*String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            toast = Toast.makeText(getApplicationContext(),
                    "External Storage is writable " + getApplicationContext().getFilesDir().toString(), Toast.LENGTH_LONG);
            toast.show();
            try
            {
                outputStream = openFileOutput("test.txt", Context.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            }catch (Exception ex)
            {
                toast = Toast.makeText(getApplicationContext(), getfil)
            }
            file = new File("/", "test.txt");
        }*/
    }

    private void TestPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
        else
        {
            // Android version is lesser than 6.0 or the permission is already granted.
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                TestPermission();
            }
            else
            {
                Toast.makeText(this, "Error. Grant permission to read and write files", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
