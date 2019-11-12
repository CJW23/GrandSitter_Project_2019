package GrandSiter.yjd.com.GrandSiter;

import android.content.Context;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * Created by jhb on 2019/2/22 0022.
 */
public class MyMarkerView extends MarkerView {


    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(0, -getHeight()-20);
    }

}
