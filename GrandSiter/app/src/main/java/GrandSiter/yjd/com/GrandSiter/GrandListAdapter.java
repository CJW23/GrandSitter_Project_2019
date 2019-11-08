package GrandSiter.yjd.com.GrandSiter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class GrandListAdapter extends RecyclerView.Adapter<GrandListAdapter.ViewHolder>{

    private List<GrandListItem> listItems;
    private Context context;

    public GrandListAdapter(List<GrandListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void setListItems(List<GrandListItem> listItems) {
        this.listItems = listItems;
    }
    @NonNull
    @Override
    public GrandListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grand_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final GrandListAdapter.ViewHolder holder, final int position) {
        final GrandListItem listItem = listItems.get(position);

        holder.name.setText(listItem.getName());
        holder.age.setText("생년월일 : " + listItem.getAge());
        holder.gender.setText("성별 : " + listItem.getGender());
        holder.chracteristic.setText("질환 : " + listItem.getCharacteristic());

        //클릭했을 시 노인 정보창 이
        holder.mView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GrandStatusActivity.class);
                intent.putExtra("id", listItem.getId());
                intent.putExtra("name", listItem.getName());
                intent.putExtra("gender", listItem.getGender());
                intent.putExtra("age", listItem.getAge());
                intent.putExtra("ch", listItem.getCharacteristic());
                context.startActivity(intent);
            }
        });
        //길게 터치 시 삭제/수정 여부
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AlertDialog.Builder alertdialog = new AlertDialog.Builder(context);
                Log.d("cdcdcdc : ", Integer.toString(position));
                //환자 삭제시 Http 통신
                alertdialog.setMessage("환자를 삭제하시겠습니까?").setCancelable(false).
                        setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try{
                                            listItems.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            notifyItemRangeChanged(holder.getAdapterPosition(), listItems.size());
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                DeleteGrandRequest deleteGrandRequest = new DeleteGrandRequest(listItem.getId(), responseListener);
                                RequestQueue qu = Volley.newRequestQueue(context);
                                qu.add(deleteGrandRequest);
                            }
                        }).
                        setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = alertdialog.create();
                alert.setTitle("알림");
                alert.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView age;
        public TextView gender;
        public TextView chracteristic;
        public final View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.grname);
            age = (TextView)itemView.findViewById(R.id.grage);
            gender = (TextView)itemView.findViewById(R.id.grgender);
            chracteristic = (TextView)itemView.findViewById(R.id.grcharacteristic);
            mView = itemView;
        }
    }


}
