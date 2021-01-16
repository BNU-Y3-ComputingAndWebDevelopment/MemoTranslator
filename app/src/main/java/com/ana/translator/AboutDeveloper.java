package com.ana.translator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.shashank.sony.fancyaboutpagelib.FancyAboutPage;

public class AboutDeveloper extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_developer);
        setTitle("Developer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        FancyAboutPage fancyAboutPage=findViewById(R.id.fancyaboutpage);
        //fancyAboutPage.setCoverTintColor(Color.BLUE); //Optional
        fancyAboutPage.setCover(R.drawable.coverimg);
        fancyAboutPage.setName("Ana Zorro");
        fancyAboutPage.setDescription("Student of Bachelor of Science Specialization in Computer Science and Web Development ");
        fancyAboutPage.setAppIcon(R.drawable.cakepop);
        fancyAboutPage.setAppName("Ana Translator App");
        fancyAboutPage.setVersionNameAsAppSubTitle("1.0.0");
        fancyAboutPage.setAppDescription("Ana Translator app is developed for translating the text of one language into another language. It uses Google provided kit for universal translation of text. It has toggle option for reversing the translation and also stores the recent search words via the help of Favorites");
        fancyAboutPage.addEmailLink("al.petingazorro@gmail.com");
        fancyAboutPage.addFacebookLink("https://www.facebook.com/ana.zorro.31");
        fancyAboutPage.addTwitterLink("https://twitter.com/anazorro8");
    }
}