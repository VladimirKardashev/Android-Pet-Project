package kvs.com.ua.internetradio;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerActivity extends Activity implements View.OnClickListener, OnPreparedListener, OnCompletionListener {

    private int selectPositionInList;
    private int selectPage;
    private boolean FLAG_INFO;
    MediaPlayer mediaPlayer;
    AudioManager am;
    TrackInfo trackInfo;
    TextView trackName;
    TextView webSite;
    TextView textInfo;
    RelativeLayout rLayout;
    ImageView radioIcon;
    MyTask mtask;
    Station station;
    Button btnStart;
    Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_activity);

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        trackName = (TextView) findViewById(R.id.trackName);
        webSite = (TextView) findViewById(R.id.textWebSite);
        textInfo = (TextView) findViewById(R.id.textInfo);
        rLayout = (RelativeLayout) findViewById(R.id.rlayout);
        radioIcon = (ImageView) findViewById(R.id.radioicon);
        btnStart = (Button) findViewById(R.id.btnStartStream);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        selectPositionInList = extras.getInt("POSITIONINLIST");
        selectPage = extras.getInt("PAGENUMBER");
        station = new Station();
        rLayout.setBackgroundResource(station.getBackground(selectPage, selectPositionInList));
        radioIcon.setBackgroundResource(station.getIcon(selectPage, selectPositionInList));
        webSite.setText(station.getWebSite(selectPage, selectPositionInList));
        trackName.setTextColor(station.getTextColor(selectPage, selectPositionInList));
        webSite.setTextColor(station.getTextColor(selectPage, selectPositionInList));
        textInfo.setTextColor(station.getTextColor(selectPage, selectPositionInList));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartStream:
                releaseMP();
                if (!isOnline()) {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
                } else {

                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(station.getStream(selectPage, selectPositionInList));
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setOnPreparedListener(this);
                        mediaPlayer.prepareAsync();
                        FLAG_INFO = true;
                        if (!("").equals(station.getStreamInfo(selectPage, selectPositionInList))) {
                            mtask = new MyTask();
                            mtask.execute(station.getStreamInfo(selectPage, selectPositionInList));
                        }
                        trackName.setText("");

                        btnStart.setEnabled(false);
                        break;
                    } catch (IOException ex) {
                        Toast.makeText(this, "Error internet connection", Toast.LENGTH_LONG).show();
                    }
                }

            case R.id.btnStop:
                if (mediaPlayer == null)
                    return;
                mediaPlayer.stop();
                trackName.setText("");
                FLAG_INFO = false;
                btnStart.setEnabled(true);
                break;
        }
    }

    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        FLAG_INFO = false;
        trackName.setText("");
        btnStart.setEnabled(true);
        super.onBackPressed();
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception ex) {
                Toast.makeText(this, "No Connection! (" + ex.toString() + ")", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMP();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    private class MyTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            trackInfo = new TrackInfo();
            String nowPlay = "";
            while (FLAG_INFO == true) {
                if (!nowPlay.equals(trackInfo.getTrackInfo(urls[0]))) {
                    nowPlay = trackInfo.getTrackInfo(urls[0]);
                    publishProgress(nowPlay);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values != null) {
                super.onProgressUpdate(values);
                trackName.setText(values[0]);
            }
        }
    }

    private class Station {

        private String[] mStream;
        private String[] webSiteInfo;
        private String[] streamInfo;
        private int[] background;
        private int[] icon;
        private int[] textColor;

        protected String getStream(int pageSelect, int positionInList) {
            switch (pageSelect) {
                case 0:
                    mStream = getResources().getStringArray(R.array.Rock_Stream);
                    return mStream[positionInList];
                case 1:
                    mStream = getResources().getStringArray(R.array.Metal_Stream);
                    return mStream[positionInList];
                case 2:
                    return "";
                case 3:
                    return "";
                case 4:
                    return "";
            }
            return "";
        }

        protected String getWebSite(int pageSelect, int positionInList) {
            switch (pageSelect) {
                case 0:
                    webSiteInfo = getResources().getStringArray(R.array.rock_website);
                    return webSiteInfo[positionInList];
                case 1:
                    webSiteInfo = getResources().getStringArray(R.array.metal_website);
                    return webSiteInfo[positionInList];
                case 2:
                    return "";
                case 3:
                    return "";
                case 4:
                    return "";
            }
            return "";
        }

        protected String getStreamInfo(int pageSelect, int positionInList) {
            switch (pageSelect) {
                case 0:
            return "";
                case 1:
                    streamInfo = new String[]{"http://rt1.i.ua/c?r73&c&d117", "http://rt1.i.ua/c?r64&c&d233"};
                    if (positionInList < streamInfo.length) {
                        return streamInfo[positionInList];
                    }
                    return "";

                case 2:
                    return "";
                case 3:
                    return "";
                case 4:
                    return "";
            }
            return "";
        }

        protected int getBackground(int pageSelect, int positionInList) {
            switch (pageSelect) {
                case 1:
                    background = new int[]{R.mipmap.metalvoicebg, R.mipmap.radiometalbg};
                    if (positionInList < background.length) {
                        return background[positionInList];
                    }
                    return R.mipmap.black;
                case 0:
                    return R.mipmap.black;
                case 2:
                    return R.mipmap.pop;
                case 3:
                    return R.mipmap.emusic;
                case 4:
                    return R.mipmap.black;
            }
            return R.mipmap.black;
        }

        protected int getIcon(int pageSelect, int positionInList) {
            switch (pageSelect) {
                case 0:
                    icon = new int[]{R.mipmap.radiorocs, R.mipmap.bluesrock};
                    if (positionInList < icon.length) {
                        return icon[positionInList];
                    }
                    return R.mipmap.black;
                case 1:
                    icon = new int[]{R.mipmap.metalvoice, R.mipmap.radiometal, R.mipmap.metalheadr, R.mipmap.death, R.mipmap.brutalexpradio};
                    if (positionInList < icon.length) {
                        return icon[positionInList];
                    }
                    return R.mipmap.header;
                case 2:
                    return R.mipmap.pop;
                case 3:
                    return R.mipmap.emusic;
                case 4:
            }
            return R.mipmap.black;
        }

        protected int getTextColor(int pageSelect, int positionInList) {
            switch (pageSelect) {
                case 0:
                    return Color.WHITE;
                case 1:
                    textColor = new int[]{Color.BLACK, Color.WHITE};
                    if (positionInList < textColor.length) {
                        return textColor[positionInList];
                    }
                    return Color.WHITE;
                case 2:
                    return Color.WHITE;
                case 3:
                    return Color.WHITE;
                case 4:
                    return Color.WHITE;
            }
            return Color.WHITE;
        }
    }
}








