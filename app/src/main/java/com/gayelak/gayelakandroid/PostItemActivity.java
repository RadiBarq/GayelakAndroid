package com.gayelak.gayelakandroid;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class PostItemActivity extends AppCompatActivity {


    ViewPager viewPager;

    // this is related to the description edit_text
    EditText descriptionEditText;
    int lastSpecialRequestsCursorPosition = 0;
    String  specialRequests = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_item);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPagerPostItemAdapter viewPagerPostItemAdapter = new ViewPagerPostItemAdapter(this);
        viewPager.setAdapter(viewPagerPostItemAdapter);
        descriptionEditText = (EditText) findViewById(R.id.description);

        // This is to limit the lines of the description to six
        descriptionEditText.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               lastSpecialRequestsCursorPosition = descriptionEditText.getSelectionStart();
           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
               descriptionEditText.removeTextChangedListener(this);

               if (descriptionEditText.getLineCount() > 6) {
                   descriptionEditText.setText(specialRequests);
                   descriptionEditText.setSelection(lastSpecialRequestsCursorPosition);
               }
               else
                   specialRequests = descriptionEditText.getText().toString();

               descriptionEditText.addTextChangedListener(this);
           }
       });

    }
}
