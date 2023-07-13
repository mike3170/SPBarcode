package com.stit.jhbarcode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.stit.jhbarcode.model.ApiResponse;
import com.stit.jhbarcode.model.BasLoc;
import com.stit.jhbarcode.model.ProcMast;
import com.stit.jhbarcode.repo.MyDao;

import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private MyDao myDao;
    private SharedPreferences mPreferences;
    private Context context;
    private Resources resources;
    private TextView moveMessageView;
    private TextView retnMessageView;
    private Button butMoveExec;
    private Button butRetnExec;
    private String defaultLocalString;
    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
        this.switchToolbarTitle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveMessageView = (TextView) findViewById(R.id.textView2);
        retnMessageView = (TextView) findViewById(R.id.textView3);
        butMoveExec = (Button) findViewById(R.id.btnLocationMove);
        butRetnExec = (Button) findViewById(R.id.btnLocQuery);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.myDao = new MyDao(this);
        mPreferences = getSharedPreferences(MyInfo.SPKey, Context.MODE_PRIVATE);

        this.defaultLocalString = Locale.getDefault().getCountry();
//        get loc and proc data

        // for loc list---------------------------
        boolean deleteLocOK = myDao.deleteBasLoc();
        if (! deleteLocOK) {
            showAlert("刪除 bas_loc 表格失敗!");
        }
        String locUrl = MyInfo.JH_API_URL + "getLocList";
        ApiResponse<List<BasLoc>> locApiResponse = null;
        try {
            locApiResponse = Ion.with(MainActivity.this)
                    .load(locUrl)
                    .as(new TypeToken<ApiResponse<List<BasLoc>>>() {})
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<BasLoc> basLocList  = locApiResponse.data;
        //for (CodMast cm : codMastList) {
        //  System.out.println(cm.toString());
        //}

        boolean locInsertOk = myDao.insertBasLoc(basLocList);
        System.out.println("insertok:"+locInsertOk);
        if (! locInsertOk) {
            showAlert("新增 庫位 表格失敗!");
        }

        boolean deleteProcOK = myDao.deleteProcMast();
        if (!deleteLocOK) {
            showAlert("刪除proc_mast表格失敗!");
        }

        String procUrl = MyInfo.JH_API_URL + "getProcList";
        ApiResponse<List<ProcMast>> procApiResponse = null;
        try {
            procApiResponse = Ion.with(MainActivity.this)
                    .load(procUrl)
                    .as(new TypeToken<ApiResponse<List<ProcMast>>>() {})
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<ProcMast> procMastList = procApiResponse.data;

        boolean procInsertOK = myDao.insertProcMast(procMastList);

        if (!procInsertOK) {
            showAlert("新增proc_mast表格失敗");
        }
        //  登出
        Button btnLogout = this.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener((view) -> {
            this.logout();
        });

        // 1.包裝廠庫位移轉
        Button btnLocationMove = this.findViewById(R.id.btnLocationMove);
        btnLocationMove.setOnClickListener((view) -> {
            Intent intent = new Intent(this, LocationMoveActivity.class);
            this.startActivity(intent);
        });

        // 2.線材廠線材廠庫位移轉
        Button btnWireCoilLocMove = this.findViewById(R.id.btnWireCoilLocMove);
        btnWireCoilLocMove.setOnClickListener((view) -> {
            Intent intent = new Intent(this, WireCoilLocMoveActivity.class);
            this.startActivity(intent);
        });

        // 3.成型廠線材廠庫位移轉
        Button btnHeadCoilLocMove = this.findViewById(R.id.btnLocQuery);
        btnHeadCoilLocMove.setOnClickListener((view) -> {
            Intent intent = new Intent(this, LocationQueryActivity.class);
            System.out.println("active:" + intent.getAction());
            this.startActivity(intent);
        });

        Button btnTest = this.findViewById(R.id.btnTest);
        btnTest.setVisibility(View.INVISIBLE);
        btnTest.setOnClickListener(v -> {
            //List<MainData> mainDataList = this.myDao.getMainDataList(Kind.LocationMove);
            //for (MainData d : mainDataList) {
            //    System.out.println(d);
            //    //System.out.println(d.getClassNo());
            //}
            //List<CodMast> list = this.myDao.getCodMastList();
            //System.out.println(list.size());
            //for (CodMast d : list) {
            //    System.out.println(d);
            //    //System.out.println(d.getClassNo());
            //}

            //List<MainData> mainDataList = this.myDao.getMainDataList(Kind.PicklingFinished);
            //for (MainData d : mainDataList) {
            //    System.out.println(d);
            //}

            //this.myDao.deleteMainData(Kind.PlatingReception);

            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);

            //Snackbar.make(getWindow().getDecorView(), "Hello mia", Snackbar.LENGTH_LONG)
            //        .setAction("David", null)
            //        .show();

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.lan_cht) {
//            Toast.makeText(this,"cht",Toast.LENGTH_SHORT).show();
//            context = LocaleHelper.setLocale(MainActivity.this, "en");
//            //resources = context.getResources();
//            //messageView.setText(resources.getString(R.string.app_name));
//            this.setLocale(this.defaultLocalString);
//            changeLangRefresh();
//        } else if (id == R.id.lan_ph) {
//            Toast.makeText(this,"phillipine",Toast.LENGTH_SHORT).show();
//            //context = LocaleHelper.setLocale(MainActivity.this, "ph");
//            //resources = context.getResources();
//            //messageView.setText(resources.getString(R.string.app_name));
//            this.setLocale("ph");
//            changeLangRefresh();
//        } else if (id == R.id.lan_id) {
//            Toast.makeText(this, "indonesia", Toast.LENGTH_SHORT).show();
//            //context = LocaleHelper.setLocale(MainActivity.this, "id");
//            //resources = context.getResources();
//            //messageView.setText(resources.getString(R.string.app_name));
//            System.out.println("hello indonesia");
//            this.setLocale("id");
//            changeLangRefresh();
//        }else if (id == R.id.lan_vn) {
//            Toast.makeText(this,"vn",Toast.LENGTH_SHORT).show();
//            //context = LocaleHelper.setLocale(MainActivity.this, "th");
//            //resources = context.getResources();
//            //messageView.setText(resources.getString(R.string.app_name));
//            this.setLocale("vn");
//            changeLangRefresh();
//        }
        if (id == R.id.action_logout) {
            this.logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        this.mPreferences.edit().remove("name").commit();
        this.finishAffinity();
    }

    void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Locale.setDefault(myLocale);
        onConfigurationChanged(conf);
        // this.showToast("switch to " + lang);
    }

    void switchToolbarTitle() {
        String lang = Locale.getDefault().getLanguage();
        System.out.println(lang);

        String appName = this.getResources().getString(R.string.app_name);
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        toolbar.setTitle(appName);
    }

    private void showAlert(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alertDialog_title))
                .setMessage(message)
                .setPositiveButton("關閉視窗", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();

        alertDialog.show();
    }

    void changeLangRefresh() {
        Locale locale = Locale.getDefault();
        System.out.println(locale.getLanguage());

        String appName = this.getResources().getString(R.string.app_name);
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        toolbar.setTitle(appName);
        System.out.println(appName);

        String packLabel  = this.getResources().getString(R.string.pack_title);
        this.moveMessageView.setText(packLabel);

        String headLabel = this.getResources().getString(R.string.head_coil_title);
        this.retnMessageView.setText(headLabel);

        String wireLabel = this.getResources().getString(R.string.wire_coil_title);
        this.retnMessageView.setText(wireLabel);

        String butExecLable = this.getResources().getString(R.string.execute);
        this.butMoveExec.setText(butExecLable);
        this.butRetnExec.setText(butExecLable);


    }


} // end class
