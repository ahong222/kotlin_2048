package com.ifnoif.game2048

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.dialog_enter_nickname.view.*

/**
 * Created by shen on 17/4/11.
 */

class GameActivity : Activity() {

    val PREF_BEST_SCORE = "best_score"
    val PREF_NICK_NAME = "nick_name"

    var mBestScore = 0
    lateinit var gameController: GameController

//    lateinit var mServerUtil: ServerUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameController = GameController(applicationContext)
        gameView.gameConfig = gameController.getDefaultGameConfig()

        start.setOnClickListener {
            gameView.start()
        }

        mBestScore = PreferenceManager.getDefaultSharedPreferences(applicationContext).getInt(PREF_BEST_SCORE, 0)
        bestScore.text = "" + mBestScore

        var nickName = PreferenceManager.getDefaultSharedPreferences(applicationContext).getString(PREF_NICK_NAME, null)
        if (!TextUtils.isEmpty(nickName)) {
//            mServerUtil.init(nickName, object : ValueEventListener {
//                override fun onDataChange(var1: DataSnapshot) {
//                    var score = var1.getValue(Int::class.java)
//                    Log.d("syh", "score:" + score + "  mBestScore:" + mBestScore)
//                }
//
//                override fun onCancelled(var1: DatabaseError) {
//
//                }
//            })
        }

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
            mBestScore = score
            bestScore.text = "" + score

            PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putInt(PREF_BEST_SCORE, score).apply()
        }

        if (TextUtils.isEmpty(PreferenceManager.getDefaultSharedPreferences(applicationContext).getString(PREF_NICK_NAME, null))) {
            enterNickName()
        }
    }

    fun enterNickName() {
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_enter_nickname, null, false)
        var dialog = AlertDialog.Builder(this).setTitle(R.string.tips).setView(view)
                .setPositiveButton(R.string.dialog_confirm, { dialog, which -> onEnterNickName(view.nickName.text.toString().trim()) })
                .setNegativeButton(R.string.dialog_cancel, { dialog, which -> }).create()
        dialog.show()
    }

    fun onEnterNickName(nickName: String) {
        if (nickName.length == 0) {
            enterNickName()
            return
        }
        PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putString(PREF_NICK_NAME, nickName).apply()


    }

}