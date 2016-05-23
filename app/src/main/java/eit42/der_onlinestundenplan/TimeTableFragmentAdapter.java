package eit42.der_onlinestundenplan;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L.Schnitzmeier on 13.05.2016.
 */
public class TimeTableFragmentAdapter extends FragmentPagerAdapter {

    TimeTableFragment mondayFragment;
    TimeTableFragment tuesdayFragment;
    TimeTableFragment wednesdayFragment;
    TimeTableFragment thursdayFragment;
    TimeTableFragment fridayFragment;
    FragmentManager fragmentManager;


    public TimeTableFragmentAdapter(FragmentManager fm)
    {
        super(fm);
        fragmentManager = fm;
    }

    public void setFragments(JSONObject timeTable)
    {
        mondayFragment = new TimeTableFragment();
        tuesdayFragment = new TimeTableFragment();
        wednesdayFragment = new TimeTableFragment();
        thursdayFragment = new TimeTableFragment();
        fridayFragment = new TimeTableFragment();



        JSONArray days = new JSONArray();
        List<TimeTableElement[]> weekDays = new ArrayList<>(7);

        try {
            days = timeTable.getJSONObject("timeTable").getJSONArray("days");
            JSONArray times = timeTable.getJSONObject("timeTable").getJSONArray("times");
            for (int i = 0; i < days.length(); i++) {
                JSONArray dayData = days.getJSONObject(i).getJSONArray("data");

                TimeTableElement[] dayElement = new TimeTableElement[times.length()];
                for (int j = 0; j < dayData.length(); j++) {
                    //TODO Momentan werden keine geteilten Stunden angezeigt. Wenn z.B. 2 Lehrer einer Klasse zugeteilt sind
                    if(
                            !dayData.getJSONArray(j).getJSONArray(0).isNull(0) &&
                            !dayData.getJSONArray(j).getJSONArray(0).isNull(1) &&
                            !dayData.getJSONArray(j).getJSONArray(0).isNull(2)
                        )
                    {
                        String subject = dayData.getJSONArray(j).getJSONArray(0).getString(2);
                        String teacher = dayData.getJSONArray(j).getJSONArray(0).getString(0);
                        String room = dayData.getJSONArray(j).getJSONArray(0).getString(1);
                        dayElement[j] = new TimeTableElement(times.getString(j),subject,teacher,room);
                    } else {
                        dayElement[j] = new TimeTableElement(times.getString(j),"-","-","-");
                    }

                }
                weekDays.add(dayElement);
            }
        }catch (Exception e){
            Log.d("JSON", "Fragment json umwandlung fehlgeschlagen. " + e.getLocalizedMessage());
        }

//        TimeTableElement[] monday = new TimeTableElement[day]



        mondayFragment.setElements(weekDays.get(0));
        mondayFragment.setDayText("Montag");
        tuesdayFragment.setElements(weekDays.get(1));
        tuesdayFragment.setDayText("Dienstag");
        wednesdayFragment.setElements(weekDays.get(2));
        wednesdayFragment.setDayText("Mittwoch");
        thursdayFragment.setElements(weekDays.get(3));
        thursdayFragment.setDayText("Donnerstag");
        fridayFragment.setElements(weekDays.get(4));
        fridayFragment.setDayText("Freitag");

    }


    @Override
    public Fragment getItem(int position) {

        Fragment frag = null;
        switch(position)
        {
            case 0:
                frag = mondayFragment;
                break;
            case 1:
                frag = tuesdayFragment;
                break;
            case 2:
                frag = wednesdayFragment;
                break;
            case 3:
                frag = thursdayFragment;
                break;
            case 4:
                frag = fridayFragment;
                break;

        }
        return frag;
    }

    public void update()
    {

    }


    @Override
    public int getCount() {
        return 5;
    }
}
