package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityIntentHandlerBinding

class IntentHandlerActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntentHandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.error_empty_content, LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
                return@let
            }

            with(binding.intentPost) {
                tvContent.text = text
                ivAvatar.setImageResource(R.drawable.baseline_question_mark_24)
                tvAuthor.text = "?"
                tvPublished.text = "now"
                ibLikes.text = "0"
                ibShares.text = "0"
                tvViewsCount.text = "0"
            }

            binding.fabSave.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(Intent.EXTRA_TEXT, text)
                setResult(Activity.RESULT_OK, intent)
                startActivity(intent)
                finish()
            }
            binding.fabCancel.setOnClickListener {
                finish()
            }
        }
    }
}






