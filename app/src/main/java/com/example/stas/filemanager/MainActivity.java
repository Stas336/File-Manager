package com.example.stas.filemanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    String[] files;
    File directory;
    String current_path, current_path_first_window, current_path_second_window;
    int active_window;
    ListView list;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    private static int KEYCODE_BACK_PRESSED_QTY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AskForPermissions();
    }

    public void ActivityAfterGrantingPermissions()
    {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            current_path = Environment.getExternalStorageDirectory().getPath();
            active_window = 1;
            current_path_first_window = current_path;
            current_path_second_window = current_path;
        } else {
            Toast.makeText(this, "Storage is not mounted. Exiting...", Toast.LENGTH_SHORT).show();
            finish();
        }
        list = (ListView) findViewById(R.id.List);
        registerForContextMenu(list);
        updateList();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File check = new File(current_path + "/" + list.getItemAtPosition(i).toString());
                if (check.isDirectory()) {
                    current_path = current_path + "/" + list.getItemAtPosition(i).toString();
                    updateList();
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.List) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.open:
                openFileDialog(info.position);
                return true;
            case R.id.edit:
                try
                {
                    File file = new File(current_path, files[info.position]);
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    StringBuilder stringBuilder = new StringBuilder();
                    String file_content;
                    while ((file_content = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(file_content);
                    }
                    bufferedReader.close();
                    editFileDialog(stringBuilder.toString(), file);
                }catch (Exception ex)
                {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                return true;
            case R.id.copy:
                File file_from_active_window;
                File file_from_other_window;
                try
                {
                    if (active_window == 1)
                    {
                        file_from_active_window = new File(current_path, files[info.position]);
                        file_from_other_window = new File(current_path_second_window, files[info.position]);
                    }
                    else
                    {
                        file_from_active_window = new File(current_path, files[info.position]);
                        file_from_other_window = new File(current_path_first_window, files[info.position]);
                    }
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file_from_active_window));
                    StringBuilder stringBuilder = new StringBuilder();
                    String file_content;
                    while ((file_content = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(file_content);
                    }
                    bufferedReader.close();
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file_from_other_window));
                    bufferedWriter.write(stringBuilder.toString());
                    bufferedWriter.close();
                }catch (Exception ex)
                {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                Toast.makeText(this, "Successfully created copy of " + files[info.position], Toast.LENGTH_SHORT).show();
                return true;
            case R.id.move:
                if (active_window == 1)
                {
                    file_from_active_window = new File(current_path, files[info.position]);
                    file_from_other_window = new File(current_path_second_window, files[info.position]);
                }
                else
                {
                    file_from_active_window = new File(current_path, files[info.position]);
                    file_from_other_window = new File(current_path_first_window, files[info.position]);
                }
                file_from_active_window.renameTo(file_from_other_window);
                Toast.makeText(this, "Successfully moved " + files[info.position], Toast.LENGTH_SHORT).show();
                updateList();
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

    private void updateList() {
        directory = new File(current_path);
        files = directory.list();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, files);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            KEYCODE_BACK_PRESSED_QTY++;
            if (KEYCODE_BACK_PRESSED_QTY == 2) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(50);
                Toast.makeText(this, "Press 1 more time to exit...", Toast.LENGTH_SHORT).show();
            } else if (KEYCODE_BACK_PRESSED_QTY == 3) {
                KEYCODE_BACK_PRESSED_QTY = 0;
                openQuitDialog();
            }
            if (!current_path.equals(Environment.getExternalStorageDirectory().getPath())) {
                KEYCODE_BACK_PRESSED_QTY--;
                current_path = current_path.substring(0, current_path.lastIndexOf("/"));
                updateList();
            }
        } else if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            switchWindows();
        }
        return true;
    }

    private void switchWindows()
    {
        if (active_window != 1)
        {
            current_path_second_window = current_path;
            current_path = current_path_first_window;
            active_window = 1;
        }
        else
        {
            current_path_first_window = current_path;
            current_path = current_path_second_window;
            active_window = 2;
        }
        Toast.makeText(this, "Current active window is " + active_window, Toast.LENGTH_SHORT).show();
        updateList();
    }

    private void editFileDialog(final String content, final File original_file)
    {
        AlertDialog.Builder editFileDialog = new AlertDialog.Builder(this);
        editFileDialog.setTitle("Edit");
        final EditText input = new EditText(this);
        input.setText(content);
        editFileDialog.setView(input);
        editFileDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try
                {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(original_file));
                    bufferedWriter.write(input.getText().toString());
                    bufferedWriter.close();
                }catch (Exception ex)
                {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        editFileDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        editFileDialog.show();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Do you want to exit?");
        quitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        quitDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        quitDialog.show();
    }

    private void renameFileDialog(String name, final int position) {
        AlertDialog.Builder renameFileDialog = new AlertDialog.Builder(this);
        renameFileDialog.setTitle("Rename");
        final EditText input = new EditText(this);
        input.setText(name);
        renameFileDialog.setView(input);
        renameFileDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File old_file = new File(current_path, files[position]);
                File new_file = new File(current_path, input.getText().toString());
                old_file.renameTo(new_file);
                updateList();
            }
        });
        renameFileDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        renameFileDialog.show();
    }

    private void deleteFileDialog(final int position) {
        AlertDialog.Builder deleteFileDialog = new AlertDialog.Builder(this);
        deleteFileDialog.setTitle("Delete");
        deleteFileDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(current_path, files[position]);
                file.delete();
                updateList();
            }
        });
        deleteFileDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        deleteFileDialog.show();
    }

    private void openFileDialog(final int position) {
        File file;
        Uri uri;
        String type;
        Intent intent = null;
        try
        {
            file = new File(current_path, files[position]);
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
            type = getContentResolver().getType(uri);
            intent = ShareCompat.IntentBuilder.from(this)
                    .setType(type)
                    .setStream(uri)
                    .setChooserTitle("Choose application")
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
        this.startActivity(intent);
    }

    private void AskForPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        else
        {
            ActivityAfterGrantingPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                else
                {
                    Toast.makeText(this, "Permission has been denied to read external storage", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}