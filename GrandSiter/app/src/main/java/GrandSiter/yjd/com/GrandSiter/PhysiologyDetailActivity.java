package GrandSiter.yjd.com.GrandSiter;


import androidx.core.widget.ContentLoadingProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class PhysiologyDetailActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBSheet;
    private ToggleButton tbUpDown;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private TextView txtCantante, txtCancion;
    private ContentLoadingProgressBar progbar;
    private List<DateListItem> listItem;
    private DateListItem dateItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physiology_detail);
        init();
        onClick();
        listItem.add(new DateListItem("awd"));
        listItem.add(new DateListItem("zxc"));
        listItem.add(new DateListItem("wcs"));
        adapter = new DateListAdapter(listItem, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    private void init() {
        this.linearLayoutBSheet = findViewById(R.id.bottomSheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        this.tbUpDown = findViewById(R.id.toggleButton);
        this.txtCantante = findViewById(R.id.txtCantante);
        this.txtCancion = findViewById(R.id.txtCancion);
        this.listItem = new ArrayList<>();
        this.recyclerView = (RecyclerView) findViewById(R.id.daterecycler);
    }
    private void onClick(){
        tbUpDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View view, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    tbUpDown.setChecked(true);
                }else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    tbUpDown.setChecked(false);
                }
            }

            @Override
            public void onSlide(View view, float v) {

            }
        });
    }
    public void changeData(DateListItem item){
        dateItem = item;
        Log.d("awd", dateItem.getDate());
    }
}
