package com.ifnoif.game2048

import android.content.Context
import java.util.*

/**
 * Created by shen on 17/5/11.
 */
class GameConfig {
    var mContext: Context

    /**
     * 格子数
     */
    var gridCount: Int = 4

    constructor(context: Context) {
        mContext = context
    }

    /**
     * 初始♦️个数
     */
    fun getFirstRandomCount(): Int {
        return 2
    }

    /**
     * 常规生成♦️个数
     */
    fun getNormalRandomCount(): Int {
        return 1
    }

    /**
     * 生成随机数算法
     */
    fun randomValue(): Int {
        return 2 + (Random().nextInt(2) * 2)
    }

    /**
     * 是否win
     */
    fun win(maxValue: Int): Boolean {
        var result = maxValue == 2048
        if (result) SoundPoolManager.play(SoundPoolManager.TYPE_GOOD)
        return result
    }

    /**
     * 显示数值,返回空可以不显示数值，只显示背景图
     */
    fun getBlockText(value: Int): String {
        return value.toString();
    }

    /**
     * 文字颜色
     */
    fun getBlockTextColor(value: Int): Int {
        return mContext.resources.getIdentifier("block_text_color" + value, "color", mContext.packageName);
    }

    /**
     * 背景图
     */
    fun getBlockBg(value: Int): Int {
        return mContext.resources.getIdentifier("block_bg" + value, "drawable", mContext.packageName);
    }

    /**
     * 有合并
     */
    fun onMerged(value: Int) {
        SoundPoolManager.play(SoundPoolManager.TYPE_MERGE)
    }

    /**
     * 有移动
     */
    fun onMove() {
        SoundPoolManager.play(SoundPoolManager.TYPE_MOVE)
    }
}