package GrandSiter.yjd.com.GrandSiter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.ViewHolder> {

    private List<DateListItem> listItems;
    private Context context;
    public DateListAdapter(List<DateListItem> listItems, Context context){
        this.listItems = listItems;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("viewtype : ", Integer.toString(viewType));
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final DateListItem listItem = listItems.get(position);

        holder.date.setText(listItem.getDate());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PhysiologyDetailActivity)context).changeBottomSheet();    //클릭시 bottomsheet 내려감
                ((PhysiologyDetailActivity)context).changeData(listItems.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("awd", Integer.toString(listItems.size()));
        return listItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView date;
        public final View view;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            view = itemView;
        }
    }


}
