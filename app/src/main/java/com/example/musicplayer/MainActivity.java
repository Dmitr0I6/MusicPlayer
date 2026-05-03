package com.example.musicplayer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicplayer.model.Track;
import com.example.musicplayer.viewmodel.MusicViewModel;

import java.util.ArrayList;

public class MainActivity extends ComponentActivity {

    private SessionManager sessionManager;
    private MusicViewModel viewModel;

    private EditText etSearch;
    private ProgressBar progressLoading;

    private TextView tvTrackTitle;
    private TextView tvArtist;
    private TextView tvAlbum;
    private CheckBox chbFavorite;
    private SeekBar seekProgress;
    private Button btnPlay;

    private LinearLayout tracksContainer;

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
        initViewModel();

        viewModel.loadTracks("Imagine Dragons");
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        progressLoading = findViewById(R.id.progressLoading);

        tvTrackTitle = findViewById(R.id.tvTrackTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvAlbum = findViewById(R.id.tvAlbum);
        chbFavorite = findViewById(R.id.chbFavorite);
        seekProgress = findViewById(R.id.seekProgress);
        btnPlay = findViewById(R.id.btnPlay);

        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnLogout = findViewById(R.id.btnLogout);

        tracksContainer = findViewById(R.id.tracksContainer);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();

            if (query.isEmpty()) {
                Toast.makeText(this, "Введите название трека или исполнителя", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.loadTracks(query);
        });

        btnPlay.setOnClickListener(v -> viewModel.togglePlay());
        btnPrev.setOnClickListener(v -> viewModel.previousTrack());
        btnNext.setOnClickListener(v -> viewModel.nextTrack());

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
                // Начало перемещения ползунка
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Конец перемещения ползунка
            }
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            openLoginScreen();
            finish();
        });
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        viewModel.getLoadingLiveData().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                progressLoading.setVisibility(View.VISIBLE);
            } else {
                progressLoading.setVisibility(View.GONE);
            }
        });

        viewModel.getTracksLiveData().observe(this, tracks -> {
            if (tracks != null) {
                showTracksList(tracks);
            }
        });

        viewModel.getSelectedTrackLiveData().observe(this, track -> {
            if (track != null) {
                showTrack(track);
            }
        });

        viewModel.getMessageLiveData().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getPlayButtonTextLiveData().observe(this, text -> {
            if (text != null) {
                btnPlay.setText(text);
            }
        });
    }

    private void showTracksList(ArrayList<Track> tracks) {
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

            trackView.setOnClickListener(v -> viewModel.selectTrack(index));

            tracksContainer.addView(trackView);
        }
    }

    private void showTrack(Track track) {
        tvTrackTitle.setText(track.getTitle());
        tvArtist.setText(track.getArtist());
        tvAlbum.setText("Альбом: " + track.getAlbum() + " • " + track.getDuration());

        chbFavorite.setChecked(false);
        seekProgress.setProgress(0);
    }

    private void openLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}