package com.gayelak.gayelakandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ProfileSettingsActivity extends AppCompatActivity {


    private ListView settingslistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        settingslistView = (ListView) findViewById(R.id.settingsList);
        String[] listItems= {"الصورة الشخصية", "البريد الالكتروني", "اسم المستخدم", "كلمة المرور" ,"تسجيل الخروج"};
        ProfileSettingsAdapter adapter = new ProfileSettingsAdapter(this);
        settingslistView .setAdapter(adapter);

    }
}
