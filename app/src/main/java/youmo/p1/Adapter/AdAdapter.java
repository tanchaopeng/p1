package youmo.p1.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import youmo.p1.Model.VideoModel;

/**
 * Created by tanch on 2016/4/16.
 */
public class AdAdapter extends ArrayAdapter<VideoModel> {
    private int mResourceId;
    public AdAdapter(Context context, int resource, ArrayList<VideoModel> objects) {
        super(context, resource, objects);
        this.mResourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(mResourceId,null);

        return super.getView(position, convertView, parent);
    }
}
