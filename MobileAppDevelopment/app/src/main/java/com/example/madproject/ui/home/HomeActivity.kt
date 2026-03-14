package com.example.madproject.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.madproject.R
import com.example.madproject.ui.addword.AddWordActivity
import com.example.madproject.ui.devicestats.DeviceStatsActivity
import com.example.madproject.ui.quiz.QuizActivity
import com.example.madproject.ui.viewwords.ViewWordActivity

/** just a nav hub.
 *  no logic and no viewmodel needed
*/

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.addWord).setOnClickListener {
            startActivity(Intent(this, AddWordActivity::class.java))
        }
        findViewById<Button>(R.id.viewWords).setOnClickListener {
            startActivity(Intent(this, ViewWordActivity::class.java))
        }
        findViewById<Button>(R.id.quiz).setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }
        findViewById<Button>(R.id.deviceStats).setOnClickListener {
            startActivity(Intent(this, DeviceStatsActivity::class.java))
        }
    }
}
