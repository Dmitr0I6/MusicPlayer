package com.example.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private SessionManager sessionManager;

    private TextView tvTrackTitle;
    private TextView tvArtist;
    private TextView tvAlbum;
    private CheckBox chbFavorite;
    private SeekBar seekProgress;
    private Button btnPlay;

    private LinearLayout tracksContainer;

    private ArrayList<Track> tracks = new ArrayList<>();
    private int currentTrackIndex = 0;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            openLoginScreen();
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        initViews();
        loadTracksFromJson();
        showTracksList();

        if (!tracks.isEmpty()) {
            showTrack(0);
        }
    }

    private void initViews() {
        tvTrackTitle = findViewById(R.id.tvTrackTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvAlbum = findViewById(R.id.tvAlbum);
        chbFavorite = findViewById(R.id.chbFavorite);
        seekProgress = findViewById(R.id.seekProgress);
        btnPlay = findViewById(R.id.btnPlay);

        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnLogout = findViewById(R.id.btnLogout);

        tracksContainer = findViewById(R.id.tracksContainer);

        btnPlay.setOnClickListener(v -> togglePlay());
        btnPrev.setOnClickListener(v -> previousTrack());
        btnNext.setOnClickListener(v -> nextTrack());

        chbFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Трек добавлен в избранное", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Трек удален из избранного", Toast.LENGTH_SHORT).show();
            }
        });

        seekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Toast.makeText(MainActivity.this, "Позиция: " + progress + "%", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Метод вызывается при начале перемещения ползунка
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Метод вызывается после отпускания ползунка
            }
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            openLoginScreen();
            finish();
        });
    }

    private void loadTracksFromJson() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.tracks);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            JSONObject root = new JSONObject(json);
            JSONArray jsonTracks = root.getJSONArray("tracks");

            for (int i = 0; i < jsonTracks.length(); i++) {
                JSONObject item = jsonTracks.getJSONObject(i);

                Track track = new Track(
                        item.getInt("id"),
                        item.getString("title"),
                        item.getString("artist"),
                        item.getString("album"),
                        item.getString("duration")
                );

                tracks.add(track);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка чтения JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTracksList() {
        tracksContainer.removeAllViews();

        for (int i = 0; i < tracks.size(); i++) {
            Track track = tracks.get(i);
            int index = i;

            TextView trackView = new TextView(this);
            trackView.setText(track.getTitle() + " — " + track.getArtist() + " (" + track.getDuration() + ")");
            trackView.setTextColor(Color.WHITE);
            trackView.setTextSize(16);
            trackView.setPadding(20, 20, 20, 20);
            trackView.setBackgroundColor(Color.parseColor("#202020"));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 12);
            trackView.setLayoutParams(params);

            trackView.setOnClickListener(v -> showTrack(index));

            tracksContainer.addView(trackView);
        }
    }

    private void showTrack(int index) {
        if (index < 0 || index >= tracks.size()) {
            return;
        }

        currentTrackIndex = index;
        Track track = tracks.get(index);

        tvTrackTitle.setText(track.getTitle());
        tvArtist.setText(track.getArtist());
        tvAlbum.setText("Альбом: " + track.getAlbum() + " • " + track.getDuration());

        chbFavorite.setChecked(false);
        seekProgress.setProgress(0);

        Toast.makeText(this, "Выбран трек: " + track.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void togglePlay() {
        isPlaying = !isPlaying;

        if (isPlaying) {
            btnPlay.setText("Pause");
            seekProgress.setProgress(35);
            Toast.makeText(this, "Воспроизведение", Toast.LENGTH_SHORT).show();
        } else {
            btnPlay.setText("Play");
            Toast.makeText(this, "Пауза", Toast.LENGTH_SHORT).show();
        }
    }

    private void previousTrack() {
        if (tracks.isEmpty()) {
            return;
        }

        int newIndex = currentTrackIndex - 1;

        if (newIndex < 0) {
            newIndex = tracks.size() - 1;
        }

        showTrack(newIndex);
    }

    private void nextTrack() {
        if (tracks.isEmpty()) {
            return;
        }

        int newIndex = currentTrackIndex + 1;

        if (newIndex >= tracks.size()) {
            newIndex = 0;
        }

        showTrack(newIndex);
    }

    private void openLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}