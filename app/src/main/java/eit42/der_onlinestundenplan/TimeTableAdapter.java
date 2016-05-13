package eit42.der_onlinestundenplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by L.Schnitzmeier on 12.05.2016.
 */
public class TimeTableAdapter extends ArrayAdapter<TimeTableElement> {

    public TimeTableAdapter(Context context, TimeTableElement[] elements) {
        super(context,R.layout.list_item_time_table ,elements);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if(itemView == null)
        {
            itemView = inflater.inflate(R.layout.list_item_time_table,parent,false);
        }

        TimeTableElement element = getItem(position);

        TextView hourText = (TextView) itemView.findViewById(R.id.hourTextView);
        TextView subjectText = (TextView) itemView.findViewById(R.id.classTextView);
        TextView rtText = (TextView) itemView.findViewById(R.id.rtTextView);

        hourText.setText(element.getHour());
        subjectText.setText(element.getSubject());
        rtText.setText(element.getRoom() + " | " + element.getTeacher());

        return itemView;
    }
}
