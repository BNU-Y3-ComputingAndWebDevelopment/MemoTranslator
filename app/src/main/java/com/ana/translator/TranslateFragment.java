package com.ana.translator;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.ana.translator.Database.Database;
import com.ana.translator.databinding.TranslateFragmentBinding;
import com.ana.translator.TranslateViewModel.Language;
import com.ana.translator.TranslateViewModel.ResultOrError;
import com.ana.translator.model.Favorites;
import com.ana.translator.recyclerview.RecyclerViewAdopter;

import java.util.ArrayList;
import java.util.List;

public class TranslateFragment extends BaseFragment {

    private RecyclerViewAdopter mAdopter;
    private Database mDatabase;
    private TranslateFragmentBinding mBinding;
    private ClipboardManager cpb;

    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mDatabase = Database.getLocalDatabase(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.translate_fragment, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = DataBindingUtil.bind(view);
        setUpRecyclerView(mBinding.favoritesRecyclerView);

        final TranslateViewModel viewModel =
                ViewModelProviders.of(this).get(TranslateViewModel.class);

        final ArrayAdapter<Language> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, viewModel.getAvailableLanguages());
        mBinding.sourceLangSelector.setAdapter(adapter);
        mBinding.targetLangSelector.setAdapter(adapter);
        mBinding.sourceLangSelector.setSelection(adapter.getPosition(new Language("en")));
        mBinding.targetLangSelector.setSelection(adapter.getPosition(new Language("es")));
        mBinding.sourceLangSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setProgressText(mBinding.targetText);
                viewModel.sourceLang.setValue(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBinding.targetText.setText("");
            }
        });
        mBinding.targetLangSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setProgressText(mBinding.targetText);
                viewModel.targetLang.setValue(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mBinding.targetText.setText("");
            }
        });

        mBinding.buttonSwitchLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressText(mBinding.targetText);
                int sourceLangPosition = mBinding.sourceLangSelector.getSelectedItemPosition();
                mBinding.sourceLangSelector.setSelection(mBinding.targetLangSelector.getSelectedItemPosition());
                mBinding.targetLangSelector.setSelection(sourceLangPosition);
            }
        });

        mBinding.buttonSyncSource.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Language language = adapter.getItem(mBinding.sourceLangSelector.getSelectedItemPosition());
                if (isChecked) {
                    viewModel.downloadLanguage(language);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Are you Sure to Delete Model")
                            .setCancelable(false)
                            .setTitle("Are you Sure")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    viewModel.deleteLanguage(language);
                                }
                            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog alert = builder.create();
                    alert.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(requireContext().getResources().getColor(android.R.color.holo_green_light));
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getResources().getColor(android.R.color.holo_red_dark));
                        }
                    });
                    alert.show();
                }
            }
        });
        mBinding.buttonAddTofavorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!mBinding.sourceText.getText().toString().isEmpty()) {
                        mDatabase.userDao().addNewfavorites(new Favorites(mBinding.sourceText.getText().toString(), mBinding.targetText.getText().toString()));
                    }
            }
        });

        cpb=(ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);


        mBinding.buttonSyncTarget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Language language = adapter.getItem(mBinding.targetLangSelector.getSelectedItemPosition());
                if (isChecked) {
                    viewModel.downloadLanguage(language);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage("Are you Sure to Delete Model")
                            .setCancelable(false)
                            .setTitle("Are you Sure")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    viewModel.deleteLanguage(language);
                                }
                            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog alert = builder.create();
                    alert.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(requireContext().getResources().getColor(android.R.color.holo_green_light));
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getResources().getColor(android.R.color.holo_red_dark));
                        }
                    });
                    alert.show();

                }
            }
        });

        mBinding.sourceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setProgressText(mBinding.targetText);
                viewModel.sourceText.postValue(s.toString());
            }
        });
        viewModel.translatedText.observe(this, new Observer<ResultOrError>() {
            @Override
            public void onChanged(TranslateViewModel.ResultOrError resultOrError) {
                if (resultOrError.error != null) {
                    mBinding.sourceText.setError(resultOrError.error.getLocalizedMessage());
                } else {
                    mBinding.targetText.setText(resultOrError.result);
                }
            }
        });

        viewModel.availableModels.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> firebaseTranslateRemoteModels) {
                String output = getContext().getString(R.string.downloaded_models_label,
                        firebaseTranslateRemoteModels);
                mBinding.downloadedModels.setText(output);
                mBinding.buttonSyncSource.setChecked(firebaseTranslateRemoteModels.contains(
                        adapter.getItem(mBinding.sourceLangSelector.getSelectedItemPosition()).getCode()));
                mBinding.buttonSyncTarget.setChecked(firebaseTranslateRemoteModels.contains(
                        adapter.getItem(mBinding.targetLangSelector.getSelectedItemPosition()).getCode()));
            }
        });

    mBinding.btndcp2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String data = mBinding.targetText.getText().toString().trim();
            if (!data.isEmpty()) {
                ClipData temp = ClipData.newPlainText("text", data);
                cpb.setPrimaryClip(temp);
                Toast.makeText(getContext(),"Copied",Toast.LENGTH_LONG).show();
            }
        }
    });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDatabase.userDao().getAll().observe(this, new Observer<List<Favorites>>() {
            @Override
            public void onChanged(List<Favorites> histories) {
                List<Object> list = new ArrayList<>();
                for (Favorites obj: histories) {
                    list.add(obj);
                }
                mAdopter.setListData(list);
            }
        });
    }

    @Override
    protected RecyclerView.Adapter onPrepareAdopter() {
        List<Object> objectList = new ArrayList<>();
        mAdopter = new RecyclerViewAdopter(getContext(),R.layout.item_hisotry,objectList);
        return mAdopter;
    }

    private void setProgressText(TextView tv) {
        tv.setText(getContext().getString(R.string.translate_progress));
    }

}
