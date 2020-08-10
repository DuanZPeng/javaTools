package org.leihuo.tools.io;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * @Description:
 * @Company：com.ylz
 * @Auther: 段志鹏
 * @Date: 2020/8/7 16:10
 */
public class HttpUtils {

    private static final class DefaultTrustManager implements X509TrustManager {


        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static HttpsURLConnection getHttpsURLConnection(String uri, String method) throws IOException {
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SSLSocketFactory ssf = ctx.getSocketFactory();

        URL url = new URL(uri);
        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
        httpsConn.setSSLSocketFactory(ssf);
        httpsConn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        httpsConn.setRequestMethod(method);
        httpsConn.setDoInput(true);
        httpsConn.setDoOutput(true);
        return httpsConn;
    }

    private static byte[] getBytesFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] kb = new byte[1024];
        int len;
        while ((len = is.read(kb)) != -1) {
            baos.write(kb, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        baos.close();
        is.close();
        return bytes;
    }

    private static void setBytesToStream(OutputStream os, byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        byte[] kb = new byte[1024];
        int len;
        while ((len = bais.read(kb)) != -1) {
            os.write(kb, 0, len);
        }
        os.flush();
        os.close();
        bais.close();
    }

    public static byte[] doGet(String uri) throws IOException {
        HttpsURLConnection httpsConn = getHttpsURLConnection(uri, "GET");
        return getBytesFromStream(httpsConn.getInputStream());
    }



    public static String doGetBase64(String uri) throws IOException {
        HttpsURLConnection httpsConn = getHttpsURLConnection(uri, "GET");
        byte[] data = getBytesFromStream(httpsConn.getInputStream());
        String base64 = Base64.getEncoder().encodeToString(data);
        return base64;//返回Base64编码过的字节数组字符串
    }

    public static byte[] doPost(String uri, String data) throws IOException {
        HttpsURLConnection httpsConn = getHttpsURLConnection(uri, "POST");
        setBytesToStream(httpsConn.getOutputStream(), data.getBytes());
        return getBytesFromStream(httpsConn.getInputStream());
    }




    public static void main(String[] args) throws IOException {
        String uri = "https://www.baidu.com";
        byte[] bytes = doGet(uri);
        System.out.println(new String(bytes,"utf-8"));
        FileOutputStream fos = new FileOutputStream("D:/bing.txt");
        fos.write(bytes);
        fos.close();
        System.out.println("done!");
        byte[] bytes1 = "hello world".getBytes();        //Verify original content
        System.out.println( new String(bytes1,"utf-8") );
    }


    public static byte[] request(String urlString) throws Exception {

        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5*1000);
        // 输入流
        InputStream is = con.getInputStream();

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;


        return null;
    }
}
