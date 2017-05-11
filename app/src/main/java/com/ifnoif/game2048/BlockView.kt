package com.ifnoif.game2048

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.block_view_layout.view.*

/**
 * Created by shen on 17/5/5.
 */

class BlockView : FrameLayout {

    lateinit var mBlockText: TextView
    lateinit var mCenterBlock: FrameLayout
    lateinit var gameConfig:GameConfig

    var point: Point = Point()
    private var value: Int = 0;

    var needRemoveView: Boolean = false
    var removeTranslation: Float = 0F

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        init()
    }

    fun init() {
        mBlockText = blockText
        mCenterBlock = centerBlock
    }

    fun getValue(): Int {
        return this.value;
    }

    fun setNumber(value: Int) {
        this.value = value
        mBlockText.setText("" + value)
        var color = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            color = context.getColor(gameConfig.getBlockTextColor(value))
        } else {
            context.resources.getColor(gameConfig.getBlockTextColor(value))
        }
        mBlockText.setTextColor(color)

        mCenterBlock.setBackgroundResource(gameConfig.getBlockBg(value))
    }

    companion object {
        fun create(context: Context, point: Point): BlockView {
            var blockView = LayoutInflater.from(context).inflate(R.layout.block_view_layout, null, false) as BlockView

            blockView.point = point

            return blockView;
        }
    }

}