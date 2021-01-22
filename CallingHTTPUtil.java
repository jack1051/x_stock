package project.common.util;


import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XN_GOUCHAO on 2017/7/10.
 */
public class CallingHTTPUtil {
    public  static final Logger LOGGER= Logger.getLogger(CallingHTTPUtil.class);
    /**
     * 接口调用 GET
     */
    public  static JSONObject httpURLConectionGET(Map map) throws IOException {

            String urlString= StringUtil.safeToString(map.get("url"), "");
            URL url = new URL(urlString);    // 把字符串转换为URL请求地址
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
            connection.connect();// 连接会话
            connection.disconnect();// 断开连接
         JSONObject object=new JSONObject();
        String result = ConvertStream2Json(connection.getInputStream());
        if(result !=null && !result.equals("")){
            object = net.sf.json.JSONObject.fromObject(result);
        }
        return object;
    }

    /**
     * 接口调用 GET
     */
    public  static String httpURLConectionGET_STR(Map map) throws IOException {

        String urlString= StringUtil.safeToString(map.get("url"), "");
        URL url = new URL(urlString);    // 把字符串转换为URL请求地址
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
        connection.connect();// 连接会话
        connection.disconnect();// 断开连接
        String result = ConvertStream2Json(connection.getInputStream(),"GBK");

        return result;
    }

    /**
     * 接口调用  POST
     */
    public static JSONObject httpURLConnectionPOST (Map map) throws IOException {

         JSONObject jsonObject=new JSONObject();
            String param= StringUtil.safeToString(map.get("param"), "");
            String urlString= StringUtil.safeToString(map.get("url"), "");
            URL url = new URL(urlString);
            // 将url 以 open方法返回的urlConnection  连接强转为HttpURLConnection连接  (标识一个url所引用的远程对象连接)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 此时cnnection只是为一个连接对象,待连接中
            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            // 设置连接输入流为true
            connection.setDoInput(true);
            // 设置请求方式为post
            connection.setRequestMethod("POST");
            // post请求缓存设为false
            connection.setUseCaches(false);
            // 设置该HttpURLConnection实例是否自动执行重定向
            connection.setInstanceFollowRedirects(true);
            // 设置请求头里面的各个属性 (以下为设置内容的类型,设置为经过urlEncoded编码过的from参数)
            // application/x-javascript text/xml->xml数据 application/json->json对象 application/x-www-form-urlencoded->表单数据
            // ;charset=utf-8 必须要，不然妙兜那边会出现乱码【★★★★★】
           connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        // 建立连接 (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();
            // 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
            DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());
            // 格式 parm = aaa=111&bbb=222&ccc=333&ddd=444
            // 将参数输出到连接
            dataout.writeBytes(param);

            // 输出完成后刷新并关闭流
            dataout.flush();
            dataout.close(); // 重要且易忽略步骤 (关闭流,切记!)

//            System.out.println(connection.getResponseCode());

            // 连接发起请求,处理服务器响应  (从连接获取到输入流并包装为bufferedReader)


            connection.disconnect(); // 销毁连接
            JSONObject object=new JSONObject();
           String result = ConvertStream2Json(connection.getInputStream());
            if(result !=null && !result.equals("")){
                object = net.sf.json.JSONObject.fromObject(result);
            }

            return object;
    }

    /**
     * 接口调用  POST application/json
     */
    public static JSONObject httpURLConnectionPOST_1 (Map map) throws IOException {
        String param= StringUtil.safeToString(map.get("param"), "");
        String urlString= StringUtil.safeToString(map.get("url"), "");
        System.out.println("param="+param+"\\n"+"  url="+urlString);
        LOGGER.debug("param="+param+"\\n"+"  url="+urlString);
        // 创建连接
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/json");//以json的方式传递参数
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(1200000);
        connection.setRequestProperty("Charsert", "UTF-8");
        connection.connect();

        // POST请求
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.write(param.getBytes("UTF-8"));//参数需要json格式(其实就是一个字符串)
        out.flush();
        out.close();

        // 断开连接
        connection.disconnect();
        JSONObject jsonObject=new JSONObject();

        //判断HTTP状态
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

            // 读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = ConvertStream2Json(connection.getInputStream());
            reader.close();
            if (result !=null && !result.equals("")) jsonObject= JSONObject.fromObject(result);
        }
        jsonObject.put("ResponseCode",connection.getResponseCode());
        System.out.println(jsonObject.toString());
        return jsonObject;
    }
    private static String ConvertStream2Json(InputStream inputStream) {
       return ConvertStream2Json(  inputStream, null);
    }
    private static String ConvertStream2Json(InputStream inputStream,String encoding) {
        if (encoding == null || encoding.equals("")) encoding = "utf-8";
        String jsonStr = "";
        // ByteArrayOutputStream相当于内存输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        // 将输入流转移到内存输出流中
        try {
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            // 将内存流转换为字符串
            jsonStr = new String(out.toByteArray(),encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }


     public static JSONObject httpRequest(String actionUrl , Map map, Map<String, String> requestProperty)throws IOException{

         String param= StringUtil.safeToString(map.get("param"), "");
         String method= StringUtil.safeToString(map.get("method"), "PUT");

         DataOutputStream ds = null;
         InputStream inputStream = null;
         InputStreamReader inputStreamReader = null;
         BufferedReader reader = null;
         String result ="";
         String tempLine = null;
         JSONObject jsonObject=new JSONObject();
         try {
             // 统一资源
             URL url = new URL(actionUrl);
             // 连接类的父类，抽象类
             URLConnection urlConnection = url.openConnection();
             // http的连接类
             HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

             // 设置是否从httpUrlConnection读入，默认情况下是true;
             httpURLConnection.setDoInput(true);
             // 设置是否向httpUrlConnection输出
             httpURLConnection.setDoOutput(true);
             // Post 请求不能使用缓存
             httpURLConnection.setUseCaches(false);
             // 设定请求的方法，默认是GET
             httpURLConnection.setRequestMethod(method);
             // 设置字符编码连接参数
             httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
             // 设置字符编码
             httpURLConnection.setRequestProperty("Charset", "UTF-8");
             // 设置请求内容类型
             httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
             httpURLConnection.setRequestProperty("x-amz-date", "Tue, 23 Apr 2019 03:11:15 GMT" );
             httpURLConnection.setRequestProperty("Authorization", "AWS AS01:seJ0KDs+TV88aek2DRf+muOJbVg=" );
             httpURLConnection.setRequestProperty("x-as-userid", "39974158-0b4f-11e9-ac5b-005056985f72" );

             for ( String rpkey  : requestProperty.keySet()    ) {
                 httpURLConnection.setRequestProperty(rpkey, requestProperty.get(rpkey));
             }

             // 设置DataOutputStream
             ds = new DataOutputStream(httpURLConnection.getOutputStream());
             Object fileInputObject =    map.get("fileInputStream");
             if (fileInputObject !=null){
//                 String uploadFile = "D:\\aaaa\\a.txt";
//                 FileInputStream fStream = new FileInputStream(uploadFile);
                 FileInputStream fStream = (FileInputStream) fileInputObject;
                 int bufferSize = 1024;
                 byte[] buffer = new byte[bufferSize];
                 int length = -1;
                 while ((length = fStream.read(buffer)) != -1) {
                     ds.write(buffer, 0, length);
                 }
                 /* close streams */
                 fStream.close();

             }

             /* close streams */
             ds.flush();

             //判断HTTP状态
             if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                 inputStream = httpURLConnection.getInputStream();
                   result = ConvertStream2Json(inputStream);
                 if(result !=null && !result.equals("")){
                     jsonObject=JSONObject.fromObject(result);
                 }
             }

             jsonObject.put("ResponseCode",httpURLConnection.getResponseCode());

         }  finally {
             if (ds != null) {
                     ds.close();
             }
             if (reader != null) {
                 reader.close();
             }
             if (inputStreamReader != null) {
                 inputStreamReader.close();
             }
             if (inputStream != null) {
                 inputStream.close();
             }

         }
         System.out.println(jsonObject.toString());
         return jsonObject;
             }


    /**
     * 接口调用  POST 获取附件
     */
    public static Map httpURLConnectionPOST_getFile (Map map) throws IOException {
        String param= StringUtil.safeToString(map.get("param"), "");
        String urlString= StringUtil.safeToString(map.get("url"), "");
        // 创建连接
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/json");//以json的方式传递参数
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3*60*1000);
        connection.setRequestProperty("Charsert", "UTF-8");
        connection.connect();

//        // POST请求
//        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
//        out.write(param.getBytes("UTF-8"));//参数需要json格式(其实就是一个字符串)
//        out.flush();
//        out.close();

//        // 测试 保存文件流
//        int bytesum = 0;
//        int byteread = 0;
//        FileOutputStream fs = new FileOutputStream("d:/a.ppt");
//
//
//        byte[] buffer = new byte[1204];
//        int length;
//        InputStream inStream = connection.getInputStream();
//        while ((byteread = inStream.read(buffer)) != -1) {
//            bytesum += byteread;
//            System.out.println(bytesum);
//            fs.write(buffer, 0, byteread);
//        }



        // 断开连接
        connection.disconnect();
        Map resultmap = new HashMap();

        //判断HTTP状态
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            resultmap.put("InputStream",connection.getInputStream());
        }
        resultmap.put("ResponseCode",connection.getResponseCode());
        LOGGER.debug(resultmap.toString());
        return resultmap;
    }


    /**
     * 表单提交
     *
     * @param urlStr
     * @param textMap
     * @param filelist
     * @return
     */
    public static String formUpload(String urlStr, Map<String, String> textMap, List<Map> filelist, Map<String, String> requestProperty) {
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Charsert", "utf-8");
            String sys_encoding = System.getProperty("file.encoding");
            LOGGER.debug("系统默认编码="+sys_encoding);

            for ( String rpkey  : requestProperty.keySet()    ) {
                conn.setRequestProperty(rpkey, requestProperty.get(rpkey));
            }

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                for (String key: textMap.keySet()){
                    String inputName = key;
                    String inputValue = (String) textMap.get(key);
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\"").append(inputName).append("\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes("UTF-8"));
            }

            // 文件
            for (Map fileMap : filelist){
                String key =   StringUtil.safeToString( fileMap.get("key"),"key");
                String filename =   StringUtil.safeToString( fileMap.get("filename"),"filename");
                FileInputStream fileInputStream = (FileInputStream) fileMap.get("content" );
                DataInputStream in = new DataInputStream(fileInputStream);

                String inputName = key;
                String contentType = "";
                if (filename.endsWith(".png")) {
                    contentType = "image/png";
                }
                if (contentType == null || contentType.equals("")) {
                       contentType = "application/octet-stream";
                 }
                StringBuffer strBuf = new StringBuffer();
                strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                strBuf.append("Content-Disposition: form-data; name=\"").append(inputName).append("\"; filename=\"").append(filename).append("\"\r\n");
                strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                out.write(strBuf.toString().getBytes("UTF-8"));
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                   out.write(bufferOut, 0, bytes);
                }
                in.close();
            }



            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取返回数据
            res = ConvertStream2Json(conn.getInputStream());
            LOGGER.debug(res);
        } catch (Exception e) {
            LOGGER.error("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }


}
