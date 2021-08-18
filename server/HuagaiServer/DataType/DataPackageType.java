package DataType;

public class DataPackageType {
    public static final int LOGIN_INFO_TYPE = 1;
    public static final int LOGIN_INFO_SUCCESSFUL = 2;
    public static final int LOGIN_INFO_FAILURE = 3;
    public static final int LOGON_INFO_TYPE= 4;
    public static final int LOGON_INFO_SUCCESSFUL = 5;
    public static final int LOGON_INFO_FAILURE = 6;
    public static final int SUBMMIT_PICTURE_TYPE = 7;
    public static final int SUBMMIT_PICTURE_SUCCESSFUL = 8;
    public static final int SUBMMIT_PICTURE_FAILURE = 9;
    public static final int DOWNLOAD_PICTURE_TYPE = 10;
    public static final int DOWNLOAD_PICTURE_SUCCESSFUL = 11;
    public static final int DOWNLOAD_PICTURE_FAILURE = 12;
    public static final int DOWNLOAD_PICTURE_PREPARE = 13;
    public static final int DOWNLIAD_PICTURE_PROCESS = 14;
    public static final int DOWNLOAD_NOTHING_DO = 15;
    public static final int DOWNLOAD_SOMETHING_ERROR = 16;
    public static final int SUBMMIT_MARK_PICTURE_TYPE = 17;
    public DataPackageType(){
        
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }
    /**
     * byte[]转int
     * @param bytes 需要转换成int的数组
     * @return int值
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value=0;
        for(int i = 0; i < 4; i++) {
            int shift= (3-i) * 8;
            value +=(bytes[i] & 0xFF) << shift;
        }
        return value;
    }
}
