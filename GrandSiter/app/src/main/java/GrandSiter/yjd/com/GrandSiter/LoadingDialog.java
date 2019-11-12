package GrandSiter.yjd.com.GrandSiter;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingDialog {
    private Context context;
    private int loadingNum, loadingFlag;
    private ProgressDialog dialog;

    public LoadingDialog(Context context, int loadingNum) {
        this.context = context;
        this.loadingNum = loadingNum;
    }
    //다이얼로그 출력
    void makeDialog(){
        dialog = new ProgressDialog(context);
        dialog.setMax(100);
        dialog.setMessage("정보");
        dialog.setTitle("로딩 중");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(true){
                        if(loadingFlag >=  loadingNum){
                            dialog.dismiss();
                            break;
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    void finishLoading() {
        loadingFlag++;
    }
}

