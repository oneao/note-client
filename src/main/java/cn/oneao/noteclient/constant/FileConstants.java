package cn.oneao.noteclient.constant;

import java.io.File;

public class FileConstants {
    //    public static String ASSETS_PATH = System.getProperty("user.dir");
    public static String ASSETS_PATH = new File(System.getProperty("user.dir")).getParent();
    public static String USER_AVATAR = "file:" + ASSETS_PATH + "/img/userAvatar/";
}
