package com.example.releasesapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.releasesapp.Models.ReleaseModel;
import com.example.releasesapp.Retrofit.ApiClient;
import com.example.releasesapp.Retrofit.ApiInterface;
import com.example.releasesapp.Session.SessionManagement;
import com.example.releasesapp.databinding.ActivityNewReleaseBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewReleaseActivity extends AppCompatActivity {

    ApiInterface apiInterface;

    private ActivityNewReleaseBinding binding;

    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewReleaseBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // init vars
        final int NUM_INPUTS = 22;

        // progress bar
        binding.progressBarNewRelease.setProgress(0);
        binding.progressBarNewRelease.setMax(NUM_INPUTS);

        // text field listener
        TextWatcher formTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() == 0) {
                    binding.progressBarNewRelease.incrementProgressBy(1);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    binding.progressBarNewRelease.incrementProgressBy(-1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        binding.textInputEditTrackName.addTextChangedListener(formTextWatcher);

        binding.textInputEditArtist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0) {
                    binding.progressBarNewRelease.incrementProgressBy(1);
                }
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String[] featList = new String[]{"ft.", "feat.", "ft", "feat"};
                boolean isFeat = false;

                for (String str : featList) {
                    if (charSequence.toString().contains(str)) {
                        isFeat = true;
                    }
                }
                if (isFeat) {
                    binding.textInputLayoutArtist.setError("Main artist only! Add featured artist(s) to track name instead.");
                }
                else {
                    binding.textInputLayoutArtist.setError(null);
                }

                if (charSequence.toString().length() == 0) {
                    binding.progressBarNewRelease.incrementProgressBy(-1);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        binding.textInputEditLabel.addTextChangedListener(formTextWatcher);

        // filters for ISRC (adding all caps)
        InputFilter[] editFilters = binding.textInputEditISRC.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        binding.textInputEditISRC.setFilters(newFilters);

        binding.textInputEditISRC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() == 0) {
                    binding.progressBarNewRelease.incrementProgressBy(1);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0 && s.toString().length() < 12) {
                    binding.textInputLayoutISRC.setError("ISRC should be 12 characters.");
                }
                else {
                    binding.textInputLayoutISRC.setError(null);
                }

                if (s.toString().length() == 0) {
                    binding.progressBarNewRelease.incrementProgressBy(-1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.textInputEditUPC.addTextChangedListener(formTextWatcher);

        // release date
        binding.buttonReleaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        year = selectedYear;
                        month = selectedMonth;
                        day = selectedDay;

                        DateTimeFormatter formatter =
                                DateTimeFormatter.ofPattern("MMM/dd/yyyy" );

                        if (binding.buttonReleaseDate.getText().toString().length() == 0) {
                            binding.progressBarNewRelease.incrementProgressBy(1);
                        }

                        binding.buttonReleaseDate.setText(LocalDate.of(year, month + 1, day).format(formatter));
                    }
                };

                DatePickerDialog dateDialog;

                if (year == 0 && month == 0 && day == 0) { // initial date shown
                    dateDialog = new DatePickerDialog(binding.buttonReleaseDate.getContext(),
                            android.R.style.Theme_Material_Dialog_MinWidth,
                            datePickerListener, LocalDate.now().getYear(),
                            LocalDate.now().getMonth().getValue() - 1, LocalDate.now().getDayOfMonth());
                }
                else { // last selected date
                    dateDialog = new DatePickerDialog(binding.buttonReleaseDate.getContext(),
                            android.R.style.Theme_Material_Dialog_MinWidth,
                            datePickerListener, year,
                            month, day);
                }

                dateDialog.setTitle("Select Date");

                dateDialog.show();
            }

        });

        // grid row listeners
        MaterialButtonToggleGroup.OnButtonCheckedListener gridRowButtonListener = (group, checkedId, isChecked) -> {
            if (isChecked) {
                binding.progressBarNewRelease.incrementProgressBy(1);
            }
            else {
                binding.progressBarNewRelease.incrementProgressBy(-1);
            }
        };

        binding.buttonsGridRowOne.addOnButtonCheckedListener(gridRowButtonListener);
        binding.buttonsGridRowTwo.addOnButtonCheckedListener(gridRowButtonListener);
        binding.buttonsGridRowThree.addOnButtonCheckedListener(gridRowButtonListener);
        binding.buttonsGridRowFour.addOnButtonCheckedListener(gridRowButtonListener);

        // activity button listeners
        binding.buttonCancelRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancelIntent = new Intent(NewReleaseActivity.this, DashboardActivity.class);
                cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(cancelIntent);
            }
        });

        binding.buttonAddRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRelease(v);
            }

        });

        apiInterface = ApiClient.getApiClient(NewReleaseActivity.this).create(ApiInterface.class);
    }

    public void addRelease(View v) {
        // exit early if errors present
        if (!noErrors()) {
            Toast.makeText(NewReleaseActivity.this, "Please correct the errors.",
                    Toast.LENGTH_LONG).show();

            return;
        }

        ArrayList<MaterialButton> buttons = new ArrayList<>();

        // cycle through all buttons in grid and add to arraylist
        int numGridRows = binding.buttonsGrid.getChildCount();

        for (int i = 0; i < numGridRows; i++) {
            View gridRowView = binding.buttonsGrid.getChildAt(i);

            if (gridRowView instanceof MaterialButtonToggleGroup) {
                int numButtons = ((MaterialButtonToggleGroup) gridRowView).getChildCount();

                for (int j = 0; j < numButtons; j++) {
                    View buttonView = ((MaterialButtonToggleGroup) gridRowView).getChildAt(j);

                    buttons.add(((MaterialButton) buttonView));
                }

            }

        }

        // grab button states and write to database
        String[] buttonStates = new String[buttons.size()];

        for (int i = 0; i < buttons.size(); i++) {
            buttonStates[i] = String.valueOf(buttons.get(i).isChecked());
        }

        // associate release with user's account
        SessionManagement sessionManagement = new SessionManagement(NewReleaseActivity.this);

        String owner = sessionManagement.getSessionUid();
        String username = sessionManagement.getUsername();

        // build date
        LocalDate releaseDate;

        if (year == 0 && month == 0 && day == 0) { // no date selected case
            releaseDate = null;
        }
        else {
            releaseDate = LocalDate.of(year, month + 1, day);
        }

        // label logic
        String label = binding.buttonSelfRelease.isChecked() ? username :
                (binding.textInputEditLabel.getText().toString().length() == 0 ? username :
                        binding.textInputEditLabel.getText().toString()); // create label

        // progress to int and float
        int progress = binding.progressBarNewRelease.getProgress();
        float percentage = ((float) progress / binding.progressBarNewRelease.getMax()) * 100;

        // API call
        Call<ReleaseModel> releaseModelCall = apiInterface.addRelease(
                owner.trim(),
                binding.textInputEditTrackName.getText().toString().trim(),
                binding.textInputEditArtist.getText().toString().trim(),
                label.trim(),
                binding.textInputEditISRC.getText().toString().trim(),
                binding.textInputEditUPC.getText().toString().trim(),
                releaseDate,
                TextUtils.join(",", buttonStates),
                percentage
                );

        releaseModelCall.enqueue(new Callback<ReleaseModel>() {
            @Override
            public void onResponse(Call<ReleaseModel> call, Response<ReleaseModel> response) {
                if (response.body() != null) {
                    ReleaseModel releaseModel = response.body();

                    if (releaseModel.isSuccess()) {
                        // increment for empty label
                        if (binding.textInputEditLabel.getText().toString().length() == 0) {
                            binding.progressBarNewRelease.incrementProgressBy(1); // increment check
                        }

                        Toast.makeText(NewReleaseActivity.this, "Release added to catalogue.",
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(NewReleaseActivity.this, NewReleaseActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(NewReleaseActivity.this, releaseModel.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<ReleaseModel> call, Throwable t) {
                Toast.makeText(NewReleaseActivity.this, "Error, no response!",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public boolean noErrors() {
        return binding.textInputLayoutArtist.getError() == null
                && binding.textInputLayoutISRC.getError() == null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}