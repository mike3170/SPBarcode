package com.stit.jhbarcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.stit.jhbarcode.model.ApiResponse;
import com.stit.jhbarcode.model.BasLoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LocSelectionActivity extends AppCompatActivity {
    ArrayList<String> locList = new ArrayList<String>();
    ListView listView;

    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_selection);
        this.btnCancel = this.findViewById(R.id.btnCancel);
//----------------
        String locUrl = MyInfo.JH_API_URL + "getLocList";
        ApiResponse<List<BasLoc>> locApiResponse = null;
        try {
            locApiResponse = Ion.with(LocSelectionActivity.this)
                    .load(locUrl)
                    .as(new TypeToken<ApiResponse<List<BasLoc>>>() {})
                    .get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<BasLoc> basLocList  = locApiResponse.data;

        for (BasLoc loc: basLocList){
            locList.add(loc.getLocNo());
        }
        listView = findViewById(R.id.locSelectionLv);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                locList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onClickListView);

        this.btnCancel.setOnClickListener(view -> {
            Bundle extra = getIntent().getExtras();
            Intent intent = new Intent(LocSelectionActivity.this, LocationMoveActivity.class);
            if (extra != null) {
                intent.putExtra("loc", extra.getString("loc"));
                intent.putExtra("jobNo", extra.getString("jobNo"));
                intent.putExtra("procNo", extra.getString("procNo"));
            }
//            startActivity(intent);

            setResult(1, intent);
            finish();
        });
    }

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle extra = getIntent().getExtras();
//            -------
            String loc = locList.get(position);
            Intent intent = new Intent(LocSelectionActivity.this, LocationMoveActivity.class);
            intent.putExtra("loc",loc);
            if (extra != null) {
                intent.putExtra("jobNo", extra.getString("jobNo"));
            }
            if (extra.getString("procNo") != null) {
                intent.putExtra("procNo", extra.getString("procNo"));
            }
//            startActivity(intent);

            setResult(1, intent);
            finish();
        }
    };


}