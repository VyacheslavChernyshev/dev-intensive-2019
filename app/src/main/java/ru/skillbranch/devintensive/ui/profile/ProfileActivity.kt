package ru.skillbranch.devintensive.ui.profile

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel
import kotlin.math.roundToInt

class ProfileActivity : AppCompatActivity() {
    companion object {
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
        const val IS_REPO_ERROR = "IS_REPO_ERROR"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    var isRepoError = false
    lateinit var viewFields: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews(savedInstanceState)
        initViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)
        outState.putBoolean(IS_REPO_ERROR, isRepoError)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getAppTheme().observe(this, Observer { updateTheme(it) })
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for ((k, v) in viewFields) {
                v.text = it[k].toString()
            }
            iv_avatar.setImageDrawable(getInitialsDrawable(profile))
        }

    }

    private fun updateTheme(mode: Int) {
        Log.d("M_ProfileActivity", "updateTheme")
        delegate.setLocalNightMode(mode)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickName" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        isRepoError = savedInstanceState?.getBoolean(IS_REPO_ERROR, false) ?: false
        showCurrentMode(isEditMode)

        btn_edit.setOnClickListener {
            if (isEditMode) {
                if (isRepoError) {
                    et_repository.text.clear()
                }
                saveProfileInfo()
            }
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }

        et_repository.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Utils.isValidGitHubURL(s.toString())) {
                    isRepoError = true
                    wr_repository.isErrorEnabled = true
                    wr_repository.error = "Невалидный адрес репозитория"
                } else {
                    isRepoError = false
                    wr_repository.isErrorEnabled = false
                    wr_repository.error = ""
                }

            }
        })
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName", "lastName", "about", "repository").contains(it.key) }
        for ((_, v) in info) {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if (isEdit) 255 else 0
        }
        ic_eye.visibility = if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit

        with(btn_edit) {
            val filter: ColorFilter? = if (isEdit) {
                PorterDuffColorFilter(
                    resources.getColor(R.color.color_accent, theme),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                null
            }
            val icon =
                if (isEdit) {
                    resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
                } else {
                    resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
                }
            background.colorFilter = filter
            setImageDrawable(icon)
        }
    }

    private fun saveProfileInfo() {
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileData(this)
        }
    }

    private fun getInitialsDrawable(
        profile: Profile,
        fontSize: Float = 64f,
        fontColor: Int = Color.WHITE
    ): Drawable {
        if (profile.initials.isEmpty()) return resources.getDrawable(R.drawable.avatar_default, theme)

        val dp = resources.displayMetrics.density
        val width = resources.getDimension(R.dimen.avatar_round_size)
        val height = resources.getDimension(R.dimen.avatar_round_size)
        @ColorRes val accentColor: Int = getAccentColorTheme()

        val bitmap = Bitmap.createBitmap((width * dp).roundToInt(), (height * dp).roundToInt(), Bitmap.Config.ARGB_8888)
        val paint = Paint()
        with(paint) {
            textSize = fontSize * dp
            color = fontColor
            textAlign = Paint.Align.CENTER
        }
        bitmap.eraseColor(accentColor)
        val canvas = Canvas(bitmap)
        canvas.drawText(profile.initials, width / 2 * dp, height / 2 * dp + paint.textSize / 3, paint)
        return BitmapDrawable(resources, bitmap)
    }

    private fun getAccentColorTheme(): Int {
        val accentColorTV = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, accentColorTV, true)
        return accentColorTV.data
    }

}
