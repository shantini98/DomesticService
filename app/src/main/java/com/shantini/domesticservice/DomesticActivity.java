package com.shantini.domesticservice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DomesticActivity extends AppCompatActivity {
    TextView tvrname,tvrphone,tvraddress,tvrloc;
    ImageView imgDome;
    ListView lvservice;
    Dialog myDialogWindow;
    ArrayList<HashMap<String, String>> servicelist;
    String userid,domeid,userphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domestic);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        domeid = bundle.getString("domesticid");
        String rname = bundle.getString("service");
        String rphone = bundle.getString("name");
        String raddress = bundle.getString("phone");
        String rlocation = bundle.getString("mainservice");
        userid = bundle.getString("userid");
        userphone = bundle.getString("userphone");
        initView();
        tvrname.setText(rname);
        tvraddress.setText(raddress);
        tvrphone.setText(rphone);
        tvrloc.setText(rlocation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Picasso.with(this).load("https://shantini123.000webhostapp.com/images/"+domeid+".jpg")
                .fit().into(imgDome);
        //  .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)

        lvservice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showServiceDetail(position);
            }
        });
        loadServices(domeid);

    }

    private void showServiceDetail(int p) {
        myDialogWindow = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        myDialogWindow.setContentView(R.layout.dialog_window);
        myDialogWindow.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tvfname,tvfprice,tvfquan;
        final ImageView imgservice = myDialogWindow.findViewById(R.id.imageViewFood);
        final Spinner spquan = myDialogWindow.findViewById(R.id.spinner2);
        Button btnorder = myDialogWindow.findViewById(R.id.button2);
        final ImageButton btnfb = myDialogWindow.findViewById(R.id.btnfacebook);
        tvfname= myDialogWindow.findViewById(R.id.textView12);
        tvfprice = myDialogWindow.findViewById(R.id.textView13);
        tvfquan = myDialogWindow.findViewById(R.id.textView14);
        tvfname.setText(servicelist.get(p).get("servicename"));
        tvfprice.setText(servicelist.get(p).get("serviceprice"));
        tvfquan.setText(servicelist.get(p).get("servicehours"));
        final String serviceid =(servicelist.get(p).get("serviceid"));
        final String servicename = servicelist.get(p).get("servicename");
        final String serviceprice = servicelist.get(p).get("serviceprice");
        btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fquan = spquan.getSelectedItem().toString();
                dialogOrder(serviceid,servicename,fquan,serviceprice);
            }
        });

        btnfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image = ((BitmapDrawable)imgservice.getDrawable()).getBitmap();
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                ShareDialog shareDialog = new ShareDialog(DomesticActivity.this);
                shareDialog.show(content);
            }
        });

        int quan = Integer.parseInt(servicelist.get(p).get("servicehours"));
        List<String> list = new ArrayList<String>();
        for (int i = 1; i<=quan;i++){
            list.add(""+i);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spquan.setAdapter(dataAdapter);

        Picasso.with(this).load("https://shantini123.000webhostapp.com/serviceimages/"+serviceid+".jpg")
                .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                .fit().into(imgservice);
        myDialogWindow.show();
    }

    private void dialogOrder(final String serviceid, final String servicename, final String fquan, final String serviceprice) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Book "+servicename+ " with hours "+fquan);

        alertDialogBuilder
                .setMessage("Are you sure")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        insertCart(serviceid,servicename,fquan,serviceprice);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void insertCart(final String serviceid, final String servicename, final String fquan, final String serviceprice) {
        class InsertCart extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("serviceid",serviceid);
                hashMap.put("domesticid",domeid);
                hashMap.put("servicename",servicename);
                hashMap.put("hours",fquan);
                hashMap.put("serviceprice",serviceprice);
                hashMap.put("userid",userid);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("https://shantini123.000webhostapp.com/insert_cart.php",hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Toast.makeText(DomesticActivity.this,s, Toast.LENGTH_SHORT).show();
                if (s.equalsIgnoreCase("success")){
                    Toast.makeText(DomesticActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    myDialogWindow.dismiss();
                    loadServices(domeid);
                }else{
                    Toast.makeText(DomesticActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }
        InsertCart insertCart = new InsertCart();
        insertCart.execute();
    }

    private void loadServices(final String domeid) {
        class LoadService extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("domesticid",domeid);
                RequestHandler requestHandler = new RequestHandler();
                String s = requestHandler.sendPostRequest("https://shantini123.000webhostapp.com/load_services.php",hashMap);
                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                servicelist.clear();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray servicearray = jsonObject.getJSONArray("service");
                    for (int i = 0; i < servicearray.length(); i++) {
                        JSONObject c = servicearray.getJSONObject(i);
                        String jsid = c.getString("serviceid");
                        String jsfname = c.getString("servicename");
                        String jsfprice = c.getString("serviceprice");
                        String jsquan = c.getString("hours");
                        HashMap<String,String> servicelisthash = new HashMap<>();
                        servicelisthash.put("serviceid",jsid);
                        servicelisthash.put("servicename",jsfname);
                        servicelisthash.put("serviceprice",jsfprice);
                        servicelisthash.put("servicehours",jsquan);
                        servicelist.add(servicelisthash);
                    }
                }catch(JSONException e){}
                ListAdapter adapter = new CustomAdapterService(
                        DomesticActivity.this, servicelist,
                        R.layout.service_list_dome, new String[]
                        {"servicename","serviceprice","servicehours"}, new int[]
                        {R.id.textView,R.id.textView2,R.id.textView3});
                lvservice.setAdapter(adapter);

            }
        }
        LoadService loadService = new LoadService();
        loadService.execute();
    }

    private void initView() {
        imgDome = findViewById(R.id.imageView3);
        tvrname = findViewById(R.id.textView6);
        tvrphone = findViewById(R.id.textView7);
        tvraddress = findViewById(R.id.textView8);
        tvrloc = findViewById(R.id.textView9);
        lvservice = findViewById(R.id.listviewfood);
        servicelist = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(DomesticActivity.this,MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userid",userid);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
