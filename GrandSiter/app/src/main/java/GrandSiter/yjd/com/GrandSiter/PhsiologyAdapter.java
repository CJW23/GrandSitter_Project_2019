package GrandSiter.yjd.com.GrandSiter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//각 월 대소변 시간 데이터 recycler뷰 출
public class PhsiologyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE = 2;
    private  static final int HVIEW_TYPE = 1;
    private Context context;
    private List<PhysiologyListItem> listItems;

    public PhsiologyAdapter(List<PhysiologyListItem> listItems, Context context){
        this.context = context;
        this.listItems = listItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //헤더
        if(viewType == HVIEW_TYPE){
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.physiology_header_item, parent, false);//레이아웃 만들기
            return new HViewHolder(v);
        }
        //시간 내용
        else{
            View v = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.physiology_date_item, parent, false);

            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final PhysiologyListItem listItem = listItems.get(position);
        if(holder instanceof HViewHolder){
            ((HViewHolder) holder).date.setText(listItem.getTime());
        }
        else{
            ((ViewHolder) holder).date.setText(listItem.getTime());
        }

    }

    @Override
    public int getItemViewType(int position){
        if(listItems.get(position).getViewType() == 1) {
            return HVIEW_TYPE;      //헤더
        }
        else{
            return VIEW_TYPE;       //시간 내
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    //시간 출력 holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView date;
        public final View view;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.physiology_date);
            view = itemView;
        }
    }
    //헤더 날짜 출력 holder
    public class HViewHolder extends RecyclerView.ViewHolder{
        public TextView date;
        public final View view;

        public HViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.physiology_header);
            view = itemView;
        }
    }
}
