package com.antandbuffalo.birthdayreminder.sharewish

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.antandbuffalo.birthdayreminder.Constants
import com.antandbuffalo.birthdayreminder.DateOfBirth
import com.antandbuffalo.birthdayreminder.R
import com.antandbuffalo.birthdayreminder.Util
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

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

        val adView = findViewById (R.id.adView) as AdView;
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        btnSend.setOnClickListener(View.OnClickListener {
            //create the sharing intent
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = editText.text.toString();
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Send via..."))
        })

        btnCancel.setOnClickListener(View.OnClickListener {
            finish()
        })


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
