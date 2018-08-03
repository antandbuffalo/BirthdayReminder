package com.antandbuffalo.birthdayreminder.sharewish

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.antandbuffalo.birthdayreminder.Constants
import com.antandbuffalo.birthdayreminder.DateOfBirth
import com.antandbuffalo.birthdayreminder.R
import com.antandbuffalo.birthdayreminder.Util

class ShareWish : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_wish)

        val editText = findViewById (R.id.shareWishText) as EditText

        val currentDOB = intent.getSerializableExtra("currentDOB") as DateOfBirth
        editText.setText(getCurrentTemplate() + " " + currentDOB.name)

        val btnSend = findViewById<View>(R.id.send) as ImageButton
        btnSend.setBackgroundResource(R.drawable.save_button)

        val btnCancel = findViewById<View>(R.id.cancel) as ImageButton
        btnCancel.setBackgroundResource(R.drawable.cancel_button)


        //this is for App Compat activity
//        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
//        getSupportActionBar()?.setIcon(R.mipmap.ic_notification);

    }

    fun getCurrentTemplate() : String {
        val settingsPreference = Util.getSharedPreference()
        val currentTemplate = settingsPreference.getString(Constants.PREFERENCE_WISH_TEMPLATE, Constants.WISH_TEMPLATE_DEFAULT)
        return currentTemplate;
    }
}
