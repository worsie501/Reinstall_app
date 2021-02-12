package com.example.reinstall_app.activity_classes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reinstall_app.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class bottom_notification extends BottomSheetDialogFragment {

    RadioButton rbOne, rbTwo, rbThree, rbFour;
    RadioGroup rgReportButtons;
    LinearLayout reportCategories, reportThanks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_notification_layout, container, false);

        rbOne = v.findViewById(R.id.rbOne);
        rbTwo = v.findViewById(R.id.rbTwo);
        rbThree = v.findViewById(R.id.rbThree);
        rbFour = v.findViewById(R.id.rbFour);
        rgReportButtons = v.findViewById(R.id.rgReportButtons);

        reportCategories = v.findViewById(R.id.report_categories);
        reportThanks = v.findViewById(R.id.report_thanks);


          rgReportButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(RadioGroup group, int checkedId) {

                  reportCategories.setVisibility(View.GONE);
                  reportThanks.setVisibility(View.VISIBLE);

              }
          });

        return v;

    }
}
