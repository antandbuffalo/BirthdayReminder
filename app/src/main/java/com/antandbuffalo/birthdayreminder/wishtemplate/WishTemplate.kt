package com.antandbuffalo.birthdayreminder.wishtemplate

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.antandbuffalo.birthdayreminder.Constants
import com.antandbuffalo.birthdayreminder.R
import com.antandbuffalo.birthdayreminder.Util
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class WishTemplate : Activity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wish_template)

        val settingsPreference = Util.getSharedPreference()
        val currentTemplate = settingsPreference.getString(Constants.PREFERENCE_WISH_TEMPLATE, Constants.WISH_TEMPLATE_DEFAULT)

        val save = findViewById<View>(R.id.save) as ImageButton
        save.setBackgroundResource(R.drawable.save_button)

        val cancel = findViewById<View>(R.id.cancel) as ImageButton
        cancel.setBackgroundResource(R.drawable.cancel_button)

        val editText = findViewById (R.id.wishTemplate1) as EditText
        editText.setText(currentTemplate)

        val adView = findViewById (R.id.adView) as AdView;
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        save.setOnClickListener {
            val editor = settingsPreference.edit()
            editor.putString(Constants.PREFERENCE_WISH_TEMPLATE, editText.text.toString())
            editor.commit()

            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}
