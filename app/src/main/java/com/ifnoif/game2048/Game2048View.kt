package com.ifnoif.game2048

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import java.util.*


/**
 * Created by shen on 17/4/12.
 */

class Game2048View : FrameLayout {
    companion object {

        var TAG = "Game2048View";
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typeArray: TypedArray = context!!.obtainStyledAttributes(attrs,
                R.styleable.Game2048View)
        size = typeArray.getInteger(R.styleable.Game2048View_grid, 4);

    }

    val duration: Long = 300;
    var size: Int;
    lateinit var array: Array<Array<GameUtil.Block>>;
    var emptyPointList: ArrayList<Point> = ArrayList<Point>()
    var actionList: ArrayList<GameUtil.Block> = ArrayList<GameUtil.Block>()
    var gameUtil = GameUtil();


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(widthSize, widthSize);
    }

    fun start() {
        Log.d(TAG, "开始游戏")

        reset();
        doNext();
    }

    fun doNext() {
        if (checkGameOver()) {
            Log.d(TAG, "游戏结束")
            var dialog = AlertDialog.Builder(context).setTitle("Game Over").setMessage("是否再玩一遍")
                    .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which -> reset();doNext(); })
                    .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which -> reset();invalidate(); }).create()
            dialog.show()
            return;
        }
        var randomIndex = Random().nextInt(emptyPointList.size);
        var point = emptyPointList.removeAt(randomIndex);


        var randomValue = getRandomValue();
        (array[point.y])[point.x].value = randomValue;

        var view = createView(point, randomValue);
        addView(view, getLayoutParams(point));

        view.translationX = (point.x * width.toFloat()) / size;
        view.translationY = (point.y * height.toFloat()) / size;

        invalidate();

        Log.d(TAG, "生成一个方块(" + point.x + "," + point.y + "),大小:" + randomValue);
    }

    fun scroll(direction: GameUtil.Direction) {
        Log.d(TAG, "往" + direction + "滑动");


        emptyPointList.clear();
        actionList.clear();

        gameUtil.scroll(array, direction, emptyPointList, actionList);

        Log.d(TAG, "所有移动如下:")
        for (block in actionList) {
            Log.d(TAG, block.getActionStr())
        }


        var callback: Runnable = Runnable { doNext() };
        doActionAnimation(callback);
    }

    fun doActionAnimation(callback: Runnable) {
        if (actionList.size == 0) {
            callback.run();
            return;
        }
        Log.d(TAG, "开始滑动==")
        var animatorSet: AnimatorSet = AnimatorSet();
        animatorSet.duration = duration
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                Log.d(TAG, "动画全部完成");
                callback.run();
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })

        for (block in actionList) {
            val needRemoveView = block.changeX >= 0;
            var removeTranslation: Float = 0F;
            var targetRemoveView: BlockView? = null;
            if (needRemoveView) {
                if (block.x == block.pX) {
                    removeTranslation = Math.abs((block.changeY * width).toFloat() / size);
                } else {
                    removeTranslation = Math.abs((block.changeX * width).toFloat() / size);
                }
                targetRemoveView = getView(block.changeX, block.changeY);
            }
            var scrollHorizontal: Boolean = (block.pY == block.y);

            var startOffset = (width * (if (scrollHorizontal) block.pX else block.pY)).toFloat() / size;
            var targetOffset = (width * (if (scrollHorizontal) block.x else block.y)).toFloat() / size;

            Log.d(TAG, block.getActionStr() + " 像素从" + startOffset + "到" + targetOffset);

            var view = getView(block.pX, block.pY);
            if (view == null) {
                Log.e(TAG, "获取(" + block.pX + "," + block.pY + ")为空");
                continue;
            }
            view!!.needRemoveView = needRemoveView;
            view.removeTranslation = removeTranslation + ((if (targetOffset > startOffset) -0.02F else 0.02F) * width / size);
            view.removeTranslation = Math.min(Math.max(0F, view.removeTranslation), width.toFloat());

            var animation: ValueAnimator = ObjectAnimator.ofFloat(view, (if (scrollHorizontal) "translationX" else "translationY"), startOffset, targetOffset);
            animation.duration = duration;
            animation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    Log.d(TAG, block.getActionStr() + " ==完成,像素:" + (if (scrollHorizontal) view.translationX else view.translationY));
                }

                override fun onAnimationStart(animation: Animator?) {
                    //修改View中的坐标,不等动画结束
                    view.point.x = block.x;
                    view.point.y = block.y;
                }
            })


            animation.addUpdateListener { animator ->
                var currentTranslation = (animator!!.animatedValue as Float);

                if (targetOffset > startOffset) {

                }
                if (view.needRemoveView && (if (targetOffset > startOffset) currentTranslation >= view.removeTranslation else currentTranslation <= view.removeTranslation)) {
                    //删除合并的view
                    removeView(targetRemoveView)

                    view.needRemoveView = false;
                    view.value = view.value * 2;
                    view.setText(view.value.toString());
                    Log.d(TAG, "改变" + "(" + view.point.x + "," + view.point.y + ")的值为" + view.value);
                } else {
                    Log.d(TAG, "移动 currentTranslation：" + currentTranslation + " removeTranslation:" + view.removeTranslation);
                }
//                view.invalidate();
            }
            animatorSet.playSequentially(animation);
        }
        animatorSet.start()
    }

    fun removeView(x: Int, y: Int) {
        var view = getView(x, y);
        Log.d(TAG, "删除View(" + x + "," + y + ")" + (if (view == null) "失败" else "成功"));
        removeView(view);
    }

    fun getView(x: Int, y: Int): BlockView? {
        for (index in 0..childCount - 1) {
            var child = (getChildAt(index) as BlockView);
            if (child.point.x == x && child.point.y == y) {
                return child;
            }
        }
        return null;
    }

    /**
     * return 2 or 4
     */
    fun getRandomValue(): Int {
        return 2 + Random().nextInt(2) * 2;
    }

    fun createView(point: Point, value: Int): BlockView {
        return BlockView(context, point, value);
    }

    fun checkGameOver(): Boolean {
        //TODO 滑动不能减少位置
        return emptyPointList.size == 0;//没有空白位置，且滑动也不能减少位置
    }

    fun reset() {
        Log.d(TAG, "初始化")
        removeAllViews();
        emptyPointList.clear();
        array = Array(size, { y -> Array(size, { x -> GameUtil.Block(x, y, 0) }) });
        for (y in 0..size - 1) {
            for (x in 0..size - 1) {
                emptyPointList.add(Point(x, y))
            }
        }
    }

//    fun updateLayout(blockView: BlockView) {
//        var layoutParams = (blockView.layoutParams as LayoutParams);
//        layoutParams.width = width / size;
//        layoutParams.height = blockView.width;
//        layoutParams.leftMargin = blockView.point.x * width / size;
//        layoutParams.topMargin = blockView.point.y * height / size;
//    }

    fun getLayoutParams(point: Point): LayoutParams {
        var layoutParams = LayoutParams(width / size, width / size);
//        layoutParams.leftMargin = point.x * width / size;
//        layoutParams.topMargin = point.y * height / size;
        return layoutParams;
    }

    class BlockView : TextView {
        lateinit var point: Point;
        var value: Int;
        var needRemoveView: Boolean = false;
        var removeTranslation: Float = 0F;

        constructor(context: Context, point: Point, value: Int) : super(context) {
            this.point = point;
            this.value = value;
            gravity = Gravity.CENTER;
            setText("" + value);
            setTextColor(Color.RED);
            this.setBackgroundColor(Color.GRAY);
        }

        fun updatePoint(x: Int, y: Int) {
            point.x = x;
            point.y = y;
        }
    }

}