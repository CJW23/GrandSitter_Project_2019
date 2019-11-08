package GrandSiter.yjd.com.GrandSiter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {

    private List<ScheduleListItem> listItems;
    private Context context;
    private String scheId;

    public ScheduleListAdapter(List<ScheduleListItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }
    @NonNull
    @Override
    public ScheduleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleListAdapter.ViewHolder holder, int position) {

        final ScheduleListItem listItem1 = listItems.get(position);
        scheId = listItem1.getId();
        Log.d("zxczxc11", listItem1.getName());
        holder.sc_name.setText(listItem1.getName());
        holder.sc_date.setText(listItem1.getDate());
        holder.sc_des.setText(listItem1.getDes());
        holder.scView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("일정 ");
                alertDialogBuilder
                        .setMessage("일정이 마무리 됬나요?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteSche();
                                        notifyDataSetChanged();
                                        Intent intent = ((Activity) context).getIntent();
                                        ((Activity) context).finish();
                                        context.startActivity(intent);
                                    }
                                })
                        .setNegativeButton("아니요",
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
    }
    public void deleteSche(){
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
        DeleteScheRequest deleteScheRequest = new DeleteScheRequest(scheId, responseListener);
        RequestQueue qu = Volley.newRequestQueue(context);
        qu.add(deleteScheRequest);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sc_name;
        public TextView sc_date;
        public TextView sc_des;

        public final View scView;
        public ViewHolder(View itemView) {
            super(itemView);
            sc_name = (TextView) itemView.findViewById(R.id.scName);
            sc_date = (TextView)itemView.findViewById(R.id.scTime);
            sc_des = (TextView)itemView.findViewById(R.id.scDes);
            scView = itemView;
        }
    }
}
