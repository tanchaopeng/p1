package youmo.p1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import youmo.p1.Tools.SPHelper;
import youmo.p1.fragment.VideoList_1717LU;
import youmo.p1.fragment.VideoList_75PA;
import youmo.p1.fragment.VideoList_BADA;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int CurMode;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sp=new SPHelper(this,"p1config");
        sp.Read("Host",Host);
        if (Host==null||Host.length()<1)
            Host="http://1717lu.com";
        Host_1717LU="http://1717lu.com";
        Host_75PA="http://www.x8s5.com";
        Host_BADA="http://www.badady.com";


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (CurMode == R.id.nav_bada) {
//            getMenuInflater().inflate(R.menu.main, menu);
        } else if (CurMode == R.id.nav_75pa) {
            getMenuInflater().inflate(R.menu.video_75pa, menu);
        } else if (CurMode == R.id.nav_1717lu) {
            getMenuInflater().inflate(R.menu.video_1717lu, menu);
        } else if (CurMode == R.id.nav_settings) {
            getMenuInflater().inflate(R.menu.main, menu);
        } else if (CurMode == R.id.nav_send) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.dongman_1717lu) {
            SendFragment1717Lu("/list/13.html");
            toolbar.setTitle("动漫");
        }else if (id==R.id.oumei_1717lu){
            SendFragment1717Lu("/list/11.html");
            toolbar.setTitle("欧美");
        }else if (id==R.id.zipai_1717lu){
            SendFragment1717Lu("/list/12.html");
            toolbar.setTitle("自拍");
        }else if (id==R.id.yazhou_1717lu){
            SendFragment1717Lu("/list/9.html");
            toolbar.setTitle("亚洲");
        }else if (id==R.id.zhifu_1717lu){
            SendFragment1717Lu("/list/10.html");
            toolbar.setTitle("制服");
        }

        if (id == R.id.rihan_75pa) {
            SendFragment75Pa("/AAyidong/AAlb/yazhouzaixian/");
            toolbar.setTitle("日韩");
        }else if (id==R.id.zipai_75pa){
            SendFragment75Pa("/AAyidong/AAlb/shoujitoupai/");
            toolbar.setTitle("自拍");
        }else if (id==R.id.sanji_75pa){
            SendFragment75Pa("/AAyidong/AAlb/shoujilunli/");
            toolbar.setTitle("三级");
        }else if (id==R.id.dongman_75pa){
            SendFragment75Pa("/AAyidong/AAlb/cartoons/");
            toolbar.setTitle("动漫");
        }else if (id==R.id.zhifu_75pa){
            SendFragment75Pa("/AAyidong/AAlb/cosplay/");
            toolbar.setTitle("制服");
        } else if (id==R.id.juru_75pa){
            SendFragment75Pa("/AAyidong/AAlb/juruboba/");
            toolbar.setTitle("巨乳");
        }else if (id==R.id.yizu_75pa){
            SendFragment75Pa("/AAyidong/AAlb/yizu/");
            toolbar.setTitle("异族");
        }else if (id==R.id.linglei_75pa){
            SendFragment75Pa("/AAyidong/AAlb/pihao/");
            toolbar.setTitle("另类");
        }else if (id==R.id.luanlun_75pa){
            SendFragment75Pa("/AAyidong/AAlb/tongjian/");
            toolbar.setTitle("乱伦");
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_75pa) {
            CurMode=R.id.nav_75pa;
            SendFragment75Pa("/AAyidong/AAlb/yazhouzaixian/");
            toolbar.setTitle("日韩");
        } else if (id == R.id.nav_1717lu) {
            CurMode=R.id.nav_1717lu;
            SendFragment1717Lu("/list/13.html");
            toolbar.setTitle("动漫");
        }else if (id == R.id.nav_bada) {
            CurMode=R.id.nav_bada;
            SendFragmentBADA("/vod-type-id-3-wd--letter--year-0-area--order-hits-p-1.html");
            toolbar.setTitle("八达动漫");
        }
        this.invalidateOptionsMenu();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void SendFragment1717Lu(String url)
    {
        if (findViewById(R.id.frame_main_content)!=null)
        {
            VideoList_1717LU videoList = new VideoList_1717LU();
            Bundle b = new Bundle();
            b.putString("url",Host_1717LU+url);
            b.putString("host",Host_1717LU);
            videoList.setArguments(b);
            getFragmentManager().beginTransaction().addToBackStack(null);
            getFragmentManager().beginTransaction().add(R.id.frame_main_content,videoList).commit();
        }
    }

    public void SendFragment75Pa(String url)
    {
        if (findViewById(R.id.frame_main_content)!=null)
        {
            VideoList_75PA videoList = new VideoList_75PA();
            Bundle b = new Bundle();
            b.putString("url",Host_75PA+url);
            b.putString("host",Host_75PA);
            videoList.setArguments(b);
            getFragmentManager().beginTransaction().addToBackStack(null);
            getFragmentManager().beginTransaction().add(R.id.frame_main_content,videoList).commit();
        }
    }
    public void SendFragmentBADA(String url)
    {
        if (findViewById(R.id.frame_main_content)!=null)
        {
            VideoList_BADA videoList = new VideoList_BADA();
            Bundle b = new Bundle();
            b.putString("url",Host_BADA+url);
            b.putString("host",Host_BADA);
            videoList.setArguments(b);
            getFragmentManager().beginTransaction().addToBackStack(null);
            getFragmentManager().beginTransaction().add(R.id.frame_main_content,videoList).commit();
        }
    }
}
