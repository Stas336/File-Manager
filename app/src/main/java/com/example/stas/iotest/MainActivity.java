package com.example.stas.iotest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Boolean result = false;
    String[] files;
    File directory;
    String current_path;
    ListView list;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    private static int KEYCODE_BACK_PRESSED_QTY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        result = AskForPermissions();
        if (result.equals(false))
        {
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            current_path = Environment.getExternalStorageDirectory().getPath();
        }
        else
        {
            Toast.makeText(this, "Storage is not mounted. Exiting...", Toast.LENGTH_SHORT).show();
            finish();
        }
        list = (ListView) findViewById(R.id.List);
        registerForContextMenu(list);
        updateList();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                File check = new File(current_path + "/" + list.getItemAtPosition(i).toString());
                if (check.isDirectory())
                {
                    current_path = current_path + "/" + list.getItemAtPosition(i).toString();
                    updateList();
                }
            }
        });
        /*list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Toast.makeText(getApplicationContext(), "LONG PRESS", Toast.LENGTH_SHORT).show();
                openOptionsMenu();
                return true;
            }
        });*/
        //toast.makeText(this, Environment.getExternalStorageDirectory().getPath(), Toast.LENGTH_LONG).show();
       /*File file = new File(Environment.getExternalStorageDirectory().getPath(), "test.txt");
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            // пишем данные
            bw.write("TEST");
            // закрываем поток
            bw.close();
        }catch (Exception ex)
        }*/
        //test = Environment.getExternalStoragePublicDirectory("Documents").listFiles();
        //test = Environment.getExternalStorageDirectory().listFiles();*/
        /*if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
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
            file = new File("/sdcard/", "test.txt");
        }*/
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.List)
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) //TODO
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.open:
                openFileDialog(info.position);
                return true;
            case R.id.edit:

                return true;
            case R.id.rename:
                renameFileDialog(list.getItemAtPosition(info.position).toString(), info.position);
                return true;
            case R.id.delete:
                deleteFileDialog(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateList()
    {
        directory = new File(current_path);
        files = directory.list();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, files);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            KEYCODE_BACK_PRESSED_QTY++;
            if (KEYCODE_BACK_PRESSED_QTY == 2)
            {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(50);
                Toast.makeText(this, "Press 1 more time to exit...", Toast.LENGTH_SHORT).show();
            }
            else if (KEYCODE_BACK_PRESSED_QTY == 3)
            {
                KEYCODE_BACK_PRESSED_QTY = 0;
                openQuitDialog();
            }
            if (!current_path.equals(Environment.getExternalStorageDirectory().getPath()))
            {
                KEYCODE_BACK_PRESSED_QTY--;
                current_path = current_path.substring(0, current_path.lastIndexOf("/"));
                updateList();
            }
        }
        else if (keyCode == KeyEvent.KEYCODE_MENU) //TODO
        {
            Toast.makeText(this, "NOT SUPPORTED AT THE MOMENT", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void showAllExternalStorages() //TODO
    {
        current_path = current_path.substring(0, current_path.lastIndexOf("/"));
        directory = new File(current_path);
        files = directory.list();

    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Do you want to exit?");
        quitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        quitDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        quitDialog.show();
    }

    private void renameFileDialog(String name, final int position)
    {
        AlertDialog.Builder renameFileDialog = new AlertDialog.Builder(this);
        renameFileDialog.setTitle("Rename");
        final EditText input = new EditText(this);
        input.setText(name);
        renameFileDialog.setView(input);
        renameFileDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                File old_file = new File(current_path, files[position]);
                File new_file = new File(current_path, input.getText().toString());
                old_file.renameTo(new_file);
                updateList();
            }
        });
        renameFileDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        renameFileDialog.show();
    }

    private void deleteFileDialog(final int position)
    {
        AlertDialog.Builder deleteFileDialog = new AlertDialog.Builder(this);
        deleteFileDialog.setTitle("Delete");
        deleteFileDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                File file = new File(current_path, files[position]);
                file.delete();
                updateList();
            }
        });
        deleteFileDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        deleteFileDialog.show();
    }

    private void openFileDialog(final int position)
    {
        File file = new File(current_path, files[position]);
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        String type = getContentResolver().getType(uri);
        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setType(type)
                .setStream(uri)
                .setChooserTitle("Choose application")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.startActivity(intent);
    }

    private boolean AskForPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
                AskForPermissions();
            }
            else
            {
                Toast.makeText(this, "Error. Grant permission to read and write files", Toast.LENGTH_SHORT).show();
            }
        }
    }
}