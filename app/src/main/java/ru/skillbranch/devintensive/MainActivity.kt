package ru.skillbranch.devintensive

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.extensions.hideKeyboard
import ru.skillbranch.devintensive.models.Bender

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var benderImage: ImageView
    lateinit var textText: TextView
    lateinit var messageText: EditText
    lateinit var sendButton: ImageView

    lateinit var benderObj: Bender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        benderImage = iv_bender
        textText = tv_text
        messageText = et_message
        sendButton = iv_send

        val status = savedInstanceState?.getString("STATUS") ?: Bender.Status.NORMAL.name
        val question = savedInstanceState?.getString("QUESTION") ?: Bender.Question.NAME.name
        benderObj = Bender(Bender.Status.valueOf(status), Bender.Question.valueOf(question))
        Log.d("M_MainActivity", "onCreate ${benderObj.status.name}, ${benderObj.question.name}")

        val (r, g, b) = benderObj.status.color
        benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)

        textText.text = benderObj.askQuestion()
        sendButton.setOnClickListener(this)
        messageText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val (phrase, color) = benderObj.listenAnswer(messageText.text.toString())
                messageText.text.clear()
                val (r, g, b) = color
                benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)
                textText.text = phrase
                hideKeyboard()
                true
            } else false
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("M_MainActivity", "OnStart")
    }

    override fun onStop() {
        super.onStop()
        Log.d("M_MainActivity", "OnStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("M_MainActivity", "OnRestart")
    }

    override fun onPause() {
        super.onPause()
        Log.d("M_MainActivity", "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d("M_MainActivity", "OnResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("M_MainActivity", "OnDestroy")
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.iv_send) {
            val (phrase, color) = benderObj.listenAnswer(messageText.text.toString())
            messageText.text.clear()
            val (r, g, b) = color
            benderImage.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)
            textText.text = phrase
            hideKeyboard()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("STATUS", benderObj.status.name)
        outState?.putString("QUESTION", benderObj.question.name)

        Log.d("M_MainActivity", "onSaveInstanceState ${benderObj.status.name}, ${benderObj.question.name}")
    }
}
