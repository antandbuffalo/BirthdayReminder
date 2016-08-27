package com.antandbuffalo.birthdayreminder.update;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DataHolder;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.TabsAdapter;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;

import java.util.Calendar;
import java.util.Date;

public class Update extends Activity {
    TextView selectedDate;
    EditText name;
    DatePicker datePicker;
    DateOfBirth currentDOB;
    Intent intent;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        currentDOB = (DateOfBirth)getIntent().getSerializableExtra("currentDOB");

        selectedDate = (TextView)findViewById(R.id.selectedDate);
        selectedDate.setText(Util.getStringFromDate(currentDOB.getDobDate(), Constants.ADD_NEW_DATE_FORMAT));
        name = (EditText)findViewById(R.id.personName);
        name.setText(currentDOB.getName());
        datePicker = (DatePicker)findViewById(R.id.perosnDateOfBirth);
        datePicker.setMaxDate(new Date().getTime());

        cal = Calendar.getInstance();
        cal.setTime(currentDOB.getDobDate());
        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

        Button delete = (Button)findViewById(R.id.delete);
        delete.setBackgroundResource(R.drawable.delete_button);

        Button cancel = (Button)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        Button save = (Button)findViewById(R.id.save);
        save.setBackgroundResource(R.drawable.save_button);

        intent = new Intent();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateOfBirthDBHelper.deleteRecordForTheId(currentDOB.getDobId());
                currentDOB = null;
                System.out.println("deleted");
                Toast toast = Toast.makeText(getApplicationContext(), Constants.NOTIFICATION_DELETE_MEMBER_SUCCESS, Toast.LENGTH_SHORT);
                toast.show();

                setResult(RESULT_OK, intent);
                clearInputs();

                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void clearInputs() {
        name.setText("");
        cal = Calendar.getInstance();
        cal.setTime(new Date());
        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        selectedDate.setText(Util.getStringFromDate(new Date(), Constants.ADD_NEW_DATE_FORMAT));
    }
}
