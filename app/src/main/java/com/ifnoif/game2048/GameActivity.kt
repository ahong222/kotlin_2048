package com.ifnoif.game2048

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_game.*

/**
 * Created by shen on 17/4/11.
 */

class GameActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game);

        start.setOnClickListener { gameView.start() }

        scrollLeft.setOnClickListener { gameView.scroll(GameUtil.Direction.Left) }
        scrollRight.setOnClickListener { gameView.scroll(GameUtil.Direction.Right) }
        scrollTop.setOnClickListener { gameView.scroll(GameUtil.Direction.Top) }
        scrollBottom.setOnClickListener { gameView.scroll(GameUtil.Direction.Bottom) }
    }

    override fun onPause() {
        super.onPause()
    }
}