package com.ana.translator;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateModelManager;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TranslateViewModel extends AndroidViewModel {
    private final FirebaseTranslateModelManager modelManager;
    MutableLiveData<Language> sourceLang = new MutableLiveData<>();
    MutableLiveData<Language> targetLang = new MutableLiveData<>();
    MutableLiveData<String> sourceText = new MutableLiveData<>();
    MediatorLiveData<ResultOrError> translatedText = new MediatorLiveData<>();
    MutableLiveData<List<String>> availableModels =
            new MutableLiveData<>();
    public TranslateViewModel(@NonNull Application application) {
        super(application);
        modelManager = FirebaseTranslateModelManager.getInstance();

        final OnCompleteListener<String> processTranslation = new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    translatedText.setValue(new ResultOrError(task.getResult(), null));
                } else {
                    translatedText.setValue(new ResultOrError(null, task.getException()));
                }
                fetchDownloadedModels();
            }
        };

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

        fetchDownloadedModels();
    }

    List<Language> getAvailableLanguages() {
        List<Language> languages = new ArrayList<>();
        Set<Integer> languageIds = FirebaseTranslateLanguage.getAllLanguages();
        for (Integer languageId : languageIds) {
            languages.add(new Language(FirebaseTranslateLanguage.languageCodeForLanguage(languageId)));
        }
        return languages;
    }

    private FirebaseTranslateRemoteModel getModel(Integer languageCode) {
        return new FirebaseTranslateRemoteModel.Builder(languageCode).build();
    }

    private void fetchDownloadedModels() {
        modelManager.getAvailableModels(FirebaseApp.getInstance()).addOnSuccessListener(
                new OnSuccessListener<Set<FirebaseTranslateRemoteModel>>() {
                    @Override
                    public void onSuccess(Set<FirebaseTranslateRemoteModel> remoteModels) {
                        List<String> modelCodes = new ArrayList<>(remoteModels.size());
                        for (FirebaseTranslateRemoteModel model : remoteModels) {
                            modelCodes.add(model.getLanguageCode());
                        }
                        Collections.sort(modelCodes);
                        availableModels.setValue(modelCodes);
                    }
                });
    }
    void downloadLanguage(Language language) {
        FirebaseTranslateRemoteModel model =
                getModel(FirebaseTranslateLanguage.languageForLanguageCode(language.getCode()));
        modelManager.downloadRemoteModelIfNeeded(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                fetchDownloadedModels();
            }
        });
    }
    void deleteLanguage(Language language) {
        FirebaseTranslateRemoteModel model =
                getModel(FirebaseTranslateLanguage.languageForLanguageCode(language.getCode()));
        modelManager.deleteDownloadedModel(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                fetchDownloadedModels();
            }
        });

    }

    public Task<String> translate() {
        final TaskCompletionSource<String> translateTask = new TaskCompletionSource<String>();
        final String text = sourceText.getValue();
        final Language source = sourceLang.getValue();
        final Language target = targetLang.getValue();
        if (source == null || target == null || text == null || text.isEmpty()) {
            return Tasks.forResult("");
        }
        int sourceLangCode =
                FirebaseTranslateLanguage.languageForLanguageCode(source.getCode());
        int targetLangCode =
                FirebaseTranslateLanguage.languageForLanguageCode(target.getCode());
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(sourceLangCode)
                .setTargetLanguage(targetLangCode)
                .build();
        final FirebaseTranslator translator =
                FirebaseNaturalLanguage.getInstance().getTranslator(options);
        return translator.downloadModelIfNeeded().continueWithTask(new Continuation<Void,
                Task<String>>() {
            @Override
            public Task<String> then(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    return translator.translate(text);
                } else {
                    Exception e = task.getException();
                    if (e == null) {
                        e = new Exception(getApplication().getString(R.string.unknown_error));
                    }
                    return Tasks.forException(e);
                }
            }
        });
    }

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

    static class Language implements Comparable<Language> {
        private String code;

        Language(String code) {
            this.code = code;
        }

        String getDisplayName() {
            return new Locale(code).getDisplayName();
        }

        String getCode() {
            return code;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof Language)) {
                return false;
            }

            Language otherLang = (Language) o;
            return otherLang.code.equals(code);
        }

        @NonNull
        public String toString() {
            return code + " - " + getDisplayName();
        }

        @Override
        public int hashCode() {
            return code.hashCode();
        }

        @Override
        public int compareTo(@NonNull Language o) {
            return this.getDisplayName().compareTo(o.getDisplayName());
        }
    }
}
