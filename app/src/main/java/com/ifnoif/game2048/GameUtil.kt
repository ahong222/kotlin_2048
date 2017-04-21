package com.ifnoif.game2048

import android.graphics.Point
import android.util.Log
import java.util.*

/**
 * Created by shen on 17/4/11.
 */

class GameUtil {

    var debug = true;

    enum class Direction {
        Left, Right, Top, Bottom
    }

    /**
     * 0,1,2,3
     * 1,2,3,4
     * 2,3,4,5
     * 3,4,5,6
     */
    fun scroll(array: Array<Array<Block>>, direction: Direction, listPoint: ArrayList<Point>, listAction: ArrayList<Block>) {
        onStartScroll(array);
        println("start scroll direction:" + direction);
        dumpArray(array, "original")
        if (direction != Direction.Right) {
            convert(array, direction, false)
            dumpArray(array, "after convert")
        }

        addSameToRight(array)
        dumpArray(array, "addSameToRight")

        removeBlank(array)
        dumpArray(array, "removeBlank")

        if (direction != Direction.Right) {
            convert(array, getBackDirection(direction), false)

            dumpArray(array, "getBackDirection")
        }


        getBlank(array, listPoint)

        if (direction != Direction.Right) {
            convert(array, getBackDirection(direction), true)
        }
        onEndScroll(array);

        getAction(array, listAction);

        dumpEndArray(array, "end")
        ("end scroll =========").println();
    }

    fun addSameToRight(array: Array<Array<Block>>) {
        var size: Int = array.size;
        for (y in (0..size - 1)) {
            var largeIndexValue = 0;
            var lastLargeIndex = -1;
            for (x in (size - 2 downTo 0)) {
                var tY = y;
                var tX = x;
                if ((array[y])[x + 1].value != 0) {
                    lastLargeIndex = x + 1;
                    largeIndexValue = (array[y])[x + 1].value;
                }
                if ((array[y])[x].value == 0) {
                    continue;
                } else if (largeIndexValue == 0) {
                    continue;
                }
                if ((array[y])[x].value == largeIndexValue) {
                    //变化新X
                    (array[y])[lastLargeIndex].pX = (array[y])[x].pX;
                    (array[y])[lastLargeIndex].pY = (array[y])[x].pY;
                    (array[y])[lastLargeIndex].value *= 2;

                    (array[y])[x].value = 0;
                    (array[y])[x].merged = true;


                    (array[y])[lastLargeIndex].changeX = lastLargeIndex;
                    (array[y])[lastLargeIndex].changeY = y;

                    largeIndexValue = 0;
                }
            }
        }
    }

    fun removeBlank(array: Array<Array<Block>>) {
        var size: Int = array.size;
        for (y in (0..size - 1)) {
            for (x in (size - 1 downTo 0)) {
                if ((array[y])[x].value == 0) {
                    //去除空格
                    //把小Index往大Index移动
                    removeOnBlank(array, x, y);
                }
            }
        }
    }

    fun removeOnBlank(array: Array<Array<Block>>, emptyX: Int, currentY: Int) {
        //去除空格
        //把小Index往大Index移动
        for (newX in (emptyX downTo 1)) {

            if ((array[currentY])[newX - 1].value == 0) {
                removeOnBlank(array, newX - 1, currentY);
            }
            var tmp = (array[currentY])[newX];
            (array[currentY])[newX] = (array[currentY])[newX - 1];
            (array[currentY])[newX - 1] = tmp;
        }
    }


    fun getBlank(array: Array<Array<Block>>, listPoint: ArrayList<Point>) {
        var size: Int = array.size;
        for (y in (0..size - 1)) {
            for (x in (0..size - 1)) {
                if ((array[y])[x].value == 0) {
                    listPoint.add(Point(x, y));
                }
            }
        }
    }

    fun getBackDirection(direction: Direction): Direction {
        if (direction == Direction.Left) {
            return Direction.Left;
        } else if (direction == Direction.Top) {
            return Direction.Bottom;
        } else if (direction == Direction.Right) {
            return Direction.Left
        } else if (direction == Direction.Bottom) {
            return Direction.Top
        }
        return direction;
    }

    fun convert(array: Array<Array<Block>>, direction: Direction, convertChangedSize: Boolean) {
        if (direction == Direction.Right) {
            return;
        }
        var size: Int = array.size;
        if (direction == Direction.Left) {
            //(x,y) 与 (size-x,y) 互换
            realConvert(array, { x: Int, y: Int, size: Int -> size - 1 - x }, { x: Int, y: Int, size: Int -> y }, convertChangedSize);
        } else if (direction == Direction.Bottom) {
            //(x,y) 与 (y,size-x) 互换
            realConvert(array, { x: Int, y: Int, size: Int -> y }, { x: Int, y: Int, size: Int -> size - 1 - x }, convertChangedSize);
        } else if (direction == Direction.Top) {
            //(x,y) 与 (size-y,x) 互换
            realConvert(array, { x: Int, y: Int, size: Int -> size - 1 - y }, { x: Int, y: Int, size: Int -> x }, convertChangedSize);
        }
    }

    /**
     * convertChangedSize：是否只对调changeX或changeY
     */
    fun realConvert(array: Array<Array<Block>>, convertX: (x: Int, y: Int, size: Int) -> Int, convertY: (x: Int, y: Int, size: Int) -> Int, convertChangedSize: Boolean) {
        var size: Int = array.size;

        var changedArray: Array<Array<Int>> = Array(size, { y -> Array(size, { index -> 0 }) });

        for (y in (0..size - 1)) {
            for (x in (0..size - 1)) {
                if ((changedArray[y])[x] != 0) {
                    //已经转换过
                    continue;
                }
                if (convertChangedSize) {
                    var block1: Block = (array[y])[x]
                    var pX = block1.changeX
                    var pY = block1.changeY
                    if (pX >= 0 && pY >= 0) {
                        block1.changeX = convertX(pX, pY, size)
                        block1.changeY = convertY(pX, pY, size)
                    }
                } else {

                    var tmp = Block(0, 0, 0);
                    tmp = (array[y])[x];
                    (array[y])[x] = (array[convertY(x, y, size)])[convertX(x, y, size)];
                    (array[convertY(x, y, size)])[convertX(x, y, size)] = tmp;
                    (changedArray[convertY(x, y, size)])[convertX(x, y, size)] = 1;
                }

            }
        }
    }

    fun dumpArray(array: Array<Array<Block>>, tag: String) {
        (tag + " dumpArray========").println();
        var size: Int = array.size;
        for (y in (0..size - 1)) {
            var logStr: StringBuilder = StringBuilder();
            for (x in (0..size - 1)) {
                var strPXPY = "(" + (array[y])[x].pX + "," + (array[y])[x].pY + "," + (array[y])[x].merged + ")";
                logStr.append(" " + (array[y])[x].value + strPXPY + " ");
            }
            println(logStr.toString());
        }
    }

    fun getAction(array: Array<Array<Block>>, listAction: ArrayList<Block>) {
        var size: Int = array.size;

        for (y in (0..size - 1)) {
            for (x in (0..size - 1)) {

                if ((array[y])[x].value != 0 && !(array[y])[x].merged && (array[y])[x].y * 10 + (array[y])[x].x != ((array[y])[x].pX * 10 + (array[y])[x].pY)) {
                    listAction.add((array[y])[x].copy())
                }
            }
        }
    }

    fun dumpEndArray(array: Array<Array<Block>>, tag: String) {
        println(tag + " dumpEndArray========");
        var size: Int = array.size;

        for (y in (0..size - 1)) {
            var logStr: StringBuilder = StringBuilder();
            for (x in (0..size - 1)) {
                logStr.append(" " + (array[y])[x].value + " ");
                var str = "";
                if (!(array[y])[x].merged && (array[y])[x].value != (array[y])[x].pValue) {
                    str = " 改变位置：" + (array[y])[x].changeX + "," + (array[y])[x].changeY;
                }

                if ((array[y])[x].value != 0 && !(array[y])[x].merged && (array[y])[x].y * 10 + (array[y])[x].x != ((array[y])[x].pX * 10 + (array[y])[x].pY)) {
                    (tag + " remove==" + (array[y])[x].pX + "," + (array[y])[x].pY + " 移动到:" + (array[y])[x].x + "," + (array[y])[x].y + str).println();
                }
            }
        }
    }

    fun onStartScroll(array: Array<Array<Block>>) {
//        var size: Int = array.size;
//        for (y in (0..size - 1)) {
//            for (x in (0..size - 1)) {
//                (array[y])[x].reset();
//            }
//        }

//        使用函数
//        array.forEach { arr->arr.forEach { item->item.reset() } }
//        array.flatten().forEach { item->item.reset() }
//        array.flatMap { item->item.toList()}.forEach { Block::reset }
//        array.flatten().forEach { Block::reset }
        array.flatten().forEach(Block::reset)
    }

    fun onEndScroll(array: Array<Array<Block>>) {
        var size: Int = array.size;
        for (y in (0..size - 1)) {
            for (x in (0..size - 1)) {
                (array[y])[x].x = x;
                (array[y])[x].y = y;
            }
        }
    }

    fun test() {
        test(GameUtil.Direction.Top);
        test(GameUtil.Direction.Bottom);
        test(GameUtil.Direction.Right);
        test(GameUtil.Direction.Left);
    }

    fun test(direction: Direction) {
        var intArray: Array<Array<Int>> = arrayOf(kotlin.arrayOf(2, 2, 4, 4), arrayOf(2, 2, 4, 2), arrayOf(4, 8, 8, 4), arrayOf(4, 2, 8, 4));
        var array: Array<Array<Block>> = arrayOf(
                Array(4, { x -> Block(x, 0, (intArray[0])[x]) }),
                Array(4, { x -> Block(x, 1, (intArray[1])[x]) }),
                Array(4, { x -> Block(x, 2, (intArray[2])[x]) }),
                Array(4, { x -> Block(x, 3, (intArray[3])[x]) }));
        test(array, direction);
    }

    fun test(array: Array<Array<Block>>, direction: Direction) {
        var listPoint: ArrayList<Point> = ArrayList<Point>();
        var listAction: ArrayList<Block> = ArrayList<Block>();
        scroll(array, direction, listPoint, listAction);
    }

    open class Block1  {
        //最终的Value
        var value: Int = 0;
        //最终的位置
        var x: Int = 0;
        var y: Int = 0;

        //临时变量
        var pX: Int = 0;
        var pY: Int = 0;
        var pValue: Int = 0;
        var changeX: Int = -1;
        var changeY: Int = -1;
        var merged = false;

        //x,y 与 px,py不相等表示有移动
        //value与pValue不相等表示有合并，value>pValue表示合并，value＝0表示被合并，要删除View
        //changeX,changeY表示改变值的坐标

        constructor(x: Int, y: Int, value: Int) {
            this.x = x;
            this.y = y;
            this.value = value;
        }

        init {
            pX = x;
            pY = y;
            pValue = value;
        }

        fun copy(): Block1 {
            var block = Block1(x, y, value);
            block.pX = pX;
            block.pY = pY;
            block.pValue = pValue;
            block.changeX = changeX;
            block.changeY = changeY;
            block.merged = merged;
            return block
        }

        fun reset() {
            pX = x;
            pY = y;
            pValue = value;
            merged = false;
            changeX = -1;
            changeY = -1;
        }

        fun getActionStr(): String {
            var block: Block1 = this;
            var direct = if (block.pX == block.x) (if (block.y > block.pY) "向下" else "向上") else (if (block.x > block.pX) "向右" else "向左");
            return "方块从 px:" + block.pX + " pY:" + block.pY + " " + direct + "滑到:" + block.x + "," + block.y + (if (block.changeX < 0) "" else (",并移除(" + block.changeX + "," + block.changeY + ")"))
        }
    }


    /**
     * 最终的位置：x,y,value
     * 临时变量:px,py,pValue,changeX,changeY,merged
     * x,y 与 px,py不相等表示有移动
     * value与pValue不相等表示有合并，value>pValue表示合并，value＝0表示被合并，要删除View
     * changeX>=0 && changeY>=0表示改变值的坐标
     */
    data class Block(var x: Int, var y: Int, var value: Int,
                     var pX: Int = x,
                     var pY: Int = y,
                     var pValue: Int = value,
                     var changeX: Int = -1,
                     var changeY: Int = -1,
                     var merged: Boolean = false) {
        fun reset() {
            pX = x;
            pY = y;
            pValue = value;
            merged = false;
            changeX = -1;
            changeY = -1;
        }

        fun getActionStr(): String {
            var block: Block = this;
            var direct = if (block.pX == block.x) (if (block.y > block.pY) "向下" else "向上") else (if (block.x > block.pX) "向右" else "向左");
            return "方块从 px:" + block.pX + " pY:" + block.pY + " " + direct + "滑到:" + block.x + "," + block.y + (if (block.changeX < 0) "" else (",并移除(" + block.changeX + "," + block.changeY + ")"))
        }
    }

    //扩展类方法
    fun Any.println() {
        if (debug) println(toString())
    }
}
