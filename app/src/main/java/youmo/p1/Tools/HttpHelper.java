package youmo.p1.Tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanch on 2016/4/4.
 */
public class HttpHelper {

    public static String Get(String url)
    {
        String result="";
        try{
            URL address=new URL(url);
            HttpURLConnection http=(HttpURLConnection)address.openConnection();

            InputStreamReader isr=new InputStreamReader(http.getInputStream());
            BufferedReader br=new BufferedReader(isr);

            String resultLine;
            while((resultLine=br.readLine())!=null)
            {
                result+=resultLine;
            }
            isr.close();
            http.disconnect();
        }
        catch(Exception e)
        {
            return e.getMessage();
        }
        return result;

    }

    /**
     * 键值对转换成Byte[]
     * @param values
     * @return
     */
    public static byte[] MapToByte(Map<String,String> values)
    {
        StringBuffer sb = new StringBuffer();
        try {
            for(Map.Entry<String, String> entry : values.entrySet()) {
                sb.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(),"UTF-8"))
                        .append("&");
            }
            //删除最后的一个"&"
            sb.deleteCharAt(sb.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString().getBytes();
    }

    /**
     * 一个请求
     * @param url               访问URL
     * @param method            访问方式，默认或者null为GET访问，POST,GET,其他
     * @param values            请求参数键值对
     * @param IsSaveCookies     是否开启COOKIES存储，开启则可以保持登录状态
     * @return
     */
    public static String Connection(String url,String method,Map<String,String> values,boolean IsSaveCookies)
    {
        String result="";
        if (IsSaveCookies)
        {
            //初始化一个CookieManager，用于存放Cookies
            CookieManager cookieManager = new CookieManager();
            //设置接收规则，接收所有链接
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            //设置本地默认CookieManager，使接收到的COOKIES生效,清空->CookieHandler.setDefault(null)
            CookieHandler.setDefault(cookieManager);
        }

        try {
            URL address=new URL(url);
            HttpURLConnection http=(HttpURLConnection)address.openConnection();
            if (!method.isEmpty())
                http.setRequestMethod(method);
            if (values!=null&&values.size()>0)
            {
                //打开输入流
                OutputStream outPut=http.getOutputStream();
                //写入参数
                outPut.write(MapToByte(values));
                //关闭流
                outPut.close();
            }

            //开始请求，读取输出流,BufferedReader用于接收返回数据
            BufferedReader br=new BufferedReader(new InputStreamReader(http.getInputStream()));
            String resultLine;
            while((resultLine=br.readLine())!=null)
            {
                result+=resultLine;
            }
            return result;
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    /**
     * 取出所有COOKIES键值对
     * @param store COOKIES存储体
     * @return HashMap<String, String> COOKIES键值对
     */
    public static HashMap<String, String> CookiesToHashMap(CookieStore store) {
        boolean needUpdate = false;
        List<HttpCookie> cookies = store.getCookies();
        HashMap<String, String> cookieMap = null;
        if (cookieMap == null) {
            cookieMap = new HashMap<String, String>();
        }
        for (HttpCookie cookie : cookies) {
            String key = cookie.getName();
            String value = cookie.getValue();
            if (cookieMap.size() == 0 || !value.equals(cookieMap.get(key))) {
                needUpdate = true;
            }
            Log.i("Cookies",key+"|"+value);
            cookieMap.put(key, value);
        }
        return cookieMap;
    }

    public static void ClearCookies()
    {
        //清空所有
        CookieHandler.setDefault(null);
        //((CookieManager)CookieHandler.getDefault()).removeAll()
    }
    public static void ClearCookies(String uri)
    {
        //取得本地CookieManager
        CookieManager cookieManager =(CookieManager)CookieHandler.getDefault();
        //清楚指定URI  COOKIES
        cookieManager.getCookieStore().get(URI.create(uri)).clear();

    }
}
