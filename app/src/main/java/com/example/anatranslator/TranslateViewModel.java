package com.example.anatranslator;

import android.app.Application;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.List;

public class TranslateViewModel extends AndroidViewModel {
    private static final int NUM_TRANSLATORS = 3;

    private final RemoteModelManager modelManager;
    private final LruCache<TranslatorOptions, Translator> translators =
            new LruCache<TranslatorOptions, Translator>(NUM_TRANSLATORS){

                @Override
                protected Translator create(TranslatorOptions key) {
                    return Translation.getClient(key);
                }

                @Override
                protected void entryRemoved(boolean evicted, TranslatorOptions key, Translator oldValue, Translator newValue) {
                    oldValue.close();
                }
            };

    MutableLiveData<TranslateLanguage.Language> sourceLang = new MutableLiveData<>();
    MutableLiveData<TranslateLanguage.Language> targetLang = new MutableLiveData<>();
    MutableLiveData<String> sourceText = new MutableLiveData<>();
    MediatorLiveData<ResultOrError> translatedText = new MediatorLiveData<>();
    //If there are a sentence or more to translate it will check for the available translation models
    MutableLiveData<List<String>> availableModels =
            new MutableLiveData<>();

    public TranslateViewModel(@NonNull Application application) {
        super(application);
        modelManager = RemoteModelManager.getInstance();

        // Create a translation result or error object.
        final OnCompleteListener<String> processTranslation = new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    translatedText.setValue(new ResultOrError(task.getResult(), null));
                } else {
                    translatedText.setValue(new ResultOrError(null, task.getException()));
                }
                // Update the list of downloaded models as more may have been
                // automatically downloaded due to requested translation.
                fetchDownloadedModels();
            }
        };

        // Start translation if any of the following change: input text, source lang, target lang.
        translatedText.addSource(sourceText, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                translate().addOnCompleteListener(processTranslation);
            }
        });
}