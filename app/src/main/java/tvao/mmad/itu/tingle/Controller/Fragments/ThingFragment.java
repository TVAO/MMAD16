package tvao.mmad.itu.tingle.Controller.Fragments;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.UUID;

import tvao.mmad.itu.tingle.Controller.Helpers.BaseFragment;
import tvao.mmad.itu.tingle.Model.Thing;
import tvao.mmad.itu.tingle.Model.ThingRepository;
import tvao.mmad.itu.tingle.R;

/**
 * This class represents the fragment of a detailed page for a given item.
 * The TingleFragment is hosted by the activity TinglePagerActivity.
 */
public class ThingFragment extends BaseFragment {

    public static final String EXTRA_THING_ID = "thingintent.THING_ID";
    private static final String WHAT = "what";
    private static final String WHERE = "where";
    private static final String DESCRIPTION = "description";

    private Thing mThing;
    private EditText mTitleField;
    private TextView mWhatField, mWhereField;

    public static ThingFragment newInstance(UUID thingId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_THING_ID, thingId);

        ThingFragment fragment = new ThingFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        UUID thingId = (UUID) getArguments().getSerializable(EXTRA_THING_ID);
        mThing = ThingRepository.get(getActivity()).getThing(thingId);
        setHasOptionsMenu(true);
    }

    @Override
    @TargetApi(11)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_thing, parent, false);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mThing.getWhat());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mThing.setWhat(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        return v;

    }


    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity()); // Navigate to parent activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
