package eit42.der_onlinestundenplan;

import android.app.Activity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by L.Schnitzmeier on 12.05.2016.
 */
public class TimeTableAdapter extends BaseAdapter {

    private Activity context;
    private String[] classInfo;
    private static int counter;

    public TimeTableAdapter(Activity pContext, String[] pClassInfo)
    {
        context = pContext;
        classInfo = pClassInfo;
    }

    @Override
    public int getCount() {
        return classInfo.length;
    }

    @Override
    public Object getItem(int position) {
        return classInfo[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if(row == null)
        {
            row = LayoutInflater.from(context).inflate(R.layout.list_item_time_table,null,false);
        }

        TextView hour = (TextView) row.findViewById(R.id.hourTextView);
        TextView subject = (TextView) row.findViewById(R.id.classTextView);
        TextView rt = (TextView) row.findViewById(R.id.rtTextView);

        hour.setText(counter+". Std.");
        subject.setText(classInfo[position]);
        rt.setText("__"+counter+"__"+counter*2);

        counter++;

        return row;
    }
}
