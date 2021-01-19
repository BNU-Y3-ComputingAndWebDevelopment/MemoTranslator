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
        fancyAboutPage.setDescription("Student of BSc in Computing and Web Development ");
        fancyAboutPage.setAppIcon(R.drawable.cakepop);
        fancyAboutPage.setAppName("MemoTranslator App");
        fancyAboutPage.setVersionNameAsAppSubTitle("1.0.0");
        fancyAboutPage.setAppDescription("The MemoTranslator app was developed to translate text from over 100 different languages. The user can save translations to the favourites list and share them from favourites section into social media. It uses Google Translator Toolkit for universal translation of text and it has a google option for reversing the translation from one language to another.");
        fancyAboutPage.addEmailLink("al.petingazorro@gmail.com");
        fancyAboutPage.addFacebookLink("https://www.facebook.com/ana.zorro.31");
        fancyAboutPage.addTwitterLink("https://twitter.com/anazorro8");
    }
}