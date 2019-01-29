package com.crycetruly.happyhour.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crycetruly.happyhour.model.HappyHour;
import com.crycetruly.happyhour.R;
import com.crycetruly.happyhour.utils.Tools;
import com.crycetruly.happyhour.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DialogFullscreenFragment extends DialogFragment {
public static String dateComparable;
    public static int datetoday;
    public CallbackResult callbackResult;
    private Double lat, lng;
    public void setOnCallbackResult(final CallbackResult callbackResult) {
        this.callbackResult = callbackResult;
    }

    private int request_code = 0;
    private View root_view;
    private Button spn_from_date, spn_from_time;
    private Button spn_to_date, spn_to_time;
    private TextView tv_email;
    private EditText et_name, et_location;
    private AppCompatCheckBox cb_allday;
    private AppCompatSpinner spn_timezone,category;
    private String cat="Uncategorized";
    private Button save;
    ProgressBar progressBar;
    AppCompatSpinner drop;
    private static final String TAG = "DialogFullscreenFragmen";

    private String showsIn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root_view = inflater.inflate(R.layout.dialog_add_offer, container, false);

        List<String> emails=new ArrayList<>();
        emails.add(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        FirebaseFirestore.getInstance().collection("businesses").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener((documentSnapshot, e) -> {
                    lat = Double.valueOf(documentSnapshot.get("lat").toString());
                    lng = Double.valueOf(documentSnapshot.get("lng").toString());
                    showsIn=documentSnapshot.get("showsIn").toString();
                    emails.add(documentSnapshot.get("semail").toString());

                });
save=root_view.findViewById(R.id.bt_save);
progressBar=root_view.findViewById(R.id.progress_bar);
progressBar.setVisibility(View.INVISIBLE);
drop=root_view.findViewById(R.id.drop);

        spn_from_date = (Button) root_view.findViewById(R.id.spn_from_date);
        spn_from_time = (Button) root_view.findViewById(R.id.spn_from_time);
        spn_to_date = (Button) root_view.findViewById(R.id.spn_to_date);
        spn_to_time = (Button) root_view.findViewById(R.id.spn_to_time);
        tv_email = (TextView) root_view.findViewById(R.id.tv_email);
        et_name = (EditText) root_view.findViewById(R.id.et_name);
        et_location = (EditText) root_view.findViewById(R.id.et_location);
        cb_allday = (AppCompatCheckBox) root_view.findViewById(R.id.cb_allday);
        spn_timezone = (AppCompatSpinner) root_view.findViewById(R.id.spn_timezone);
        category = (AppCompatSpinner) root_view.findViewById(R.id.categories);



drop.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,emails));

drop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tv_email.setText(String.valueOf(parent.getItemAtPosition(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});


        FirebaseDatabase.getInstance().getReference().child("businesses").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String check = dataSnapshot.child("canpost").getValue().toString();
                            Log.d(TAG, "onEvent: the value is" + check);
                            if (check.equals("true")) {
                                save.setEnabled(true);
                                save.setText("Save");

                                   } else if (check.equals("false")) {
                                save.setText("ACCOUNT NOT VERIFIED");

                                Snackbar.make(root_view, "You are not authorized to add happy hours", Snackbar.LENGTH_INDEFINITE).setAction("GET AUTHORITY", view -> {
                                    sendUsEmail();


                                }).show();
                                save.setEnabled(false);
                            }
                        } else {
                            save.setEnabled(true);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





        ((ImageButton) root_view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ((Button) root_view.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataResult();
                dismiss();
            }
        });

        spn_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDatePickerLight((Button) v);
            }
        });

        spn_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTimePickerLight((Button) v);
            }
        });

        spn_to_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDatePickerLight((Button) v);
            }
        });

        spn_to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTimePickerLight((Button) v);
            }
        });

        String[] timezones = getResources().getStringArray(R.array.timezone);
        ArrayAdapter<String> array = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, timezones);
        array.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spn_timezone.setAdapter(array);
        spn_timezone.setSelection(0);

        String[] cats = getResources().getStringArray(R.array.shop_category_title);
        ArrayAdapter<String> catsadapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, cats);
        array.setDropDownViewResource(R.layout.simple_spinner_item);
        category.setAdapter(catsadapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category.setSelection(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        category.setSelection(0);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HappyHour happyHour = new HappyHour();
                happyHour.email = tv_email.getText().toString();
                happyHour.title = et_name.getText().toString();
                happyHour.description = et_location.getText().toString();
                happyHour.startTime = spn_from_date.getText().toString() + " (" + spn_from_time.getText().toString() + ")";
                happyHour.endTime = spn_to_date.getText().toString() + " (" + spn_to_time.getText().toString() + ")";
                happyHour.allowcomments = cb_allday.isChecked();
                happyHour.timezone = spn_timezone.getSelectedItem().toString();
                happyHour.category=category.getSelectedItem().toString();

                if (datetoday < Utils.nowTime()) {
 Snackbar.make(root_view, "The date of the offer is before today", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(happyHour.email)){
                    Snackbar.make(root_view, "Please choose a contact email or add one in settings", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(happyHour.title)){
                    Snackbar.make(root_view, "Please name the offer", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(happyHour.description)){
                    Snackbar.make(root_view, "Please describe the offer", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(happyHour.startTime)){
                    Snackbar.make(root_view, "Please choose time offer starts", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(happyHour.endTime)){
                    Snackbar.make(root_view, "Please choose time the offer ends", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(happyHour.category)){
                    Snackbar.make(root_view, "Please choose a category ofthe offer", Snackbar.LENGTH_SHORT).show();
                    return;
                }




                CollectionReference firebaseFirestore = FirebaseFirestore.getInstance().collection("posts");
                String idd = firebaseFirestore.document().getId();


                HappyHour happyHour2 = new HappyHour();
                happyHour2.setStartTime(happyHour.startTime);
                happyHour2.setEndTime(happyHour.endTime);
                happyHour2.setDescription(happyHour.description);
                happyHour2.setIdd(idd);
                happyHour2.setEmail(happyHour.email);
                happyHour2.setShowsIn(showsIn);
                happyHour2.setCategory(happyHour.category);
                happyHour2.setAllowcomments(happyHour.allowcomments);
                happyHour2.setTimezone(happyHour.timezone);
                happyHour2.setTitle(happyHour.title);
                happyHour2.setOwnername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                happyHour2.setLat(lat);
                happyHour2.setSortDate(datetoday);
                happyHour2.setLng(lng);
                happyHour2.setPosted(System.currentTimeMillis());
                Snackbar.make(root_view, "Adding Offer,please wait", Snackbar.LENGTH_INDEFINITE).show();
                happyHour2.setOwneridd(FirebaseAuth.getInstance().getCurrentUser().getUid());
                save.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                firebaseFirestore.document(idd).set(happyHour2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("posts").child(idd).setValue(happyHour2, (listener, error) -> {
                                Snackbar.make(root_view, "Added successfully", Snackbar.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                dismiss();


                            });


                        }
                    }
                });





            }
        });

        return root_view;
    }

    private void showChoices() {
        List<CharSequence> list=new ArrayList<CharSequence>();
        list.add(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

         builder.setMessage("Showing work");
         builder.show();

    }

    private void sendDataResult() {
        HappyHour happyHour = new HappyHour();
        happyHour.email = tv_email.getText().toString();
        happyHour.title = et_name.getText().toString();
        happyHour.description = et_location.getText().toString();
        happyHour.startTime = spn_from_date.getText().toString() + " (" + spn_from_time.getText().toString() + ")";
        happyHour.endTime = spn_to_date.getText().toString() + " (" + spn_to_time.getText().toString() + ")";
        happyHour.allowcomments = cb_allday.isChecked();
        happyHour.timezone = spn_timezone.getSelectedItem().toString();
        happyHour.category=category.getSelectedItem().toString();
        if (callbackResult != null) {
            callbackResult.sendResult(request_code, happyHour);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void setRequestCode(int request_code) {
        this.request_code = request_code;
    }

    private void dialogDatePickerLight(final Button bt) {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date_ship_millis = calendar.getTimeInMillis();
                        bt.setText(Tools.getFormattedDateEvent(date_ship_millis));
                        String date = String.valueOf(String.format("%02d", dayOfMonth)) + "/" + String.valueOf(String.format("%02d", monthOfYear + 1)) + "/" + String.valueOf(year);
                        String testdate = String.valueOf(String.valueOf(year) + String.valueOf(String.format("%02d", monthOfYear + 1)) + String.format("%02d", dayOfMonth)) + "/";
                        dateComparable = testdate.replace("/", "");
                        datetoday = Integer.parseInt(dateComparable);






                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private void dialogTimePickerLight(final Button bt) {
        Calendar cur_calender = Calendar.getInstance();
        TimePickerDialog datePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.AM_PM, calendar.get(Calendar.AM_PM));
                long time_millis = calendar.getTimeInMillis();
                bt.setText(Tools.getFormattedTimeEvent(time_millis));
            }
        }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), true);
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getActivity().getFragmentManager(), "Timepickerdialog");
    }

    public interface CallbackResult {
        void sendResult(int requestCode, Object obj);
    }
    private void sendUsEmail() {

        Log.d(TAG, "sendUsEmail: ");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "happyhourwinnergroup@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Verification");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}