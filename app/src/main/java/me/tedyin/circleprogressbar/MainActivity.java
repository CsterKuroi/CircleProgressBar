package me.tedyin.circleprogressbar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import me.tedyin.circleprogressbarlib.CircleProgressBar;

//回款 时间 UID
public class MainActivity extends Activity {

    private CircleProgressBar  bar3;
    private static int current;
    private TextView t2;
    private TextView t4;
    private TextView t6;
    private final String wsuri = "ws://101.200.189.127:8001/ws";
    private final String wsuri2 = "ws://101.200.189.127:1234/ws";
    private String tempjson="";
    private String tempjson2="";
    private WebSocketConnection mConnection;
    private WebSocketConnection mConnection2;
    private String userID="101";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar3 = (CircleProgressBar) findViewById(R.id.bar3);
        t2 = (TextView) findViewById(R.id.textView2);
        t4 = (TextView) findViewById(R.id.textView4);
        t6 = (TextView) findViewById(R.id.textView6);
        double d4=Double.parseDouble(t4.getText().toString());
        double d6=Double.parseDouble(t6.getText().toString());
        if (d6>0) {
            int rate = (int) (d4 / d6 * 100);
            bar3.setProgress(rate);
        }
        findViewById(R.id.btn).setOnClickListener(new ClickL());
        down(downJson());
        down2(downJson2());
    }
    /**
     * 当月第一天
     * @return
     */
    private static String getFirstDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();

        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first).append(" 00:00:00");
        return str.toString();

    }

    /**
     * 当月最后一天
     * @return
     */
    private static String getLastDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        Date theDate = calendar.getTime();
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first).append(" 23:59:59");
        return str.toString();

    }

    public String downJson() {
        try {
            JSONObject object = new JSONObject();
            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            String start=getFirstDay();
            Date date1 = format.parse(start);
            String end=getLastDay();
            Date date2 = format.parse(end);

            object.put("cmd", "getContractMoney");
            object.put("type", "8");
            object.put("uid", "101");
            object.put("start",String.valueOf(date1.getTime() / 1000));
            object.put("end",String.valueOf(date2.getTime() / 1000));
            return object.toString();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void down(String json) {
        mConnection = new WebSocketConnection();
        if (mConnection.isConnected()) {
            mConnection.sendTextMessage(json);
            Log.d("test", "发送Json字段" + json);
        }else {
            try {
                tempjson=json;
                mConnection.connect(wsuri, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("test", "发送Json字段"+tempjson);
                        mConnection.sendTextMessage(tempjson);
                    }
                    @Override
                    public void onTextMessage(String payload) {
                        Log.d("test", "Got echo: " + payload);
                        try {
                            JSONObject object=null;
                            object = new JSONObject(payload);
                            String err = object.getString("error");
                            if (err.equals("1")){
                                if(!(object.getString("money").equals("null")))
                                    t4.setText(object.getString("money"));
                                double d4=Double.parseDouble(t4.getText().toString());
                                double d6=Double.parseDouble(t6.getText().toString());
                                if (d6>0) {
                                    int rate = (int) (d4 / d6 * 100);
                                    bar3.setProgress(rate);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("test", "Connection closed: " + reason);
                        if (code == 2) ;
                    }
                });
            }catch (WebSocketException e) {
                e.printStackTrace();
            }
        }
    }


    public String downJson2() {
        try {
            JSONObject object = new JSONObject();

            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
            String start=getFirstDay();
            Date date1 = format.parse(start);
            String end=getLastDay();
            Date date2 = format.parse(end);

            object.put("cmd", "getTaskMoney");
            object.put("userId", userID);
            object.put("startTime",String.valueOf(date1.getTime() / 1000));
            object.put("endTime",String.valueOf(date2.getTime() / 1000));
            return object.toString();
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void down2(String json) {
        mConnection2 = new WebSocketConnection();
        if (mConnection2.isConnected()) {
            mConnection2.sendTextMessage(json);
            Log.d("test222222", "发送Json字段" + json);
        }else {
            try {
                tempjson2=json;
                mConnection2.connect(wsuri2, new WebSocketHandler() {
                    @Override
                    public void onOpen() {
                        Log.e("test", "发送Json字段"+tempjson2);
                        mConnection2.sendTextMessage(tempjson2);
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        Log.d("test22222", "Got echo: " + payload);
                        try {
                            JSONObject object=null;
                            object = new JSONObject(payload);
                            String err = object.getString("error");
                            if (err.equals("0")){

                                if (err.equals("0")){
                                    if(!(object.getString("datas").equals("null")))
                                        t6.setText(object.getString("datas"));
                                    double d4=Double.parseDouble(t4.getText().toString());
                                    double d6=Double.parseDouble(t6.getText().toString());
                                    if (d6>0) {
                                        int rate = (int) (d4 / d6 * 100);
                                        bar3.setProgress(rate);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onClose(int code, String reason) {
                        Log.d("test", "Connection closed: " + reason);
                        if (code == 2) ;
                    }
                });
            }catch (WebSocketException e) {
                e.printStackTrace();
            }
        }
    }

    void delay() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ClickL implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            current = 0;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (current <= 100) {
                        current++;
                        bar3.setProgress(current);
                        delay();
                    }
                }
            }.start();
        }
    }
}
