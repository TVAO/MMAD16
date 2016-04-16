package tvao.mmad.itu.tingle.Controller.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tvao.mmad.itu.tingle.R;

/**
 * This fragments represents a dialog providing the user a DatePicker widget to select a date when registering a thing.
 * The DatePicker is used in ThingDetailFragment when detailing information about a thing.
 */
public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    public static final String EXTRA_DATE = "itu.mmad.dttn.tingle.date";

    private DatePicker mDatePicker;

    /**
     * This method is used to instantiate a new Fragment used to display a date picking dialogue screen.
     * Encapsulates and abstracts the steps required to setup the object from the client.
     *
     * Rather than having the client call the default constructor and manually set the fragment's arguments themselves,
     * we provide a static factory method that does this for them making fragment instantiation convenient and enforcing well-defined behavior.
     *
     * @param date - date related to thing shown in activity.
     * @return - new fragment with thing details including date.
     */
    public static DatePickerFragment newInstance(Date date)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Call to do initial creation of fragment
     * @param savedInstanceState - fragment rebuilt from saved state if not null
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);


        // Use current time as default values for picker
        Calendar calender = Calendar.getInstance();
        if (date != null)
        {
            calender.setTime(date);
        }
        int year = calender.get(Calendar.YEAR);
        int month = calender.get(Calendar.MONTH);
        int day = calender.get(Calendar.DAY_OF_MONTH);


        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date,null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        // Setup an Alert Dialogue to avoid leaving the detail window while picking a date
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year,month,day).getTime();
                        sendResult(Activity.RESULT_OK,date);
                    }
                })
                .create();
    }

    // Send result of date picker to target fragment ThingDetailFragment
    private void sendResult(int resultCode, Date date)
    {
        if(getTargetFragment() == null)
        {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
