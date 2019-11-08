package GrandSiter.yjd.com.GrandSiter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.repsly.library.timelineview.LineType;
import com.repsly.library.timelineview.TimelineView;
import org.json.JSONObject;
import java.util.List;

class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder>{
    private static final int MEDI = 10, SCHE = 11;
    private final int orientation;
    private final List<TimeLineListItem> items;
    private Context context;
    private int type;

    TimelineAdapter(int orientation, List<TimeLineListItem> items, Context context, int type){
        this.orientation = orientation;
        this.items = items;
        this.type = type;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position){
        if (orientation == LinearLayoutManager.VERTICAL) {
            return R.layout.item_vertical;
        } else {
            return R.layout.item_horizontal;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final TimeLineListItem item = items.get(position);

        holder.tvTime.setText(item.getTime());
        holder.tvDes.setText(item.getDes());
        holder.timelineView.setLineType(getLineType(position));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                Log.d("click ", Integer.toString(holder.getAdapterPosition()));
                alertDialogBuilder.setTitle("약 시간");
                alertDialogBuilder
                        .setMessage("약 시간을 제거 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        delete(items.get(holder.getAdapterPosition()).getId());
                                        items.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(), items.size());
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        holder.timelineView.setFillMarker(true);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    private LineType getLineType(int position) {
        if (getItemCount() == 1) {
            return LineType.ONLYONE;
        } else {
            if (position == 0) {
                return LineType.BEGIN;

            } else if (position == getItemCount() - 1) {
                return LineType.END;

            } else {
                return LineType.NORMAL;
            }
        }
    }

    //일정, 약 데이터 지우
    public void delete(final String id){
        switch(type){
            case MEDI:{
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                DeleteMediRequest deleteMediRequest = new DeleteMediRequest(id, responseListener);
                RequestQueue qu = Volley.newRequestQueue(context);
                qu.add(deleteMediRequest);
                break;
            }
            case SCHE:{
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                DeleteScheRequest deleteScheRequest = new DeleteScheRequest(id, responseListener);
                RequestQueue qu = Volley.newRequestQueue(context);
                qu.add(deleteScheRequest);
                break;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TimelineView timelineView;
        TextView tvTime;
        TextView tvDes;
        final View view;
        ViewHolder(View view) {
            super(view);
            timelineView = (TimelineView) view.findViewById(R.id.timeline);
            //timelineView.setMarkerColor(R.color.colorPrimary);
            //colorType();
            tvTime = (TextView) view.findViewById(R.id.tv_time);
            tvDes = (TextView) view.findViewById(R.id.tv_des);
            this.view = view;
        }
        void colorType(){
            if(type == MEDI)
                timelineView.setMarkerColor(R.color.colorPrimary);
            else
                timelineView.setMarkerColor(R.color.springgreen);
        }
    }
}
