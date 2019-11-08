package GrandSiter.yjd.com.GrandSiter;

import android.support.annotation.NonNull;

public class TimeLineListItem{
    private String time;
    private String des;
    private String id;

    public TimeLineListItem(String time, String des, String id){
        this.time = time;
        this.des = des;
        this.id = id;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String Time) {
        this.time = time;
    }
    public String getId(){ return id;}

    public void setId(String id){ this.id = id;}

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

}
