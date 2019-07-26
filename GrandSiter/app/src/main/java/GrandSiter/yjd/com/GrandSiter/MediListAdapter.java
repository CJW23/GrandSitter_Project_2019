package GrandSiter.yjd.com.GrandSiter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

import GrandSiter.yjd.com.GrandSiter.R;

public class MediListAdapter extends RecyclerView.Adapter<MediListAdapter.ViewHolder>{

    private List<MediListItem> listItems;
    private Context context;
    private String mediId;

    public MediListAdapter(List<MediListItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;

    }

    @NonNull
    @Override
    public MediListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MediListAdapter.ViewHolder holder, int position) {

        final MediListItem listItem2 = listItems.get(position);
        mediId = listItem2.getId();
        Log.d("zxczxc", listItem2.getName());
        holder.medi_name.setText(listItem2.getName());
        holder.medi_date.setText(listItem2.getDate());
        holder.medi_des.setText(listItem2.getDes());
        holder.mediView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("약 시간");
                alertDialogBuilder
                        .setMessage("약 시간을 제거 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteMedi();
                                        Log.d("mediId", mediId);
                                        notifyDataSetChanged();
                                        Intent intent = ((Activity) context).getIntent();
                                        ((Activity) context).finish();
                                        context.startActivity(intent);
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
    }
    public void deleteMedi(){
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
        DeleteMediRequest deleteMediRequest = new DeleteMediRequest(mediId, responseListener);
        RequestQueue qu = Volley.newRequestQueue(context);
        qu.add(deleteMediRequest);
    }
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView medi_name;
        public TextView medi_date;
        public TextView medi_des;

        public final View mediView;
        public ViewHolder(View itemView) {
            super(itemView);
            medi_name = (TextView) itemView.findViewById(R.id.mediName);
            medi_date = (TextView)itemView.findViewById(R.id.mediTime);
            medi_des = (TextView)itemView.findViewById(R.id.mediDes);
            mediView = itemView;
        }
    }
}
