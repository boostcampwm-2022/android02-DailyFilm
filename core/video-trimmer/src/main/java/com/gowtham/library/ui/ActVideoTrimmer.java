package com.gowtham.library.ui;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.gson.Gson;
import com.gowtham.library.R;
import com.gowtham.library.ui.seekbar.widgets.CrystalRangeSeekbar;
import com.gowtham.library.ui.seekbar.widgets.CrystalSeekbar;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.CustomProgressView;
import com.gowtham.library.utils.FileUtils;
import com.gowtham.library.utils.LocaleHelper;
import com.gowtham.library.utils.LogMessage;
import com.gowtham.library.utils.TrimVideo;
import com.gowtham.library.utils.TrimVideoOptions;
import com.gowtham.library.utils.TrimmerUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;


public class ActVideoTrimmer extends LocalizationActivity {

    private static final int PER_REQ_CODE = 115;
    private StyledPlayerView playerView;
    private ExoPlayer videoPlayer;

    private ImageView imagePlayPause;

    private ImageView[] imageViews;

    private long totalDuration;

    private Dialog dialog;

    private Uri uri;

    private TextView txtStartDuration, txtEndDuration;

    private CrystalRangeSeekbar seekbar;

    private long lastMinValue = 0;

    private long lastMaxValue = 0;

    private MenuItem menuDone;

    private CrystalSeekbar seekbarController;

    private boolean isValidVideo = true, isVideoEnded;

    private android.os.Handler seekHandler;

    private Bundle bundle;

    private ProgressBar progressBar;

    private TrimVideoOptions trimVideoOptions;
    private LottieAnimationView lottie_trim_sound_control;

    private long currentDuration, lastClickedTime;
    Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            try {
                currentDuration = videoPlayer.getCurrentPosition() / 1000;
                if (!videoPlayer.getPlayWhenReady())
                    return;
                if (currentDuration <= lastMaxValue)
                    seekbarController.setMinStartValue((int) currentDuration).apply();
                else
                    videoPlayer.setPlayWhenReady(false);
            } finally {
                seekHandler.postDelayed(updateSeekbar, 1000);
            }
        }
    };
    private CompressOption compressOption;
    private String outputPath;
    private String local;
    private int trimType;
    private long fixedGap, minGap, minFromGap, maxToGap;
    private boolean hidePlayerSeek, isAccurateCut, showFileLocationAlert;
    private CustomProgressView progressView;
    private String fileName;
    private Boolean soundFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_video_trimmer);

        ImageView iv_trim_next = findViewById(R.id.iv_trim_next);
        ImageView iv_trim_back = findViewById(R.id.iv_trim_back);
        lottie_trim_sound_control = findViewById(R.id.lottie_trim_sound_control);

        bundle = getIntent().getExtras();
        Gson gson = new Gson();
        String videoOption = bundle.getString(TrimVideo.TRIM_VIDEO_OPTION);
        trimVideoOptions = gson.fromJson(videoOption, TrimVideoOptions.class);
        setUpToolBar(getSupportActionBar(), trimVideoOptions.title);

        iv_trim_back.setOnClickListener(view -> finish());
        iv_trim_next.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - lastClickedTime > 800) {
                lastClickedTime = SystemClock.elapsedRealtime();
                trimVideo();
            }
        });

        progressView = new CustomProgressView(this);
    }

    @Override
    protected void attachBaseContext(@NotNull Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        playerView = findViewById(R.id.player_view_lib);
        imagePlayPause = findViewById(R.id.image_play_pause);
        seekbar = findViewById(R.id.range_seek_bar);
        txtStartDuration = findViewById(R.id.txt_start_duration);
        txtEndDuration = findViewById(R.id.txt_end_duration);
        seekbarController = findViewById(R.id.seekbar_controller);
        progressBar = findViewById(R.id.progress_circular);
        ImageView imageOne = findViewById(R.id.image_one);
        ImageView imageTwo = findViewById(R.id.image_two);
        ImageView imageThree = findViewById(R.id.image_three);
        ImageView imageFour = findViewById(R.id.image_four);
        ImageView imageFive = findViewById(R.id.image_five);
        ImageView imageSix = findViewById(R.id.image_six);
        ImageView imageSeven = findViewById(R.id.image_seven);
        ImageView imageEight = findViewById(R.id.image_eight);
        imageViews = new ImageView[]{imageOne, imageTwo, imageThree,
                imageFour, imageFive, imageSix, imageSeven, imageEight};
        seekHandler = new Handler();
        initPlayer();

        lottie_trim_sound_control.setOnClickListener(view -> {
            if (soundFlag) {
                soundFlag = false;
                Objects.requireNonNull(playerView.getPlayer()).setVolume(0.5f);
                ValueAnimator animator = ValueAnimator.ofFloat(0.5f, 1.0f).setDuration(500);
                animator.addUpdateListener(valueAnimator -> {
                    lottie_trim_sound_control.setProgress((Float) valueAnimator.getAnimatedValue());
                });
                animator.start();
            } else {
                soundFlag = true;
                Objects.requireNonNull(playerView.getPlayer()).setVolume(0.0f);
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(500);
                animator.addUpdateListener(valueAnimator -> {
                    lottie_trim_sound_control.setProgress((Float) valueAnimator.getAnimatedValue());
                });
                animator.start();
            }
        });

        if (checkStoragePermission())
            setDataInView();
    }

    private void setUpToolBar(ActionBar actionBar, String title) {
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(title != null ? title : getString(R.string.txt_edt_video));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * SettingUp exoplayer
     **/
    private void initPlayer() {
        try {
            videoPlayer = new ExoPlayer.Builder(this).build();
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            playerView.setPlayer(videoPlayer);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build();
            videoPlayer.setAudioAttributes(audioAttributes, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDataInView() {
        try {
            Runnable fileUriRunnable = () -> {
                uri = Uri.parse(bundle.getString(TrimVideo.TRIM_VIDEO_URI));
//              String path = FileUtils.getPath(ActVideoTrimmer.this, uri);
                String path = FileUtils.getRealPath(ActVideoTrimmer.this, uri);
                uri = Uri.parse(path);
                runOnUiThread(() -> {
                    LogMessage.v("VideoUri:: " + uri);
                    progressBar.setVisibility(View.GONE);
                    totalDuration = TrimmerUtils.getDuration(ActVideoTrimmer.this, uri);
                    imagePlayPause.setOnClickListener(v ->
                            onVideoClicked());
                    Objects.requireNonNull(playerView.getVideoSurfaceView()).setOnClickListener(v ->
                            onVideoClicked());
                    initTrimData();
                    buildMediaSource(uri);
                    loadThumbnails();
                    setUpSeekBar();
                });
            };
            Executors.newSingleThreadExecutor().execute(fileUriRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTrimData() {
        try {
            assert trimVideoOptions != null;
            trimType = TrimmerUtils.getTrimType(trimVideoOptions.trimType);
            fileName = trimVideoOptions.fileName;
            hidePlayerSeek = trimVideoOptions.hideSeekBar;
            isAccurateCut = trimVideoOptions.accurateCut;
            local = trimVideoOptions.local;
            compressOption = trimVideoOptions.compressOption;
            showFileLocationAlert = trimVideoOptions.showFileLocationAlert;
            fixedGap = trimVideoOptions.fixedDuration;
            fixedGap = fixedGap != 0 ? fixedGap : totalDuration;
            minGap = trimVideoOptions.minDuration;
            minGap = minGap != 0 ? minGap : totalDuration;
            if (trimType == 3) {
                minFromGap = trimVideoOptions.minToMax[0];
                maxToGap = trimVideoOptions.minToMax[1];
                minFromGap = minFromGap != 0 ? minFromGap : totalDuration;
                maxToGap = maxToGap != 0 ? maxToGap : totalDuration;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        setLanguage(new Locale(local != null ? local : "ko"));
    }

    private void onVideoClicked() {
        try {
            if (isVideoEnded) {
                seekTo(lastMinValue);
                videoPlayer.setPlayWhenReady(true);
                return;
            }
            if ((currentDuration - lastMaxValue) > 0)
                seekTo(lastMinValue);
            videoPlayer.setPlayWhenReady(!videoPlayer.getPlayWhenReady());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seekTo(long sec) {
        if (videoPlayer != null)
            videoPlayer.seekTo(sec * 1000);
    }

    private void buildMediaSource(Uri mUri) {
        try {
            DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(mUri));
            videoPlayer.addMediaSource(mediaSource);
            videoPlayer.prepare();
            videoPlayer.setPlayWhenReady(true);
            videoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                    imagePlayPause.setVisibility(playWhenReady ? View.GONE :
                            View.VISIBLE);
                }

                @Override
                public void onPlaybackStateChanged(int state) {
                    switch (state) {
                        case Player.STATE_ENDED:
                            LogMessage.v("onPlayerStateChanged: Video ended.");
                            imagePlayPause.setVisibility(View.VISIBLE);
                            isVideoEnded = true;
                            break;
                        case Player.STATE_READY:
                            isVideoEnded = false;
                            imagePlayPause.setVisibility(View.GONE);
                            startProgress();
                            LogMessage.v("onPlayerStateChanged: Ready to play.");
                            break;
                        default:
                            break;
                        case Player.STATE_BUFFERING:
                            LogMessage.v("onPlayerStateChanged: STATE_BUFFERING.");
                            break;
                        case Player.STATE_IDLE:
                            LogMessage.v("onPlayerStateChanged: STATE_IDLE.");
                            break;
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  loading thumbnails
     * */
    private void loadThumbnails() {
        try {
            long diff = totalDuration / 8;
            int sec = 1;
            for (ImageView img : imageViews) {
                long interval = (diff * sec) * 1000000;
                RequestOptions options = new RequestOptions().frame(interval);
                Glide.with(this)
                        .load(bundle.getString(TrimVideo.TRIM_VIDEO_URI))
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(300))
                        .into(img);
                if (sec < totalDuration)
                    sec++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpSeekBar() {
        seekbar.setVisibility(View.VISIBLE);
        txtStartDuration.setVisibility(View.VISIBLE);
        txtEndDuration.setVisibility(View.VISIBLE);

        seekbarController.setMaxValue(totalDuration).apply();
        seekbar.setMaxValue(totalDuration).apply();
        seekbar.setMaxStartValue((float) totalDuration).apply();
        if (trimType == 1) {
            seekbar.setFixGap(fixedGap).apply();
            lastMaxValue = totalDuration;
        } else if (trimType == 2) {
            seekbar.setMaxStartValue((float) minGap);
            seekbar.setGap(minGap).apply();
            lastMaxValue = totalDuration;
        } else if (trimType == 3) {
            seekbar.setMaxStartValue((float) maxToGap);
            seekbar.setGap(minFromGap).apply();
            lastMaxValue = maxToGap;
        } else {
            seekbar.setGap(2).apply();
            lastMaxValue = totalDuration;
        }
        if (hidePlayerSeek)
            seekbarController.setVisibility(View.GONE);

        seekbar.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            if (!hidePlayerSeek)
                seekbarController.setVisibility(View.VISIBLE);
        });

        seekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            long minVal = (long) minValue;
            long maxVal = (long) maxValue;
            if (lastMinValue != minVal) {
                seekTo((long) minValue);
                if (!hidePlayerSeek)
                    seekbarController.setVisibility(View.INVISIBLE);
            }
            lastMinValue = minVal;
            lastMaxValue = maxVal;
            txtStartDuration.setText(TrimmerUtils.formatSeconds(minVal));
            txtEndDuration.setText(TrimmerUtils.formatSeconds(maxVal));
            if (trimType == 3)
                setDoneColor(minVal, maxVal);
        });

        seekbarController.setOnSeekbarFinalValueListener(value -> {
            long value1 = (long) value;
            if (value1 < lastMaxValue && value1 > lastMinValue) {
                seekTo(value1);
                return;
            }
            if (value1 > lastMaxValue)
                seekbarController.setMinStartValue((int) lastMaxValue).apply();
            else if (value1 < lastMinValue) {
                seekbarController.setMinStartValue((int) lastMinValue).apply();
                if (videoPlayer.getPlayWhenReady())
                    seekTo(lastMinValue);
            }
        });
    }

    /**
     * will be called whenever seekBar range changes
     * it checks max duration is exceed or not.
     * and disabling and enabling done menuItem
     *
     * @param minVal left thumb value of seekBar
     * @param maxVal right thumb value of seekBar
     */
    private void setDoneColor(long minVal, long maxVal) {
        try {
            if (menuDone == null)
                return;
            //changed value is less than maxDuration
            if ((maxVal - minVal) <= maxToGap) {
                menuDone.getIcon().setColorFilter(
                        new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorWhite)
                                , PorterDuff.Mode.SRC_IN)
                );
                isValidVideo = true;
            } else {
                menuDone.getIcon().setColorFilter(
                        new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorWhiteLt)
                                , PorterDuff.Mode.SRC_IN)
                );
                isValidVideo = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PER_REQ_CODE) {
            if (isPermissionOk(grantResults))
                setDataInView();
            else {
                Toast.makeText(this, getString(R.string.txt_denied), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPlayer != null)
            videoPlayer.release();
        if (progressView != null && progressView.isShowing())
            progressView.dismiss();
        deleteFile("temp_file");
        stopRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    // 완료
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuDone = menu.findItem(R.id.action_done);
        return super.onPrepareOptionsMenu(menu);
    }

    // 완료
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            //preventing multiple clicks
            if (SystemClock.elapsedRealtime() - lastClickedTime < 800)
                return true;
            lastClickedTime = SystemClock.elapsedRealtime();
            trimVideo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void trimVideo() {
        if (isValidVideo) {
            //not exceed given maxDuration if has given
            outputPath = getFileName();
            LogMessage.v("outputPath::" + outputPath + new File(outputPath).exists());
            LogMessage.v("sourcePath::" + uri);
            videoPlayer.setPlayWhenReady(false);
            showProcessingDialog();
            String[] complexCommand;
            if (compressOption != null)
                complexCommand = getCompressionCmd();
            else if (isAccurateCut) {
                //no changes in video quality
                //faster trimming command and given duration will be accurate
                complexCommand = getAccurateCmd();
            } else {
                //no changes in video quality
                //fastest trimming command however, result duration
                //will be low accurate(2-3 secs)
                complexCommand = new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                        "-i", String.valueOf(uri),
                        "-t",
                        TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue),
                        "-async", "1", "-strict", "-2", "-c", "copy", outputPath};
            }
            execFFmpegBinary(complexCommand, true);
        } else
            Toast.makeText(this, getString(R.string.txt_smaller) + " " + TrimmerUtils.getLimitedTimeFormatted(maxToGap), Toast.LENGTH_SHORT).show();
    }

    private String getFileName() {
        String path = getExternalFilesDir("TrimmedVideo").getPath();
        Calendar calender = Calendar.getInstance();
        String fileDateTime = calender.get(Calendar.YEAR) + "_" +
                calender.get(Calendar.MONTH) + "_" +
                calender.get(Calendar.DAY_OF_MONTH) + "_" +
                calender.get(Calendar.HOUR_OF_DAY) + "_" +
                calender.get(Calendar.MINUTE) + "_" +
                calender.get(Calendar.SECOND);
        String fName = "trimmed_video_";
        if (fileName != null && !fileName.isEmpty())
            fName = fileName;
        File newFile = new File(path + File.separator +
                (fName) + fileDateTime + "." + TrimmerUtils.getFileExtension(this, uri));
        return String.valueOf(newFile);
    }

    private String[] getCompressionCmd() {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(String.valueOf(uri));
        int rotation = TrimmerUtils.getVideoRotation(this, uri);
        if (rotation == 90 || rotation == 270) {
            int w = compressOption.getWidth();
            int h = compressOption.getHeight();

            compressOption.setWidth(h);
            compressOption.setHeight(w);
        }

        if (compressOption.getWidth() != 0 || compressOption.getHeight() != 0
                || !compressOption.getBitRate().equals("0k")) {
            return new String[]{
                    "-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                    "-i", String.valueOf(uri),
                    "-r", String.valueOf(compressOption.getFrameRate()),
                    "-vf", String.format("scale=%d:%d", compressOption.getWidth(), compressOption.getHeight()),
                    "-c:v", "libx265", "-vtag", "hvc1",
                    "-crf", "28",
                    "-preset", "ultrafast",
                    "-t", TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue),
                    outputPath};
        } else {
            return new String[]{
                    "-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                    "-i", String.valueOf(uri),
                    "-r", "24",
                    "-c:v", "libx265", "-vtag", "hvc1",
                    "-crf", "28",
                    "-preset", "ultrafast",
                    "-t", TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue),
                    outputPath};
        }
    }

    private void execFFmpegBinary(final String[] command, boolean retry) {
        long intendedTrimmedDurationMs = (lastMaxValue - lastMinValue) * 1000;
        try {
            new Thread(() -> {
                dialog.dismiss(); // No dialog process
                FFmpeg.executeAsync(command, new ExecuteCallback() {
                    @Override
                    public void apply(long executionId, int returnCode) {
                    }
                });
            }).start();

            Intent intent = new Intent();
            intent.putExtra(TrimVideo.TRIMMED_VIDEO_PATH, outputPath);
            intent.putExtra(TrimVideo.TRIMMED_VIDEO_START_MILLI, lastMinValue * 1000);
            intent.putExtra(TrimVideo.INTENDED_TRIMMED_DURATION_MS, intendedTrimmedDurationMs);
            setResult(RESULT_OK, intent);
            finish(); // close activity immediately

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLocationAlert() {
        // dialog to ask user to open file location in file manager or not
        AlertDialog openFileLocationDialog = new AlertDialog.Builder(ActVideoTrimmer.this).create();
        openFileLocationDialog.setTitle(getString(R.string.open_file_location));
        openFileLocationDialog.setCancelable(true);

        // when user click yes
        openFileLocationDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), (dialogInterface, i) -> {
            // open file location
            Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uriFile = Uri.parse(outputPath);
            chooser.addCategory(Intent.CATEGORY_OPENABLE);
            chooser.setDataAndType(uriFile, "*/*");
            startActivity(chooser);
        });

        // when user click no and finish current activity
        openFileLocationDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), (dialogInterface, i) -> openFileLocationDialog.dismiss());

        // when user click no and finish current activity
        openFileLocationDialog.setOnDismissListener(dialogInterface -> {
            Intent intent = new Intent();
            intent.putExtra(TrimVideo.TRIMMED_VIDEO_PATH, outputPath);
            setResult(RESULT_OK, intent);
            finish();
        });
        openFileLocationDialog.show();
    }

    private String[] getAccurateCmd() {
        return new String[]{"-ss", TrimmerUtils.formatCSeconds(lastMinValue)
                , "-i", String.valueOf(uri), "-t",
                TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue),
                "-async", "1", outputPath};
    }

    private void showProcessingDialog() {
        try {
            dialog = new Dialog(this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_convert);
            TextView txtCancel = dialog.findViewById(R.id.txt_cancel);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            txtCancel.setOnClickListener(v -> {
                dialog.dismiss();
                FFmpeg.cancel();
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return checkPermission(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    Manifest.permission.ACCESS_MEDIA_LOCATION
            );
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            return checkPermission(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.ACCESS_MEDIA_LOCATION
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_MEDIA_LOCATION
            );
        } else
            return checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    private boolean checkPermission(String... permissions) {
        boolean allPermitted = false;
        for (String permission : permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED);
            if (!allPermitted)
                break;
        }
        if (allPermitted)
            return true;
        ActivityCompat.requestPermissions(this, permissions,
                PER_REQ_CODE);
        return false;
    }

    private boolean isPermissionOk(int... results) {
        boolean isAllGranted = true;
        for (int result : results) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }

    void startProgress() {
        updateSeekbar.run();
    }

    void stopRepeatingTask() {
        seekHandler.removeCallbacks(updateSeekbar);
    }

}
