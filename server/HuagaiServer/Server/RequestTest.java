package Server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.google.gson.Gson;

import DataType.DataPackageType;
import database.DataInfo;
import database.SyncPackage;
import database.UserInfo;
import net.sf.json.JSONObject;

public class RequestTest{
    private InputStream suInput = null;
    private InputStream input = null;
    private DataInputStream dataRead = null;
    private byte[] buff;
    private DataInfo dataJson = null;
    private JSONObject  jsonObject = null;
    private Socket socket = null;
    private ResponseTest responseTest;
    private SyncPackage syncPackage = null;
    private int type = 0;
    private Socket surSocket = null;
    public RequestTest(Socket socket, Socket suSocket){ 
        this.socket = socket;
        this.surSocket = suSocket;
        try {
            input = socket.getInputStream();
            suInput = surSocket.getInputStream();
            dataRead = new DataInputStream(new BufferedInputStream(input));
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseTest = new ResponseTest(socket,surSocket);
        dataJson = DataInfo.getInstance();
        jsonObject = dataJson.getDataJson();
    }

    public void handleRequest() throws IOException{
        System.out.println("处理请求！");
        buff = new byte[4];
        int len = dataRead.read(buff,0,4);
        if(len != 4){
            System.out.println("read error pleasecheck");
            return;
        }
        type = DataPackageType.byteArrayToInt(buff);
        System.out.println("type = " + type);
        switch(type){
            case DataPackageType.LOGIN_INFO_TYPE:
                if(LoginAuthentication()){
                    responseTest.handleResponse(DataPackageType.LOGIN_INFO_SUCCESSFUL,"服务器回复:登录成功！");
                }else{
                    responseTest.handleResponse(DataPackageType.LOGIN_INFO_FAILURE, "服务器回复:账号和密码错误！");
                }
                break;
            case DataPackageType.LOGON_INFO_TYPE:
                if(isLogonAythentication()){
                    responseTest.handleResponse(DataPackageType.LOGON_INFO_SUCCESSFUL,"服务器回复:注册成功,请重新登录！");
                }else{
                    responseTest.handleResponse(DataPackageType.LOGON_INFO_FAILURE, "服务器回复:注册失败，用户名已存在！");
                }
                break;
            case DataPackageType.SUBMMIT_PICTURE_TYPE:
            case DataPackageType.SUBMMIT_MARK_PICTURE_TYPE:
                if(isSubmitSuccessful()){
                    responseTest.handleResponse(DataPackageType.SUBMMIT_PICTURE_SUCCESSFUL,"服务器回复:上传成功！");
                }else{
                    responseTest.handleResponse(DataPackageType.SUBMMIT_PICTURE_FAILURE, "服务器回复:上传失败！");
                }
                break;
            case DataPackageType.DOWNLOAD_PICTURE_TYPE:
                if(isDownloadPrePare()){
                    responseTest.setSyncPackage(syncPackage);
                    responseTest.handleResponse(DataPackageType.DOWNLOAD_PICTURE_PREPARE,"服务器回复:准备加载图片！");
                }else{
                    responseTest.handleResponse(DataPackageType.SUBMMIT_PICTURE_FAILURE, "服务器回复:加载图片失败");
                }
                break;   
            default:
                break;    
        }
    }

    private boolean isDownloadPrePare() throws IOException{
        System.out.println("开始发送图片数据");
        if(buff != null){
            buff = null;
        }
        buff = new byte[4];
        dataRead.read(buff,0,4);
        // int totalLength = DataPackageType.byteArrayToInt(buff);
        dataRead.read(buff,0,4);
        int gsonLen = DataPackageType.byteArrayToInt(buff);
        if(buff != null){
            buff = null;
        }
        buff = new byte[gsonLen];
        dataRead.read(buff,0,gsonLen);
        String gsonS = new String(buff,"UTF-8");
        System.out.println("download image = " + gsonS);
        syncPackage = new Gson().fromJson(gsonS, SyncPackage.class);
        if(syncPackage == null){
            return false;
        }
            return true;
    }

    private boolean isSubmitSuccessful() throws IOException{
        System.out.println("开始接受图片数据");
        if(buff != null){
            buff = null;
        }
        buff = new byte[4];
        dataRead.read(buff,0,4);
        int totalLength = DataPackageType.byteArrayToInt(buff);
        System.out.println("image totalLength = " + totalLength);
        dataRead.read(buff,0,4);
        int nameLen = DataPackageType.byteArrayToInt(buff);
        dataRead.read(buff,0,4);
        int filenameLength = DataPackageType.byteArrayToInt(buff);
        dataRead.read(buff,0,4);
        int totalFileLength = DataPackageType.byteArrayToInt(buff);
        buff = null;
        buff = new byte[nameLen];
        dataRead.read(buff,0,nameLen);
        String name = new String(buff,"UTF-8");
        System.out.println("name = " + name);
        buff = null;
        buff = new byte[filenameLength];
        dataRead.read(buff,0,filenameLength);
        String fileName = new String(buff,"UTF-8");
        System.out.println("fileName = " + fileName);
        buff = null;
        int len = -1;
        int offset = 0;
        buff = new byte[totalFileLength];
        byte[] buffer = new byte[8192];
        while((len = dataRead.read(buffer))!=-1){
            System.out.println("len = " + len +" totalFileLenght = " + totalFileLength );
            System.out.println("datalast len = " + dataRead.available());
            System.arraycopy(buffer, 0, buff, offset, len);
            offset +=len;
            if(offset >= totalFileLength){
                break;
            }
        }
        if(offset <= totalFileLength){
            saveImage(name, fileName, buff);
            buff = null;
            return true;
        }
        return false;
    }


    private void saveImage(String userName,String fileName,byte[] fileByte) throws IOException{
        String storagePath = null;
        String oldPath = null;
        if(type == DataPackageType.SUBMMIT_PICTURE_TYPE){
            storagePath = "D:\\lulu\\" + userName + "\\unMarking\\"  + fileName;
        }
        if(type == DataPackageType.SUBMMIT_MARK_PICTURE_TYPE){
            storagePath = "D:\\lulu\\" + userName + "\\Marking\\"  + fileName;
            oldPath = "D:\\lulu\\" + userName + "\\unMarking\\"  + fileName;
            File oldFile = new File(oldPath);
            if(oldFile.exists()){
                oldFile.delete();
            }
        }
        
        File storageFile = new File(storagePath);
        if(!storageFile.getParentFile().exists()){
            storageFile.getParentFile().mkdirs();
        }
        System.out.println("存储路径为path=" + storagePath );
        FileOutputStream fot = null;
        try {
            fot = new FileOutputStream(storageFile);
            fot.write(fileByte,0,fileByte.length);
            fot.flush();
            fot.close();
            fot = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }

    private boolean isLogonAythentication() throws IOException{
        System.out.println("开始校验注册信息");
        if(buff != null){
            buff = null;
        }
        buff = new byte[4];
        dataRead.read(buff,0,4);
        int totalLength = DataPackageType.byteArrayToInt(buff);
        System.out.println("totalLength = " + totalLength);
        dataRead.read(buff,0,4);
        int gsonLength = DataPackageType.byteArrayToInt(buff);
        System.out.println("gsonLength = " + gsonLength);
        if(buff !=null){
            buff = null;
        }
        byte[] gsonByte = new byte[gsonLength];
        readStringByte(gsonByte, 0, gsonLength);
        System.out.println("gson = " + new String(gsonByte,"UTF-8"));
        if(isLogon(new String(gsonByte,"UTF-8"))){
            return true;
        }
        return false;

    }

    private boolean isLogon(String userGson){
        UserInfo userInfo = new Gson().fromJson(userGson, UserInfo.class);
        if(jsonObject.has(userInfo.getUserName())){
            //用户名已经存在
            return false; 
        }
        jsonObject.put(userInfo.getUserName(), userGson);
        System.out.println("总的数据字符串为：" + dataJson.getDataJson().toString());
        return true;
    }

    private boolean LoginAuthentication() throws IOException{
        System.out.println("开始校验登录信息");
        if(buff != null){
            buff = null;
        }
        buff = new byte[4];
        dataRead.read(buff,0,4);
        int totalLen = DataPackageType.byteArrayToInt(buff);
        System.out.println("totalLen = " + totalLen);
        dataRead.read(buff,0,4);
        int nameLen = DataPackageType.byteArrayToInt(buff);
        System.out.println("nameLen = " + nameLen);
        dataRead.read(buff,0,4);
        int passWordLen = DataPackageType.byteArrayToInt(buff);
        System.out.println("passwordLen = " + passWordLen);
        if(buff != null){
            buff = null;
        }
        byte[] nameByte = new byte[nameLen];
        readStringByte(nameByte, 0, nameLen);
        byte[] passWdByte = new byte[passWordLen];
        readStringByte(passWdByte, 0, passWordLen);
        System.out.println("name = " + new String(nameByte,"UTF-8"));
        System.out.println("passwd = " + new String(passWdByte,"UTF-8"));
        if(isLogin(new String(nameByte,"UTF-8"), new String(passWdByte,"UTF-8"))){
            return true;
        }
        return false;
    }
   
    public boolean isLogin(String name,String passWd){
        if(jsonObject.has(name)){
            String gInfo = jsonObject.getString(name);
            UserInfo userInfo = (UserInfo) new Gson().fromJson(gInfo, UserInfo.class);
            if(userInfo.getPasswd().equals(passWd)){
                UserInfo.setInstance(userInfo);
                return true;
            }
        }
        return false;
    }


    public void readStringByte(byte[] data, int des,int length) throws IOException{
        int len = 1024;
        while(len < length){
            buff = new byte[1024];
            dataRead.read(buff,0,1024);
            System.arraycopy(buff, 0, data, (des + len - 1024), 1024);
            len += 1024;
        }
        int lastLen = length + 1024 -len;
        buff = new byte[lastLen];
        dataRead.read(buff, 0, lastLen);
        System.arraycopy(buff, 0, data, (des + len - 1024), lastLen);
    }

    public void requestRelease() throws IOException{
        if(dataRead != null){
            dataRead.close();
        }
        if(input != null){
            input.close();
        }
        if(socket != null){
            socket.close();
        }
        if(suInput != null){
            suInput.close();
        }
        suInput = null;
        dataRead = null;
        input = null;
        socket = null;
        responseTest.responseRelease();
    }

}