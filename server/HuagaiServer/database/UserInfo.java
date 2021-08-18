package database;

import java.util.ArrayList;
import java.util.List;

public class UserInfo{
    private String identy;
    private String userName;
    private String passwd;
    private String phoneNumber;
    private String emailNumber;
    private String age;
    private String hwMettingAccount;
    private String hwMettingPasswd;
    private String confId;
    private List<String> students = new ArrayList<>();
    private static UserInfo mInstance = null;

    public synchronized static UserInfo getInstance(){
        if( mInstance == null ){
            mInstance = new UserInfo();
        }
        return mInstance;
    }

    public synchronized static void setInstance(UserInfo info){
        mInstance = info;
    }
    public UserInfo(){
        this.identy = "";
        this.userName = "";
        this.passwd = "";
        this.phoneNumber = "";
        this.emailNumber = "";
        this.age = "";
        this.hwMettingAccount = "";
        this.hwMettingPasswd= "";
        this.confId = "";
        students.clear();
    }
    public String getHwMettingAccount() {
        return hwMettingAccount;
    }
    public void setHwMettingAccount(String hwMettingAccount) {
        this.hwMettingAccount = hwMettingAccount;
    }

    public String getHwMettingPasswd() {
        return hwMettingPasswd;
    }

    public void setHwMettingPasswd(String hwMettingPasswd) {
        this.hwMettingPasswd = hwMettingPasswd;
    }

    public String getConfId() {
        return confId;
    }

    public void setConfId(String confId) {
        this.confId = confId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public  String getPhoneNumber() {
        return phoneNumber;
    }

    public  void setPhoneNumber(String phoneNumber) {
        this.phoneNumber= phoneNumber;
    }

    public String getEmailNumber() {
        return emailNumber;
    }

    public void setEmailNumber(String emailNumber) {
        this.emailNumber = emailNumber;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getIdenty() {
        return identy;
    }

    public void setIdenty(String identy) {
        this.identy = identy;
    }
    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }
}