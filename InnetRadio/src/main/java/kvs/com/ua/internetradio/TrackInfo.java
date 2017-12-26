package kvs.com.ua.internetradio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class TrackInfo {

    private String cutTrackInfo(String str) {
        str = str.substring(48, str.length() - 16);
        str = str.replaceAll("@", "");
        return  str + "                              ";
    }

    public String getTrackInfo(String urlPath) {
        int[] bytes = new int[200];
        int index = 0;
        try {
            URL urlTrack = new URL(urlPath);
            URLConnection connect = urlTrack.openConnection();
            InputStream inStream = connect.getInputStream();
            BufferedInputStream bufferedInput = new BufferedInputStream(inStream);

            int b;
            while ((b = bufferedInput.read()) != -1) {
                bytes[index++] = b;
            }
            bufferedInput.close();

        }catch (IOException ex) {
            return "";
        }
        byte[] newBytes= new byte[index];
        for (int i = 0; i < newBytes.length; i++) {
            newBytes[i]=(byte) bytes[i];
        }

        String trackInfo;
        try {
            trackInfo = new String(newBytes,"windows-1251");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
        return cutTrackInfo(trackInfo);
    }
}