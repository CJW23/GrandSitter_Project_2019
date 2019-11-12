package GrandSiter.yjd.com.GrandSiter;

public class PhysiologyListItem {
    private int viewType;
    private String time;

    public PhysiologyListItem(String time, int viewType){
        this.viewType = viewType;
        this.time = time;
    }
    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
