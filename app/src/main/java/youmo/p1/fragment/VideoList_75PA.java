package youmo.p1.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import youmo.p1.Adapter.RvAdapter;
import youmo.p1.Model.VideoModel;
import youmo.p1.R;
import youmo.p1.Tools.HttpHelper;
import youmo.p1.Tools.StringHelper;
import youmo.p1.VideoPlayActivity;

/**
 * Created by tanch on 2016/4/9.
 */
public class VideoList_75PA extends BaseFragment {
    boolean IsLoad;
    String NextUrl;
    String Host;
    RvAdapter ra;
    List<VideoModel> VideoData;
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
        if (ret.indexOf("淫色淫色")==-1)
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
        ret=StringHelper.MidString(ret,"<div class=\"box movie_list\">","</ul>");
        ArrayList<String> items=new ArrayList<String>();
        items=StringHelper.MidListString(ret,"<li>","</li>");
        for (int i=0;i<items.size();i++)
        {
            String url=Host+StringHelper.MidString(items.get(i),"<a href=\"","\"");
            String name=StringHelper.MidString(items.get(i),"<h3>","</h3>");
            String imageUrl=StringHelper.MidString(items.get(i),"<img src=\"","\"");
            String time="更新时间:"+StringHelper.MidString(items.get(i),"<span class=\"bg_top\">","</span>");
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
            String ret =  HttpHelper.Get(params[0]);
//            ret=StringHelper.MidString(ret,"资源采集","</ul>");
//            ret=StringHelper.MidString(ret,"<li>","</li>");
            ret=StringHelper.MidString(ret,"[a-zA-z]+://[^\\s]*.mp4");
            if (ret==null)
            {
                Toast.makeText(getActivity(),"无法解析地址!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                intent.putExtra("url",ret);
                startActivity(intent);
            }
            return null;
        }
    }
}
