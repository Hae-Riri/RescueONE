package com.example.rescueone.sos;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.rescueone.R;

public class SirenPlayer {
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    public SirenPlayer(Context context){
        mContext = context;
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.siren);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void playAudio() {
        if(mMediaPlayer == null){
            mMediaPlayer = MediaPlayer.create(mContext, R.raw.siren);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
            mMediaPlayer.start();
        }
        else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
            mMediaPlayer.start();
        }
    }

    public void stopAudio() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void closePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
