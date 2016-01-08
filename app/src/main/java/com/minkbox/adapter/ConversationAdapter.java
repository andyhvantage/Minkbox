package com.minkbox.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.minkbox.R;
import com.minkbox.lazyload.ImageLoader;
import com.minkbox.model.ChatBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by mmf-su-yash on 9/11/2015.
 */
public class ConversationAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public List<ChatBean> data;
    private Activity activity;
    ImageLoader imageLoader;

    public ConversationAdapter(Activity a, List<ChatBean> d) {
        this.activity = a;
        data = d;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader= new ImageLoader(activity);
    }

    public void updateResults(List<ChatBean> d) {
        data = d;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    public ChatBean getChatItem(int position) {
        ChatBean chatBean = data.get(position);
        return chatBean;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = null;
        convertView = null;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.conversation_item, null);
            final ViewHolder holder = new ViewHolder();
            try {
                holder.name = (TextView) vi.findViewById(R.id.username);
                holder.productname = (TextView) vi.findViewById(R.id.productname);
                holder.message = (TextView) vi.findViewById(R.id.message);
                holder.date = (TextView) vi.findViewById(R.id.date);
                holder.user_profile_image = (ImageView)vi.findViewById(R.id.user_profile_image);
                ChatBean item;
                item = data.get(position);


                holder.name.setText(item.getUserName());
                holder.productname.setText(item.getProductName());
                holder.message.setText(item.getMessage());
                Log.d("image: ", item.getProfileImage());
                imageLoader.DisplayImage(item.getProfileImage(), holder.user_profile_image);

               /* Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd");
                String formattedDate = df.format(c.getTime());
                System.out.println("Current time => " + c.getTime() + "   :  " + formattedDate+" : getTime from server"+ item.getDateTime());

                StringTokenizer tk = new StringTokenizer(item.getDateTime());
                String tkdate = tk.nextToken();
                SimpleDateFormat dateFormatP = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                Date date;
                String C_date = "";
                try {
                    date = dateFormatP.parse(tkdate);
                    C_date = dateFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                /*String time = tk.nextToken();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdfs = new SimpleDateFormat("HH:mm a");
                Date dt;
                try {
                    if (formattedDate.equalsIgnoreCase(C_date)) {
                        dt = sdf.parse(time);
                        System.out.println("Time Display: " + sdfs.format(dt)); // <-- I got result here
                        holder.date.setText(sdfs.format(dt));
                    } else {
                        dt = sdf.parse(time);
                        System.out.println("Time Display: " + sdfs.format(dt) + " Date: " + C_date); // <-- I got result here
                        holder.date.setText(C_date + " " + sdfs.format(dt));
                    }

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/

                vi.setTag(holder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


   //     }
        return vi;
    }

    public static class ViewHolder {
        public TextView name, productname, message, date;
        public ImageView user_profile_image;
        public RelativeLayout conver_rl;
    }

}
