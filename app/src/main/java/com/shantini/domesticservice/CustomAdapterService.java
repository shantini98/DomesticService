package com.shantini.domesticservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapterService extends SimpleAdapter {

    private Context mContext;
    public LayoutInflater inflater=null;
    public CustomAdapterService(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        try{
            if(convertView==null)
                vi = inflater.inflate(R.layout.service_list_dome, null);
            HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
            TextView tvservicename = vi.findViewById(R.id.textView);
            TextView tvserviceprice = vi.findViewById(R.id.textView2);
            TextView tvquantity = vi.findViewById(R.id.textView3);
            CircleImageView imgservice =vi.findViewById(R.id.imageView2);
            String dfname = (String) data.get("servicename");//hilang
            String dserviceprice =(String) data.get("serviceprice");
            String dservicequan =(String) data.get("servicehours");
            String dfid=(String) data.get("serviceid");

            tvservicename.setText(dfname);
            tvserviceprice.setText(dserviceprice);
            tvquantity.setText(dservicequan);
            String image_url = "https://shantini123.000webhostapp.com/serviceimages/"+dfid+".jpg";
            Picasso.with(mContext).load(image_url)
                    .fit().into(imgservice);
//                    .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)

        }catch (IndexOutOfBoundsException e){

        }

        return vi;
    }
}