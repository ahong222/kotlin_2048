package com.ifnoif.game2048

import android.content.Context
import android.media.SoundPool
import java.io.IOException

/**
 * Created by shen on 17/4/17.
 */


object SoundPoolManager {

    var soundMap: HashMap<Int, String> = HashMap()
    var soundPreparedMap: HashMap<String, Int> = HashMap()
    var soundPool: SoundPool = SoundPool.Builder().build();

    init {
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            if (status == 0) {
                soundMap[sampleId]?.let {
                    soundPreparedMap.put(it, sampleId)
                }
            }
        }
    }

    fun init(context: Context, type: String, rawId: Int) {
        var id = soundPool.load(context, rawId, 1);
        soundMap.put(id, type);
    }

    fun play(type: String) {
        var id = soundPreparedMap[type];
        id?.let { soundPool.play(id, 1F, 1F, 1, 0, 1F) }

    }
}