package org.lightsys.missionaryapp.views;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.lightsys.missionaryapp.R;

import org.lightsys.missionaryapp.data.UpdateNotification;
import org.lightsys.missionaryapp.tools.LocalDBHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * @author Judah Sistrunk
 * created on 5/25/2016

 * Class that governs the options menu for how often the app auto-refreshes
 * */

public class Options extends Fragment {

    private final String EXTRA_DELETE = "delete";

    private Spinner refreshPeriods, giftPeriods;
    private ToggleButton    reminderOnOff;
    private TextView        reminderDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.options_layout, container, false);
        getActivity().setTitle("Options");

        Button applyButton = (Button) v.findViewById(R.id.applyButton);
        refreshPeriods = (Spinner) v.findViewById(R.id.refreshPeriodsSpinner);
        giftPeriods = (Spinner) v.findViewById(R.id.giftPeriodsSpinner);
        reminderOnOff = (ToggleButton) v.findViewById(R.id.reminderOnOffSwitch);
        reminderDetails = (TextView) v.findViewById(R.id.reminderDetailsText);


        reminderOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(reminderOnOff.isChecked()) {
                    Intent reminder = new Intent(getActivity(), NotificationActivity.class);
                    startActivity(reminder);

                }else{
                    reminderDetails.setText("");
                    Intent reminder = new Intent(getActivity(), NotificationActivity.class);
                    reminder.putExtra(EXTRA_DELETE, true);
                    startActivity(reminder);
                }
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDBHandler db = new LocalDBHandler(v.getContext());
                db.addRefreshPeriod(refreshPeriods.getSelectedItem().toString());
                db.addGiftPeriod(giftPeriods.getSelectedItem().toString());
                Toast.makeText(v.getContext(), "Changes Applied", Toast.LENGTH_SHORT).show();
            }
        });


        LocalDBHandler db = new LocalDBHandler(v.getContext());
        String refresh = db.getRefreshPeriod();
        String period = db.getGiftPeriod();
        db.close();
        String[] updatePeriods = getResources().getStringArray(R.array.refresh_times);
        String[] allGiftPeriods = getResources().getStringArray(R.array.gift_periods);

        for (int i = 0; i < updatePeriods.length; i++) {
            if (updatePeriods[i].equals(refresh)) {
                refreshPeriods.setSelection(i);
            }
        }
        for (int i = 0; i < allGiftPeriods.length; i++) {
            if (allGiftPeriods[i].equals(period)) {
                giftPeriods.setSelection(i);
            }
        }

        setAlarmInfo(db);

        return v;
    }


    private void setAlarmInfo(LocalDBHandler db){
        ArrayList<UpdateNotification> notifications = db.getNotifications();
        if(notifications.size()>0){
            reminderOnOff.setChecked(true);
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
            reminderDetails.setText(notifications.get(0).getFrequency() + "\nNext: " + format.format(notifications.get(0).getTime()));
        }else{
            reminderOnOff.setChecked(false);
            reminderDetails.setText("");
        }
    }
    public void onResume(){
        super.onResume();
        LocalDBHandler db = new LocalDBHandler(getActivity());
        setAlarmInfo(db);
    }

}
