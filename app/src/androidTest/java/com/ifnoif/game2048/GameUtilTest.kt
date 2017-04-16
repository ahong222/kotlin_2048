package com.ifnoif.game2048

import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by shen on 17/4/16.
 */
@RunWith(AndroidJUnit4::class)
class GameUtilTest {

    @Test
    fun testScroll() {
        var intArray: Array<Array<Int>> = arrayOf(kotlin.arrayOf(4, 0, 0, 0), arrayOf(4, 0, 0, 0), arrayOf(8, 0, 0, 0), arrayOf(8, 0, 0, 0));
        var array: Array<Array<GameUtil.Block>> = arrayOf(
                Array(4, { x -> GameUtil.Block(x, 0, (intArray[0])[x]) }),
                Array(4, { x -> GameUtil.Block(x, 1, (intArray[1])[x]) }),
                Array(4, { x -> GameUtil.Block(x, 2, (intArray[2])[x]) }),
                Array(4, { x -> GameUtil.Block(x, 3, (intArray[3])[x]) }));

        var gameUtil = GameUtil();
        gameUtil.debug = true;
        gameUtil.test(array, GameUtil.Direction.Bottom)
    }
}