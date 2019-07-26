package GrandSiter.yjd.com.GrandSiter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

import GrandSiter.yjd.com.GrandSiter.R;

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
    public void onBindViewHolder(@NonNull GrandListAdapter.ViewHolder holder, int position) {
        final GrandListItem listItem = listItems.get(position);

        holder.name.setText(listItem.getName());
        holder.age.setText("생년월일 : " + listItem.getAge());
        holder.gender.setText("성별 : " + listItem.getGender());
        holder.chracteristic.setText("질환 : " + listItem.getCharacteristic());

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
