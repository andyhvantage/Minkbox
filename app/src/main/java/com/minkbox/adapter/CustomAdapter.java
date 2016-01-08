package com.minkbox.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minkbox.R;
import com.minkbox.model.ChatBean;
import com.minkbox.utils.AppPreferences;

import java.util.ArrayList;

/**
 * Created by MMFA-YOGESH on 9/12/2015.
 */
public class CustomAdapter extends ArrayAdapter<ChatBean> {

    public Resources res;
    int row;
//    LayoutInflater inflater;
    private Context context;
    private ArrayList<ChatBean> data;
    String date="";
    /*************
     * CustomAdapter Constructor
     *****************/
    public CustomAdapter(
            Context context,
            int textViewResourceId,
            ArrayList<ChatBean> objects,
            Resources resLocal
    ) {
        super(context, textViewResourceId, objects);

        /********** Take passed values **********/
        this.context = context;
        this.row = textViewResourceId;
        data = objects;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () **********************/
    }

    public void updateResults(ArrayList<ChatBean> chatBeanObject) {
        this.data = chatBeanObject;
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        final ViewHolder holder;


        /***** Get each Model object from Arraylist ********/

        if(v == null)
        {
            /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = inflater.inflate(this.row, parent, false);
            holder = new ViewHolder();

            holder.relativeLayout = (LinearLayout) v.findViewById(R.id.rl);
            holder.UserMessage = (TextView) v.findViewById(R.id.imei);
            holder.dateT = (TextView) v.findViewById(R.id.minkbox_chat_spinner_rows_date_textview);

            v.setTag(holder);
        }
        else{
            holder = (ViewHolder) v.getTag();
        }

        final ChatBean tempValues = data.get(position);
//        if(tempValues.getDateTime()!=null){
//            String dateTime = tempValues.getDateTime();
//            StringTokenizer tk = new StringTokenizer(dateTime);
//            date = tk.nextToken();
//        }else{
//            date ="";
//        }
//        Log.d("dateTime", date+ "appp: "+AppPreferences.getDate(context));

//        if(position == 0){
//            dateT.setVisibility(View.VISIBLE);
//            dateT.setText(date);
//            AppPreferences.setDate(context, date);
//        }else{
//            if(!AppPreferences.getDate(context).equalsIgnoreCase(date)){
//                dateT.setVisibility(View.GONE);
//                dateT.setText(date);
//                AppPreferences.setDate(context, date);
//
//            }
//        }

        holder.UserMessage.setText(tempValues.getMessage());
        if (tempValues.getSendStatus().equalsIgnoreCase("true")) {
            holder.UserMessage
                    .setBackgroundResource(R.drawable.speech_bubble_green);
            holder.relativeLayout.setGravity(Gravity.RIGHT);
        } else {
            holder.UserMessage
                    .setBackgroundResource(R.drawable.speech_bubble_orange);
            holder.relativeLayout.setGravity(Gravity.LEFT);
        }

        holder.dateT.setText("" + tempValues.getDateTime()+" "+tempValues.getTime());


        System.out.println(" boolean flag value is : "+!AppPreferences.getDate(context).equalsIgnoreCase(date));
//        if(!AppPreferences.getDate(context).equalsIgnoreCase(date)){
//
//            System.out.println("------------------ date not matched ------------------ date is : "+date+" pos : "+position);
////
////            holder.dateT.setText("date is : "+date);
////         //   Log.d("dateTime1", AppPreferences.getDate(context));
//            AppPreferences.setDate(context, date);
////            holder.dateT.setVisibility(View.VISIBLE);
////            holder.dateT.setText("" + date);
//
//
//            //   Log.d("dateTime11", AppPreferences.getDate(context));
//        }else{
//
////            holder.dateT.setVisibility(View.GONE);
//            //      AppPreferences.setDate(context, date);
//            //      Log.d("dateTime22",AppPreferences.getDate(context));
//
//            System.out.println("------------------ date  matched ------------------ pos : "+position);
//            holder.dateT.setText("");
//
//        }



        return v;
    }

    class ViewHolder {
        LinearLayout relativeLayout;
        TextView UserMessage;
        TextView dateT;
    }


}