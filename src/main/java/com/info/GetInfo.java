package com.info;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Class GetInfo
 * @Description TODO
 * @Author LeftEar
 * @Date 20/4/19 23:26
 * @Version 1.0
 */
public class GetInfo {
    private static final String url = "http://www.jxeea.cn/";
    private static final String sendUrl = "https://sc.ftqq.com/SCU94602T5bbabf22833f2bbb218e60dd1741f5d75e9c7c0321542.send";

    public static void main(String[] args) {
//        System.out.println(httpRequest(url));

        String name = "section2-right";
        parseElement(url, name);


    }

    /**
     * 解析
     *
     * @Date 20/4/20 7:06
     * @param url
     * @return void
     */
    private static void parseElement(String url, String name) {
        Timer timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // 获取江西教育考试院公告公示下的信息
                String elementInfo = getEduElement(url, name);
                String[] infos = elementInfo.split(" ");
                for (String info : infos) {
                    System.out.println(info);
                    if (info.contains("四级") || info.contains("四六级") || info.contains("招生简章")) {
                        // https://sc.ftqq.com/SCU94602T5bbabf22833f2bbb218e60dd1741f5d75e9c7c0321542.send?text=
                        // 发送
//                        sendGet(sendUrl, info);
                    }
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, (1000 * 60 * 10));
    }

    /**
     * 向指定链接和参数发送消息
     *
     * @Date 20/4/20 7:07
     * @param url
     * @param param
     * @return java.lang.String
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?text=" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("contentType", "utf-8");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            /*
             * Map<String, List<String>> map = connection.getHeaderFields(); //
             * 遍历所有的响应头字段 for (String key : map.keySet()) {
             * System.out.println(key + "--->" + map.get(key)); }
             */
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 获取江西教育考试院页面（需更改）
     * 通过Jsoup获取指定url、class及子标签下的所有文本
     *
     * @Date 20/4/20 7:01
     * @param url
     * @return java.lang.String
     */
    private static String getEduElement(String url, String name) {
        Document doc = null;
        String info = "";
        try {
            //通过延迟2000毫秒然后再去请求可解决js异步加载获取不到数据的问题
            doc = Jsoup.connect(url).timeout(2000).get();
            // 通过class的获取,一个数组对象Elements里面有我们想要的数据
            Elements elements = doc.getElementsByClass(name);
            // 遍历获取所有文本
            for (Element element : elements) {
                // 打印出每一个节点的信息;你可以选择性的保留你想要的数据,一般都是获取个固定的索引;
                if (element != null) {
                    info = element.text();
//                    System.out.println(element.text());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 获取指定参数Elements中的代码
     *
     * @Date 20/4/20 7:01
     * @param requestUrl
     * @return java.lang.String
     */
    private static String httpRequest(String requestUrl) {
        StringBuffer buffer = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConn = null;
        try {
            // 建立get请求
            URL url = new URL(requestUrl);
            httpUrlConn = (HttpURLConnection) url.openConnection();
//            httpUrlConn.setDoInput(true);
//            httpUrlConn.setRequestMethod("GET");

            // 获取输入流
            inputStream = httpUrlConn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            // 从输入流读取结果
            buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }
        return buffer.toString();
    }

}
