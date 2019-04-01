package com.hie2j.okhttp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView txt_result;
    private ImageView img_result;

    private static final String TAG = "MainActivity";

    private static final int txt_code = 1001;
    private static final int img_code = 1002;
    private Handler handler;

    private String baiduUrl = "http://www.baidu.com";
    private String infoUrl = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx/getMobileCodeInfo";
    private String imgUrl = "http://jsxy.gdcp.cn/UploadFile/2/2019/3/19/2019319124832881.jpg";

    private OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_result = findViewById(R.id.txt_);
        img_result = findViewById(R.id.img_);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == txt_code){
                    txt_result.setText((CharSequence) msg.obj);
                    return true;
                }else if (msg.what == img_code){
                    img_result.setImageBitmap((Bitmap) msg.obj);
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        AsynGetText();
                        AsynGetImage();
                    }
                }).start();
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SyncGetText();
                    }
                }).start();
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GetInfo("13068555865");
                    }
                }).start();
            }
        });

        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PostInfo("13068555865");
                    }
                }).start();
            }
        });

    }

    private void PostInfo(String phone) {
        RequestBody requestBody = new FormBody.Builder()
                .add("mobileCode",phone)
                .add("userID","")
                .build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url(infoUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                Message msg = Message.obtain();
                msg.what = txt_code;
                msg.obj = content;
                handler.sendMessage(msg);
            }
        });

    }

    private void GetInfo(String phone) {
        String url = infoUrl.concat("?mobileCode=").concat(phone).concat("&userID=");
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                Message msg = Message.obtain();
                msg.what = txt_code;
                msg.obj = content;
                handler.sendMessage(msg);
            }
        });
    }

    private void SyncGetText() {
        try{
            Request request = new Request.Builder()
                    .url(baiduUrl)
                    .get()
                    .build();
            Response response = null;
            response = client.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                String content = response.body().string();
                Message msg = Message.obtain();
                msg.what = txt_code;
                msg.obj = content;
                handler.sendMessage(msg);

                Log.e(TAG, "response.code()==" + response.code());
                Log.e(TAG, "response.message()==" + response.message());
                Log.e(TAG, "res==" + response.body().string());
                //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void AsynGetImage() {
        Request request = new Request.Builder()
                .get()
                .url(imgUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Message msg = Message.obtain();
                msg.what = img_code;
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        });
    }

    private void AsynGetText() {
        Request request = new Request.Builder()
                .get()
                .url(baiduUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                Message msg = Message.obtain();
                msg.what = txt_code;
                msg.obj = content;
                handler.sendMessage(msg);
            }
        });
    }
}
