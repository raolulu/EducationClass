package database;

import java.util.ArrayList;
import java.util.List;

public class SyncPackage {
    private int type;
    private String name;
    private List<String> pictureList = new ArrayList<>();
    private int mark;
    public SyncPackage(){
        type = -1;
        name = "";
        pictureList.clear();
        mark = -1;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPictureList() {
        return pictureList;
    }

    public void setPictureList(List<String> pictureList) {
        this.pictureList.addAll(pictureList);
    }
}

