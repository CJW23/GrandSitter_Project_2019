package GrandSiter.yjd.com.GrandSiter;

/**
 * Created by Jeffrey Liu on 2/2/16.
 */
public class ChartData {

    private String xAxisName;
    private int yValue;

    public ChartData(String xAxisName, int yValue) {
        this.xAxisName = xAxisName;
        this.yValue = yValue;
    }

    public String getXAxisName() {
        return xAxisName;
    }

    public int getYValue() {
        return yValue;
    }
}
