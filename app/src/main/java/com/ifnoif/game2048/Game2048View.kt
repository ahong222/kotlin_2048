package com.ifnoif.game2048

import android.animation.*
import android.app.AlertDialog
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import java.util.*


/**
 * Created by shen on 17/4/12.
 */

class Game2048View : FrameLayout {

    lateinit var gameConfig: GameConfig

    companion object {

        var TAG = "Game2048View"

    }

    var mTouchSlop: Int = 0
    var mStarted: Boolean = false
    var mMaxValue = 0

    val mDuration: Long = 300
    var mColumnSize: Int
    lateinit var mBlockArray: Array<Array<GameUtil.Block>>
    /**
     * 滑动后剩余的空白位置
     */
    var mEmptyPointList: ArrayList<Point> = ArrayList<Point>()
    /**
     * 滑动后需要移动 的Action
     */
    var mActionList: ArrayList<GameUtil.Block> = ArrayList<GameUtil.Block>()
    var mGameUtil = GameUtil()


    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {


        val vc = ViewConfiguration.get(getContext())
        mTouchSlop = vc.scaledTouchSlop
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typeArray: TypedArray = context!!.obtainStyledAttributes(attrs,
                R.styleable.Game2048View)
        mColumnSize = typeArray.getInt(R.styleable.Game2048View_grid, 4)
        typeArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(widthSize, widthSize)
    }

    fun start() {
        Log.d(TAG, "开始游戏")
        reset()
        doNext(true)
        mStarted = true
    }

    fun doNext(first: Boolean) {
        var gameStatus = checkGameOver()
        if (gameStatus > 0) {
            Log.d(TAG, "游戏结束")
            (context as GameActivity).onGameComplete()
            var dialog = AlertDialog.Builder(context).setTitle("Game Over").setMessage(String.format("您本次得分%d,是否再玩一遍", totalScore))
                    .setPositiveButton("确定", { dialog, which -> start() })
                    .setNegativeButton("取消", { dialog, which -> reset(); invalidate(); }).create()
            dialog.show()
            return
        }
        var randomCount = if (first) gameConfig.getFirstRandomCount() else gameConfig.getNormalRandomCount()
        for (i in 0..randomCount - 1) {
            if (mEmptyPointList.size == 0) {
                break
            }
            var randomIndex = Random().nextInt(mEmptyPointList.size)
            var point = mEmptyPointList.removeAt(randomIndex)

            var randomValue = gameConfig.randomValue()
            (mBlockArray[point.y])[point.x].value = randomValue

            var view = BlockView.create(context, point)
            view.gameConfig = gameConfig
            view.setNumber(randomValue)
            addView(view, LayoutParams(width / mColumnSize, width / mColumnSize))

            view.translationX = (point.x * width.toFloat()) / mColumnSize
            view.translationY = (point.y * height.toFloat()) / mColumnSize

            invalidate()

            Log.d(TAG, "生成一个方块(" + point.x + "," + point.y + "),大小:" + randomValue)
        }
    }

    fun scroll(direction: GameUtil.Direction) {
        Log.d(TAG, "往" + direction + "滑动")


        mEmptyPointList.clear()
        mActionList.clear()

        mGameUtil.scroll(mBlockArray, direction, mEmptyPointList, mActionList)

        Log.d(TAG, "所有移动如下:")
        for (block in mActionList) {
            Log.d(TAG, block.getActionStr())
        }


        var callback: Runnable = Runnable { doNext(false) }
        doActionAnimation(callback)
    }

    fun doActionAnimation(callback: Runnable) {
        if (mActionList.size == 0) {
            callback.run()
            return
        }
        Log.d(TAG, "开始滑动==")
        var animatorSet: AnimatorSet = AnimatorSet()
        animatorSet.duration = mDuration
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                Log.d(TAG, "动画全部完成")
                callback.run()
            }
        })

        for (block in mActionList) {
            val needRemoveView = block.changeX >= 0
            var removeTranslation: Float = 0F
            var targetRemoveView: BlockView? = null
            if (needRemoveView) {
                if (block.x == block.pX) {
                    removeTranslation = Math.abs((block.changeY * width).toFloat() / mColumnSize)
                } else {
                    removeTranslation = Math.abs((block.changeX * width).toFloat() / mColumnSize)
                }
                targetRemoveView = getView(block.changeX, block.changeY)

            } else {
                gameConfig.onMove()
            }
            var scrollHorizontal: Boolean = (block.pY == block.y)

            var startOffset = (width * (if (scrollHorizontal) block.pX else block.pY)).toFloat() / mColumnSize
            var targetOffset = (width * (if (scrollHorizontal) block.x else block.y)).toFloat() / mColumnSize

            Log.d(TAG, block.getActionStr() + " 像素从" + startOffset + "到" + targetOffset)

            var view = getView(block.pX, block.pY)
            if (view == null) {
                Log.e(TAG, "获取(" + block.pX + "," + block.pY + ")为空")
                continue
            }
            view!!.needRemoveView = needRemoveView
            view.removeTranslation = removeTranslation + ((if (targetOffset > startOffset) -0.02F else 0.02F) * width / mColumnSize)
            view.removeTranslation = Math.min(Math.max(0F, view.removeTranslation), width.toFloat())

            var animation: ValueAnimator = ObjectAnimator.ofFloat(view, (if (scrollHorizontal) "translationX" else "translationY"), startOffset, targetOffset)
            animation.duration = mDuration
            animation.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    Log.d(TAG, block.getActionStr() + " ==完成,像素:" + (if (scrollHorizontal) view.translationX else view.translationY))
                }

                override fun onAnimationStart(animation: Animator?) {
                    //修改View中的坐标,不等动画结束
                    view.point.x = block.x
                    view.point.y = block.y
                }
            })


            animation.addUpdateListener { animator ->
                var currentTranslation = (animator!!.animatedValue as Float)

                if (targetOffset > startOffset) {

                }
                if (view.needRemoveView && (if (targetOffset > startOffset) currentTranslation >= view.removeTranslation else currentTranslation <= view.removeTranslation)) {
                    //删除合并的view
                    removeView(targetRemoveView)

                    addScore(view.getValue())

                    view.needRemoveView = false
                    var newValue = view.getValue() * 2
                    view.setNumber(newValue)

                    gameConfig.onMerged(newValue)
                    mMaxValue = Math.max(mMaxValue, newValue)

                    Log.d(TAG, "改变" + "(" + view.point.x + "," + view.point.y + ")的值为" + view.getValue())
                } else {
                    Log.d(TAG, "移动 currentTranslation：" + currentTranslation + " removeTranslation:" + view.removeTranslation)
                }
            }
            animatorSet.playSequentially(animation)
        }
        animatorSet.start()
    }

    fun getView(x: Int, y: Int): BlockView? {
        for (index in 0..childCount - 1) {
            var child = (getChildAt(index) as BlockView)
            if (child.point.x == x && child.point.y == y) {
                return child
            }
        }
        return null
    }

    fun checkGameOver(): Int {
        if(gameConfig.win(mMaxValue)){
            return 1
        } else if(mEmptyPointList.size == 0){
            return 2
        }
        return 0
    }

    fun reset() {
        Log.d(TAG, "初始化")
        mStarted = false
        totalScore = 0
        mMaxValue = 0
        onScoreChanged()
        removeAllViews()
        mEmptyPointList.clear()
        mBlockArray = Array(mColumnSize, { y -> Array(mColumnSize, { x -> GameUtil.Block(x, y, 0) }) })
        for (y in 0..mColumnSize - 1) {
            for (x in 0..mColumnSize - 1) {
                mEmptyPointList.add(Point(x, y))
            }
        }
    }

    var touchDownX: Float = 0f
    var touchDownY: Float = 0f
    var scrolled: Boolean = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownX = event.x
                touchDownY = event.y
                scrolled = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (mStarted && !scrolled) {

                    var tmpX = event.x
                    var tmpY = event.y
                    var deltaX = Math.abs(tmpX - touchDownX)
                    var deltaY = Math.abs(tmpY - touchDownY)
                    if (deltaX > deltaY && deltaX > mTouchSlop) {
                        scroll(if (tmpX > touchDownX) GameUtil.Direction.Right else GameUtil.Direction.Left)
                        scrolled = true
                    } else if (deltaY > deltaX && deltaY > mTouchSlop) {
                        scroll(if (tmpY > touchDownY) GameUtil.Direction.Bottom else GameUtil.Direction.Top)
                        scrolled = true
                    }
                }
            }
        }
        return true
    }

    var totalScore: Int = 0
    fun addScore(scoreValue: Int) {
        totalScore += scoreValue

        onScoreChanged()
    }

    fun onScoreChanged() {
        ((context as? GameActivity))?.onScoreChanged(totalScore)
    }


}