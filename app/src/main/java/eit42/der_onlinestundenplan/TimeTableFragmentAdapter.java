package eit42.der_onlinestundenplan;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by L.Schnitzmeier on 13.05.2016.
 */
public class TimeTableFragmentAdapter extends FragmentPagerAdapter {

    TimeTableFragment mondayFragment;
    TimeTableFragment tuesdayFragment;
    TimeTableFragment wednesdayFragment;
    TimeTableFragment thursdayFragment;
    TimeTableFragment fridayFragment;


    public TimeTableFragmentAdapter(FragmentManager fm)
    {
        super(fm);
    }

    public void setFragments(Context c)
    {
        mondayFragment = new TimeTableFragment();
        tuesdayFragment = new TimeTableFragment();
        wednesdayFragment = new TimeTableFragment();
        thursdayFragment = new TimeTableFragment();
        fridayFragment = new TimeTableFragment();

        //TODO: Hardcoded Daten durch API Daten ersetzen
        TimeTableElement monday1 = new TimeTableElement("1","ITS","STO","D128");
        TimeTableElement monday2 = new TimeTableElement("2","ITS","STO","D128");
        TimeTableElement monday3 = new TimeTableElement("3","AE","RIP","D128");
        TimeTableElement monday4 = new TimeTableElement("4","AE","RIP","D128");
        TimeTableElement monday5 = new TimeTableElement("5","PG","BER","D128");
        TimeTableElement monday6 = new TimeTableElement("6","PG","BER","D128");
        TimeTableElement monday7 = new TimeTableElement("6","PG","BER","D128");
        TimeTableElement monday8 = new TimeTableElement("6","PG","BER","D128");
        TimeTableElement monday9 = new TimeTableElement("6","PG","BER","D128");

        TimeTableElement[] monday = new TimeTableElement[] {monday1,monday2,monday3,monday4,monday5,monday6,monday7,monday8,monday9};

        TimeTableElement tuesday1 = new TimeTableElement("1","WuGP","BUK","D128");
        TimeTableElement tuesday2 = new TimeTableElement("2","WuGP","BUK","D128");
        TimeTableElement tuesday3 = new TimeTableElement("3","DIFF","KEH","D128");
        TimeTableElement tuesday4 = new TimeTableElement("4","DIFF","KEH","D128");
        TimeTableElement tuesday5 = new TimeTableElement("5","PG","BER","D128");
        TimeTableElement tuesday6 = new TimeTableElement("6","PG","BER","D128");

        TimeTableElement[] tuesday = new TimeTableElement[] {tuesday1,tuesday2,tuesday3,tuesday4,tuesday5,tuesday6};

        mondayFragment.setElements(monday);
        mondayFragment.setDayText("Montag");
        tuesdayFragment.setElements(tuesday);
        tuesdayFragment.setDayText("Dienstag");

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
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
