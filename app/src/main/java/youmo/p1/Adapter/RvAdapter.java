package youmo.p1.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import youmo.p1.Model.VideoModel;
import youmo.p1.R;

/**
 * Created by tanch on 2016/4/9.
 */
//适配器
public class RvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<VideoModel> Data;
    private  OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(View v, int i);
    }
    public void SetOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.listener =onItemClickListener;
    }

    public RvAdapter(List<VideoModel> data) {
        Data=data;
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_video_list_content, parent, false);
        return new DefViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DefViewHolder dh=(DefViewHolder)holder;
        dh.title.setText(Data.get(position).getName());
        dh.time.setText(Data.get(position).getTime());
        Picasso.with(dh.image.getContext()).load(Data.get(position).getImageUrl()).into(dh.image);
    }

    private  class DefViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView time;
        public ImageView image;

        public DefViewHolder(View v) {
            super(v);
            title=(TextView)v.findViewById(R.id.textView_adapter_video_list_title);
            time=(TextView)v.findViewById(R.id.textView_adapter_video_list_time);
            image=(ImageView)v.findViewById(R.id.imageView_adapter_video_list_item);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,getAdapterPosition());
                }
            });
        }
    }
}