package GrandSiter.yjd.com.GrandSiter;

public class ScheduleListItem {
    private String name;
    private String date;
    private String des;
    private String id;

    public ScheduleListItem(String name, String date, String des, String id){
        this.name = name;
        this.date = date;
        this.des = des;
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getId() {return id;}

    public void setId(String id){this.id = id;}
}
