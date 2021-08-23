package database;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;





public class DataInfo {
    private static ConcurrentHashMap<String,String> dataJson = null;
    private static DataInfo mInstance = null;
    public DataInfo(){
        dataJson = new ConcurrentHashMap<>();
        UserInfo info = new UserInfo();
        info.setUserName("1111");
        info.setPasswd("1111");
        info.setIdenty("教师");
        info.setHwMettingAccount("13052357026");
        info.setHwMettingPasswd("m6219947036");
        List<String> tmp = new ArrayList<>();
        tmp.add("2222");
        tmp.add("3333");
        tmp.add("4444");
        tmp.add("5555");
        tmp.add("6666");
        tmp.add("7777");
        tmp.add("8888");
        tmp.add("9999");
        tmp.add("1010");
        tmp.add("1011");
        tmp.add("1012");
        tmp.add("1013");
        tmp.add("1014");
        tmp.add("1015");
        tmp.add("1016");
        tmp.add("1017");
        tmp.add("1018");
        info.setStudents(tmp);
        String gInfo = new Gson().toJson(info);
        dataJson.put(info.getUserName(), gInfo);
        UserInfo info2 = new UserInfo();
        info2.setUserName("2222");
        info2.setPasswd("2222");
        info2.setIdenty("学生");
        String gInfo2 = new Gson().toJson(info2);
        dataJson.put(info2.getUserName(), gInfo2);
        UserInfo info3 = new UserInfo();
        info3.setUserName("3333");
        info3.setPasswd("3333");
        info3.setIdenty("学生");
        String gInfo3 = new Gson().toJson(info3);
        dataJson.put(info3.getUserName(), gInfo3);

    }
    public static synchronized DataInfo getInstance(){
        if(mInstance == null){
            mInstance = new DataInfo();
        }
        return mInstance;
    }
    public ConcurrentHashMap<String,String> getDataJson() {
        return dataJson;
    }
    public static void setDataJson(ConcurrentHashMap<String,String> dataJson) {
        DataInfo.dataJson = dataJson;
    }

    public void addInfo(String info, String jsonObject){
        dataJson.put(info, jsonObject);
    
    }
    
}

