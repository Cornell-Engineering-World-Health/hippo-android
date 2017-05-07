package edu.cornell.engineering.ewh.hippoandroid;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by erinchen on 5/2/17.
 * SessionAdapter customizes ArrayAdapter to set a custom style on the session list.
 */

public class SessionAdapter<CallSession> extends ArrayAdapter<CallSession> {

    Context context;

    public SessionAdapter(Context context, int layoutResourceId, CallSession[] sessions) {
        super(context, layoutResourceId, sessions);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        View v = super.getView(position, view, viewGroup);
        TextView mTitle = (TextView) v.findViewById(R.id.session_item);
        Typeface lato = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Regular.ttf");
        mTitle.setTypeface(lato);
        return v;
    }
}
