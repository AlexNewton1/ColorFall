package com.softwareoverflow.colorfall.media;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import com.softwareoverflow.colorfall.R;

import java.util.HashMap;

public class SoundEffectHandler {

    public enum Sound {
        LEVEL_UP(R.raw.level_up), SCORE(R.raw.score), LOSE_LIFE(R.raw.lose_life), GAME_OVER(R.raw.game_over);

        private int resId;

        Sound(int resId){
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
    }

    private SoundPool soundPool;
    private boolean isLoaded;
    private HashMap<Sound, Integer> soundMap = new HashMap<>();

    private final String LOG_TAG = SoundEffectHandler.class.getSimpleName();


    public SoundEffectHandler(Context context){
        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .build()
                )
                .build();

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if(status == 0) isLoaded = true;
                else Log.e(LOG_TAG, "Error setting up SoundPool");
            }
        });

        for(Sound sound : Sound.values()){
            Integer id = soundPool.load(context, sound.getResId(), 1);
            soundMap.put(sound, id);
        }
    }

    public void playSound(Sound sound){
        if(!isLoaded) return;

        int soundId = soundMap.get(sound);
        soundPool.play(soundId, 1, 1, 1, 0, 1);
    }
}
