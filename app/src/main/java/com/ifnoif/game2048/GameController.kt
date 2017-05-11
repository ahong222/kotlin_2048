package com.ifnoif.game2048

import android.content.Context

/**
 * Created by shen on 17/5/11.
 */

class GameController(var context: Context) {
    init {
        SoundPoolManager.init(context, SoundPoolManager.TYPE_GOOD, R.raw.good)
        SoundPoolManager.init(context, SoundPoolManager.TYPE_MERGE, R.raw.merge)
        SoundPoolManager.init(context, SoundPoolManager.TYPE_MOVE, R.raw.move)
    }

    fun getDefaultGameConfig(): GameConfig {
        return GameConfig(context)
    }

    fun getGameConfig(gridCount: Int): GameConfig {
        var gameConfig = GameConfig(context)
        gameConfig.gridCount = gridCount
        return gameConfig
    }
}