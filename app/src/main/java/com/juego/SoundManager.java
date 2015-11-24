package com.juego;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.HashMap;

/**
 * Created by Fernando on 24/11/2015.
 */
public class SoundManager {

    private static HashMap<Integer, MediaPlayer> players = new HashMap<>();

    public static void playSound(Context context, final int soundId){
        final MediaPlayer player = MediaPlayer.create(context, soundId);
        players.put(soundId, player);
        player.setLooping(false);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                MediaPlayer p = players.get(soundId);
                if (p != null) {
                    p.release();
                    players.remove(soundId);
                }
            }
        });
        player.start();
    }

}
