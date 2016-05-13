package eit42.der_onlinestundenplan;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by L.Schnitzmeier on 13.05.2016.
 */
public class TimeTableFragment extends ListFragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    TimeTableElement[] elements;
    ListView timeTable;


    public TimeTableFragment() {
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);
        timeTable = (ListView) rootView.findViewById(android.R.id.list);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if(elements != null)
        {
            TimeTableAdapter adapter = new TimeTableAdapter(getContext(),elements);

            if(timeTable != null)
            {
                timeTable.setAdapter(adapter);
            }
        }
    }

    public void setElements(TimeTableElement[] newElements)
    {
        elements = newElements;
    }

}
