package com.gayelak.gayelakandroid;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {

    DisplayMetrics displayMetrics;
    public static String itemId;

    private String[] textArray = {

            "category-car", "phone-category", "category-aparment", "category-home",
            "category-dog", "category-sport", "category-clothes", "category-kids",
            "category-books", "category-others"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        GridView gridView =   (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new CategoriesGridViewAdapter(this, displayMetrics.heightPixels, displayMetrics.widthPixels));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //onClickItem(position);
                String categoryString = textArray[position];
                Map<String,Object> taskMap = new HashMap<String,Object>();
                taskMap.put("category", categoryString);
                FirebaseDatabase.getInstance().getReference().child("items").child(itemId).child("category").setValue(categoryString)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Write was successful!
                                // ...
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                // ...
                            }
                        });

            }
        });
    }
}
