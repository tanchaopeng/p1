package youmo.p1.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import youmo.p1.Adapter.AdAdapter;
import youmo.p1.Adapter.RvAdapter;
import youmo.p1.Model.VideoModel;
import youmo.p1.R;
import youmo.p1.Tools.HttpHelper;
import youmo.p1.Tools.StringHelper;
import youmo.p1.VideoPlayActivity;

/**
 * Created by tanch on 2016/4/16.
 */
public class VideoList_BADA extends BaseFragment {
    RvAdapter ra;
    List<VideoModel> VideoData;
    String NextUrl;
    String Host;
    boolean IsLoad;
    AlertDialog ag;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.from(getActivity()).inflate(R.layout.fragment_video_list,container,false);
        RequestUrl=getArguments().getString("url");
        Host=getArguments().getString("host");
        VideoData=new ArrayList<VideoModel>();
        RecyclerView recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView_video_list);
        final LinearLayoutManager glm=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(glm);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = glm.findLastVisibleItemPosition();
                int totalItemCount = glm.getItemCount();
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                    //加载
                    if (!IsLoad)
                        LoadVideoUrl(NextUrl);
                }
            }
        });
        ra=new RvAdapter(VideoData);
        ra.SetOnItemClickListener(new RvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int i) {
                Toast.makeText(getActivity(),"准备播放:"+VideoData.get(i).getName(),Toast.LENGTH_SHORT).show();
                new GetVideoUrl().execute(VideoData.get(i).getURL());
            }
        });
        recyclerView.setAdapter(ra);
        LoadVideoUrl(RequestUrl);
        ag=  new AlertDialog.Builder(getActivity()).create();


        return v;
    }
    void  LoadVideoUrl(final String l)
    {
        if (!IsLoad&&l!=null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LoadVideoList(l);
                }
            }).start();
    }
    void LoadVideoList(String l)
    {
        IsLoad=true;
        //String ret = HttpHelper.Get(Host+l);
        String ret = HttpHelper.Get(l);
        if (ret.indexOf("八达电影")==-1)
        {
            new Handler(getActivity().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),"无法访问，快去挂代理。",Toast.LENGTH_SHORT).show();
                }
            });
            IsLoad=false;
            return;
        }
        if (ret.indexOf("尾页")!=-1||ret.indexOf("下一页")!=-1)
        {
            String pages= StringHelper.MidString(ret,"<div id=\"pages\">","尾页");
            if (pages.indexOf("下一页</a>")==-1)
            {
                NextUrl=null;
            }
            else
            {
                int s=pages.lastIndexOf("<a href=\"",pages.indexOf("下一页</a>"));
                NextUrl=Host+pages.substring(s+"<a href=\"".length(),pages.indexOf("\" class=\"pagegbk\"",s));
            }
        }
        else
            NextUrl=null;
        //列表头尾
        ret=StringHelper.MidString(ret,"<ul class=\"primary-list","</ul>");
        ArrayList<String> items=new ArrayList<String>();
        items=StringHelper.MidListString(ret,"<li>","</li>");
        for (int i=0;i<items.size();i++)
        {
            String url=Host+StringHelper.MidString(items.get(i),"<a href=\"","\"");
            String name=StringHelper.MidString(items.get(i),"alt=\"","\"");
            String imageUrl=Host+StringHelper.MidString(items.get(i),"<img src=\"","\"");
            String time=StringHelper.MidString(items.get(i),"时间:","</dl>");
            time="更新时间:"+StringHelper.MidString(time,"<dd>","</dd>");
            VideoModel vm=new VideoModel();
            vm.setURL(url);
            vm.setName(name);
            vm.setImageUrl(imageUrl);
            vm.setTime(time);
            VideoData.add(vm);
        }
        new Handler(getActivity().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //这里可以直接访问UI
                IsLoad=false;
                ra.notifyDataSetChanged();
            }
        });
    }



class GetVideoUrl extends AsyncTask<String,Integer,String>
    {
        @Override
        protected String doInBackground(String... params) {
            String ret =  HttpHelper.Get(params[0]+"0-1.js");
            ret=ret.replace("\\/","/");
            //final ArrayList<String> list=StringHelper.MidListString(ret,"http://","\"");
            ret =StringHelper.MidString(ret,"\"playname\":\"mp4\",","}");
            ArrayList<String> list=StringHelper.MidListString(ret,"[","]");
            final ArrayList<VideoModel> playData=new ArrayList<VideoModel>();
            for (int i=0;i<list.size();i++)
            {
                VideoModel obj =  new VideoModel();
                obj.setName(StringHelper.MidString(list.get(i),"\"","\""));
                obj.setURL("http://"+StringHelper.MidString(list.get(i),"http://","\""));
                playData.add(obj);
            }

            //弹出列表。点击列表播放
            new Handler(getActivity().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //这里可以直接访问UI
                    ag.show();
                    ag.getWindow().setContentView(R.layout.dialog_video_list);
                    final ListView lv=(ListView)ag.getWindow().findViewById(R.id.listView_dialog_list);
                    final TextView tv=(TextView)ag.getWindow().findViewById(R.id.textView_dialog_title);
                    tv.setText("请选择");
                    AdAdapter ad=new AdAdapter(getActivity(),R.layout.adapter_dialog_play_list,playData);
                    lv.setAdapter(new ArrayAdapter<VideoModel>(getActivity(),android.R.layout.simple_list_item_1,playData){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view=inflater.inflate(android.R.layout.simple_list_item_1,null);
                            TextView textView =(TextView)view.findViewById(android.R.id.text1);
                            textView.setText(StringHelper.DeUnicode(playData.get(position).getName()));
                            return view;
                        }
                    });
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (playData.get(position)==null)
                            {
                                Toast.makeText(getActivity(),"无法解析地址!",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                                intent.putExtra("url",playData.get(position).getURL());
                                startActivity(intent);
                            }
                        }
                    });
                }
            });

            //ret=StringHelper.MidString(ret,"http://[^\\s]*.mp4");
            //ret=StringHelper.MidString(ret,"[a-zA-z]+://[^\\s]*.mp4");

            return null;
        }
    }
}
