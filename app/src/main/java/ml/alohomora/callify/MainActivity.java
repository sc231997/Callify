package ml.alohomora.callify;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    int time=30;
    double maxHeight,increment ,height;
    TextView textViewCounter;
    RelativeLayout relativeLayoutBackground,relativeLayoutForeground;
    Button button;

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    Handler handlerCounter = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d("handler",msg.arg1+"");
            if(msg.arg1==time)
                button.setVisibility(View.INVISIBLE);
            else if(msg.arg1==0) {
                button.setVisibility(View.VISIBLE);
            }
            counterRefresh(msg.arg1);
        }
    };
    Handler handlerBackground = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            backgroundRefresh();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapBackground(true);
                final Runnable runnableBackground= new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<increment;i++){
                            handlerBackground.sendEmptyMessage(0);
                            try {
                                Thread.sleep((int)(1000/increment));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Runnable runnableCounter= new Runnable() {
                    @Override
                    public void run() {

                        for (int i=time;i>=0;i--){
                            Message message = Message.obtain();
                            message.arg1 = i;
                            handlerCounter.sendMessage(message);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            new Thread(runnableBackground).start();
                        }
                    }
                };


                new Thread(runnableCounter).start();
                Log.d("tag","onclick");
                //reset code
                swapBackground(false);
                height=0;
            }

        });

    }
    void init(){

        relativeLayoutBackground = (RelativeLayout)findViewById(R.id.rl_background);
        relativeLayoutForeground = (RelativeLayout)findViewById(R.id.rl_foreground);
        button =(Button)findViewById(R.id.button);
        textViewCounter = (TextView)findViewById(R.id.tv_counter);
        textViewCounter.setText(String.valueOf(time));
        relativeLayoutBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    relativeLayoutBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    //noinspection deprecation
                    relativeLayoutBackground.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                maxHeight = relativeLayoutBackground.getHeight();
                increment = dpToPx((int)maxHeight)/time;
                height=0;
            }
        });
    }
    void swapBackground(Boolean on){
        if(on){
            relativeLayoutBackground.setBackgroundColor(Color.parseColor("#FC6D6D"));
            relativeLayoutForeground.setBackgroundColor(Color.parseColor("#353F4F"));
            button.setVisibility(View.INVISIBLE);
        }
        else{
            button.setVisibility(View.VISIBLE);
        }
    }
    void refresh(){
            int r =(int)(Math.random()*255);
            int g =(int)(Math.random()*255);
            int b =(int)(Math.random()*255);
        Log.d("color","r "+r+",g "+g+",b "+b);
            relativeLayoutBackground.setBackgroundColor(Color.rgb(r,g,b));
    }
    void counterRefresh(int count){
        textViewCounter.setText(""+count);
    }
    void backgroundRefresh(){
        height ++;
        Log.d("height","height:"+height+" Increment:"+increment);
        relativeLayoutForeground.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  pxToDp((int)height)));
    }
}
