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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
                protected void entryRemoved(boolean evicted, TranslatorOptions key,
                                            Translator oldValue, Translator newValue) {
                    oldValue.close();
                }
            };

    MutableLiveData<TranslateLanguage.Language> sourceLang = new MutableLiveData<>();
    MutableLiveData<TranslateLanguage.Language> targetLang = new MutableLiveData<>();
    MutableLiveData<String> sourceText = new MutableLiveData<>();
    MediatorLiveData<ResultOrError> translatedText = new MediatorLiveData<>();
    //If there is one or more sentences to translate it will check for the available translation models
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
        Observer<Language> languageObserver = new Observer<Language>() {
            @Override
            public void onChanged(@Nullable Language language) {
                translate().addOnCompleteListener(processTranslation);
            }
        };
        translatedText.addSource(sourceLang, languageObserver);
        translatedText.addSource(targetLang, languageObserver);

        // Update the list of downloaded models.
        fetchDownloadedModels();

        // Gets a list of all available translation languages.
        List<Language> getAvailableLanguages() {
            List<Language> languages = new ArrayList<>();
            List<String> languageIds = TranslateLanguage.getAllLanguages();
            for (String languageId : languageIds) {
                languages.add(
                        new Language(TranslateLanguage.fromLanguageTag(languageId)));
            }
            return languages;
        }

        private TranslateRemoteModel getModel(String languageCode) {
            return new TranslateRemoteModel.Builder(languageCode).build();
        }

        // Updates the list of downloaded language models available for local translation.
        private void fetchDownloadedModels() {
            modelManager.getDownloadedModels(TranslateRemoteModel.class).addOnSuccessListener(
                    new OnSuccessListener<Set<TranslateRemoteModel>>() {
                        @Override
                        public void onSuccess(Set<TranslateRemoteModel> remoteModels) {
                            List<String> modelCodes = new ArrayList<>(remoteModels.size());
                            for (TranslateRemoteModel model : remoteModels) {
                                modelCodes.add(model.getLanguage());
                            }
                            Collections.sort(modelCodes);
                            availableModels.setValue(modelCodes);
                        }
                    });
        }

        /**
         * Holds the result of the translation or any error.
         */
        static class ResultOrError {
            final @Nullable
            String result;
            final @Nullable
            Exception error;

            ResultOrError(@Nullable String result, @Nullable Exception error) {
                this.result = result;
                this.error = error;
            }
        }
}