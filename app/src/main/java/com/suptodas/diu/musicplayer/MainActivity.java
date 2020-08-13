package com.suptodas.diu.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ListView listViewa;
    private ArrayList<String> songName;
    private TextView noMusic;
    private ArrayList<File> songss;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<File> tempSongFile = new ArrayList<>();
    private ArrayList<String> tempSongName = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("All Music");

        listViewa = findViewById(R.id.listView);
        noMusic = findViewById(R.id.noMusic);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            retrieveMusic();
        }

        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        listViewa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem(i, tempSongFile, tempSongName);
            }
        });

    }

    public void selectedItem(int i, final ArrayList<File> songs, final ArrayList<String> songName){

        startActivity(new Intent(MainActivity.this, AudioPlayerActivity.class)
                .putExtra("position", i).putExtra("list", songs).putExtra("songNameList", songName));

    }

    public void retrieveMusic(){

        songss = readSongs(Environment.getExternalStorageDirectory().getAbsoluteFile());

        Collections.sort(songss);

        if (!songss.isEmpty()){

            songName = new ArrayList<>();

            for (int i = 0; i<songss.size(); i++){
                songName.add(songss.get(i).getName().replace(".mp3", ""));
            }
        }
        else {
//            Log.i(getString(R.string.DEBUG), "Null Directory");
        }

        if(!songName.isEmpty()){
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.song_layout, R.id.textView, songName);
            listViewa.setAdapter(arrayAdapter);
            listViewa.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            noMusic.setVisibility(View.GONE);
        }
        else {
            noMusic.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            listViewa.setVisibility(View.GONE);
        }

        tempSongFile.addAll(songss);
        tempSongName.addAll(songName);
    }

    private ArrayList<File> readSongs(File root){
//        Log.i(getString(R.string.DEBUG), root.toString());
        ArrayList<File> arrayList = new ArrayList<>();
        File files[] = root.listFiles();

        if (files != null){
            for (File file : files){
                if (file.isDirectory()){
//                    Log.i(getString(R.string.DEBUG), file.toString());
                    arrayList.addAll(readSongs(file));
                }
                else {
                    if(file.getName().endsWith(".mp3")){
                        arrayList.add(file);
                    }
                }
            }
        }

        return arrayList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 0){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                retrieveMusic();
            }
            else {
                noMusic.setVisibility(View.VISIBLE);
                noMusic.setText("Storage Permission Denied");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_view_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_music);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setQueryHint("Enter Song Name");

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.isEmpty()){
                    tempSongFile.clear();
                    tempSongName.clear();
                    tempSongFile.addAll(songss);
                    tempSongName.addAll(songName);
                    ArrayAdapter arrayAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.song_layout, R.id.textView, tempSongName);
                    listViewa.setAdapter(arrayAdapter1);
                }
                else {
                    tempSongName.clear();
                    tempSongFile.clear();

                    for(File fileName: songss){
                        if(fileName.getName().toLowerCase().contains(newText.toLowerCase())){
                            tempSongFile.add(fileName);
                            tempSongName.add(fileName.getName().replace(".mp3", ""));
                        }
                    }
                    ArrayAdapter arrayAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.song_layout, R.id.textView, tempSongName);
                    listViewa.setAdapter(arrayAdapter1);
                }

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
