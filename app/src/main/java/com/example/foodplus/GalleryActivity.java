package com.example.foodplus;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Resources res = getResources();
        String[] basicIngredients = res.getStringArray(R.array.basic_ingredients);
        Arrays.sort(basicIngredients);
        GridView gridView = findViewById(R.id.gridView);
        IngredientAdapter adapter = new IngredientAdapter(this, basicIngredients);
        gridView.setAdapter(adapter);
    }
}