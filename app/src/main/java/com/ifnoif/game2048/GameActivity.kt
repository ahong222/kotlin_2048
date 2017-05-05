package com.ifnoif.game2048

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_game.*

/**
 * Created by shen on 17/4/11.
 */

class GameActivity : Activity() {

    var mBestScore = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        start.setOnClickListener {
            gameView.start()
        }

        mBestScore = PreferenceManager.getDefaultSharedPreferences(applicationContext).getInt("best_score", 0)
        bestScore.text = "" + mBestScore
    }

    fun onScoreChanged(scoreValue: Int) {
        score.text = scoreValue.toString()
    }

    override fun onPause() {
        super.onPause()
    }

    fun onGameComplete() {
        var score = Integer.parseInt(score.text.toString())
        if (score > mBestScore) {
            mBestScore = score;
            bestScore.text = "" + score;

            PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putInt("best_score", score).apply()
        }
    }
}