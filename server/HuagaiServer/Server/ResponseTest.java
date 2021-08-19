package Server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;



import DataType.DataPackageType;
import database.SyncPackage;
import database.UserInfo;

public class ResponseTest {
    private OutputStream outputStream = null;
    private OutputStream outputStream2 = null;
    private DataOutputStream writeData = null;
    private DataOutputStream writeData2 = null;
    private DataInputStream readData2 = null;
    private byte[] response;
    private Socket surSocket = null;
    private Socket socket = null;
    private UserInfo mUserInfo = null;
    private SyncPackage syncPackage = null;
    public ResponseTest(Socket socket, Socket surSocket){
        this.socket = socket;
        this.surSocket = surSocket;
        try {
            outputStream = socket.getOutputStream();
            outputStream2 = surSocket.getOutputStream();
            writeData = new DataOutputStream(new BufferedOutputStream(outputStream));
            writeData2 = new DataOutputStream(outputStream2);
            readData2 = new DataInputStream(this.surSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setSyncPackage(SyncPackage str){
        syncPackage = str;
    }

    public void handleResponse(int type ,String str) throws IOException{
            switch(type){
                case DataPackageType.LOGIN_INFO_SUCCESSFUL:
                    responseStringByte(type, str);
                    writeData.write(response,0,response.length);
                    writeData.flush();
                    mUserInfo = UserInfo.getInstance();
                    String j = new Gson().toJson(mUserInfo);
                    responseStringByte(type, j);
                    writeData.write(response,0,response.length);   //个人信息发送给客户更新
                    writeData.flush();  
                    break;
                case DataPackageType.LOGIN_INFO_FAILURE:
                case DataPackageType.LOGON_INFO_FAILURE:
                case DataPackageType.LOGON_INFO_SUCCESSFUL:
                case DataPackageType.SUBMMIT_PICTURE_FAILURE:
                case DataPackageType.SUBMMIT_PICTURE_SUCCESSFUL:
                case DataPackageType.DOWNLOAD_PICTURE_FAILURE:
                    responseStringByte(type, str);
                    writeData.write(response,0,response.length);
                    writeData.flush();
                    break;
                case DataPackageType.DOWNLOAD_PICTURE_PREPARE:
                    responseStringByte(type, str);
                    writeData.write(response,0,response.length);
                    writeData.flush();
                    sendImage();
                    break;
                default:
                
            }
    }

    public void sendImage() throws IOException{
        String name = syncPackage.getName();
        String storagePathDir = null;
        if(syncPackage.getMark() == 0){
            //storagePathDir = "D:\\lulu\\" + name + "\\unMarking";
	    storagePathDir = "dirPath/" + name + "/unMarking";
        }else{
            //storagePathDir = "D:\\lulu\\" + name + "\\Marking";
	    storagePathDir = "dirPath/" + name + "/Marking";
        }
        System.out.println("storagePathDir = " + storagePathDir);
        File dir = new File(storagePathDir);
        if(!dir.exists()){
            writeData.write(DataPackageType.intToByteArray(DataPackageType.DOWNLOAD_NOTHING_DO),0,4);
            writeData.flush();
            return;
        }
        File[] files = dir.listFiles();
        if(files.length == 0){
            writeData.write(DataPackageType.intToByteArray(DataPackageType.DOWNLOAD_NOTHING_DO),0,4);
            writeData.flush();
            return;
        }
        List<File> list = new ArrayList<>();
        for(int i = 0; i<files.length;i++){
            if(syncPackage.getPictureList().contains(files[i].getName())){
                continue;
            }
            list.add(files[i]);
        }
        if(list.size() == 0){
            writeData.write(DataPackageType.intToByteArray(DataPackageType.DOWNLOAD_NOTHING_DO),0,4);
            writeData.flush();
            return;
        }
        writeData.write(DataPackageType.intToByteArray(DataPackageType.DOWNLOAD_PICTURE_PREPARE),0,4);
        writeData.flush();
        writeData.write(DataPackageType.intToByteArray(list.size()), 0, 4);
        writeData.flush();
        for(int i =0;i<list.size();i++){
            FileInputStream finput = new FileInputStream(list.get(i));
            byte[] buff = new byte[4096];
            int len = 0;
            int fileLen = finput.available();
            int sumLen = 0;
            byte[] fileBuff = new byte[fileLen];
            while((len=finput.read(buff))!=-1){
                System.arraycopy(buff, 0, fileBuff, sumLen, len);
                sumLen += len;
            }
            finput.close();
            finput = null;
            String describe = "下载进度:" + (i+1) + "/" +list.size();
            System.out.println("describe = " + describe);
            byte[] des = describe.getBytes("UTF-8");
            int desLen = des.length;
            byte[] desLenByte = DataPackageType.intToByteArray(desLen);
            String fileName = list.get(i).getName();
            byte[] nameByte = fileName.getBytes("UTF-8");
            int nameLen = nameByte.length;
            byte[] nameLenByte = DataPackageType.intToByteArray(nameLen);
            byte[] fileLenByte = DataPackageType.intToByteArray(fileLen);
            int totalLen = 12 + desLen + fileLen + nameLen;
            byte[] totalData = new byte[totalLen];
            System.arraycopy(desLenByte, 0, totalData, 0, 4);
            System.arraycopy(nameLenByte, 0, totalData, 4, 4);
            System.arraycopy(fileLenByte, 0, totalData, 8, 4);
            System.arraycopy(des, 0,totalData, 12, desLen);
            System.arraycopy(nameByte, 0, totalData, 12+desLen, nameLen);
            System.arraycopy(fileBuff, 0, totalData, 12+desLen + nameLen, fileLen);
            writeData.write(totalData, 0, totalLen);
            writeData.flush();  
            writeData2.writeInt(i+1);
            writeData2.flush();
            byte[] resNumByte = new byte[4];
            readData2.read(resNumByte,0,4);
            int resNum = DataPackageType.byteArrayToInt(resNumByte);
            if(resNum != i+1){
                System.out.println("something error in send file resNum = " + resNum);
                break;
            }
        }
        
    }

    
    public void responseStringByte(int type,String str){
        try {
            byte[] strByte = str.getBytes("UTF-8");
            byte[] intByte = DataPackageType.intToByteArray(type);
            int totlalLen = 12 + strByte.length;
            byte[] totalLenByte = DataPackageType.intToByteArray(totlalLen);
            response = new byte[totlalLen];
            byte[] strLen = DataPackageType.intToByteArray(strByte.length);
            System.arraycopy(intByte, 0, response, 0, 4);
            System.arraycopy(totalLenByte, 0, response, 4, 4);
            System.arraycopy(strLen, 0, response, 8, 4);
            System.arraycopy(strByte, 0, response, 12, strByte.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    public void responseRelease(){
        if(writeData != null){
            try {
                writeData.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try{
        if(readData2 != null){
            readData2.close();
        }
        if(writeData2 != null){
            writeData2.close();
        }
        if(outputStream2 != null){
            outputStream2.close();
        }
        if(surSocket != null){
            surSocket.close();
        }
    }catch(IOException e){
        e.printStackTrace();
    }
        surSocket = null;
        readData2 = null;
        writeData2 = null;
        outputStream2 = null;
        writeData = null;
        outputStream = null;
        socket = null;

    }
}
