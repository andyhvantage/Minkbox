package com.minkbox;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.minkbox.adapter.ConversationAdapter;
import com.minkbox.databace.ChatDataBaseHelper;
import com.minkbox.model.ChatBean;
import com.minkbox.utils.AppPreferences;
import com.minkbox.utils.Function;
import com.minkbox.utils.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Conversations extends AppCompatActivity {

    Toolbar toolbar;
    ListView conversationListView;
    public static ConversationAdapter conversationAdapter;
    public List<ChatBean> item;
    TextView blank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        conversationAdapter = null;
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.title_activity_conversations);
        getSupportActionBar().setTitle(title);

        blank = (TextView) findViewById(R.id.blank);

        conversationListView = (ListView)findViewById(R.id.conversation_lv);

        item = new ArrayList<ChatBean>();

        conversationAdapter = new ConversationAdapter(Conversations.this, item);
        conversationListView.setAdapter(conversationAdapter);


        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ChatBean finalItem = conversationAdapter.getChatItem(position);

                Intent intent = new Intent(Conversations.this, ChatActivity.class);
                intent.setAction("converstion");
                intent.putExtra("productId", finalItem.getProductId());
                intent.putExtra("productName", finalItem.getProductName());
                intent.putExtra("productPrice", finalItem.getProductPrice());
                intent.putExtra("message", finalItem.getMessage());
                intent.putExtra("reciverId", finalItem.getReciverId());
                intent.putExtra("reciver_name", finalItem.getUserName());
                intent.putExtra("productOwnerName", finalItem.getProductOwnerId());
                intent.putExtra("ProductImage", finalItem.getProductImage());
                intent.putExtra("ProfileImage", finalItem.getProfileImage());
                startActivity(intent);
            }
        });




        ChatDataBaseHelper.init(Conversations.this);
        List<ChatBean> userData = ChatDataBaseHelper.getAllUserData(String.valueOf(AppPreferences.getCustomerId(Conversations.this)));
        List<String> id = new ArrayList<String>();
        List<String> recieverId = new ArrayList<String>();



        for(int i=0; i<userData.size(); i++){

            System.out.println("size" + userData.size()+AppPreferences.getCustomerId(Conversations.this));
            System.out.println("sender id, reciever id, product id" + userData.get(i).getSenderId()+" , "+ userData.get(i).getReciverId()+" , "+ userData.get(i).getProductId());

            if(id.contains(userData.get(i).getProductId()) && recieverId.contains(userData.get(i).getReciverId())) {
                System.out.println("sender id1, reciever id1, product id1" + userData.get(i).getSenderId()+" , "+ userData.get(i).getReciverId() + " , "+ userData.get(i).getProductId());
              continue;
            }else{
                ChatBean chatBean = new ChatBean();
                chatBean.setUserName(userData.get(i).getUserName());
                chatBean.setMessage(userData.get(i).getMessage());
                chatBean.setProductName(userData.get(i).getProductName());
                chatBean.setProductPrice(userData.get(i).getProductPrice());
                chatBean.setReciverId(userData.get(i).getReciverId());
                chatBean.setProductId(userData.get(i).getProductId());
                chatBean.setProductOwnerId(userData.get(i).getProductOwnerId());
                chatBean.setProductImage(userData.get(i).getProductImage());
                chatBean.setProfileImage(userData.get(i).getProfileImage());
                chatBean.setSenderId(userData.get(i).getSenderId());
                chatBean.setDateTime(userData.get(i).getDateTime());
                item.add(chatBean);
                id.add(userData.get(i).getProductId());
                recieverId.add(userData.get(i).getReciverId());

                System.out.println("sender id2, reciever id2, product id2" + userData.get(i).getSenderId() + " , " + userData.get(i).getReciverId() + " , " + userData.get(i).getProductId());

            }
        }
        conversationAdapter.notifyDataSetChanged();


        if(item.isEmpty()){
            blank.setVisibility(View.VISIBLE);
        }else{
            blank.setVisibility(View.GONE);
        }

        Function.cancelNotification(Conversations.this, 0);

    }

//    public static void nofifyDatasetChange(){
//     //   Conversations.nofifyDatasetChange();
//        conversationAdapter.updateResults(new ArrayList<ChatBean>());
//    }

   /* class FeatchConversations extends AsyncTask<String, Void, String>{

        String customerId;

        public FeatchConversations(String customerId){
        this.customerId = customerId;
        }
        @Override
        protected String doInBackground(String... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> values = new ArrayList<>();
            values.add(new BasicNameValuePair("customer_id", customerId));

                String json = serviceHandler.makeServiceCall(params[0], ServiceHandler.POST, values);
            if(json != null){
                Log.d("chat response", json);
                try {
                    JSONObject object = new JSONObject(json);
                    if(object.getString("status").equalsIgnoreCase("200")){
                        JSONArray jsonArray = object.getJSONArray("result");
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            ChatBean chatBean = new ChatBean();
                            chatBean.setUserName(jsonObj.getString("reciver_name"));
                            chatBean.setProductName(jsonObj.getString("productName"));
                            chatBean.setProductId(jsonObj.getString("product_Id"));
                            chatBean.setReciverId(jsonObj.getString("reciverId"));
                            chatBean.setSenderId(jsonObj.getString("sender_id"));

                           // jsonObj.getString("message");

                            chatBean.setMessage(jsonObj.getString("message"));
                            item.add(chatBean);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            conversationAdapter.notifyDataSetChanged();
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<ChatBean> userData = ChatDataBaseHelper.getAllUserData(String.valueOf(AppPreferences.getCustomerId(Conversations.this)));
//        conversationAdapter.updateResults(userData);

        item.clear();
        List<String> id = new ArrayList<String>();
        List<String> recieverId = new ArrayList<String>();
        for (int i = 0; i < userData.size(); i++) {
            if (id.contains(userData.get(i).getProductId()) && recieverId.contains(userData.get(i).getReciverId())) {
                continue;
            } else {
                ChatBean chatBean = new ChatBean();
                chatBean.setUserName(userData.get(i).getUserName());
                chatBean.setMessage(userData.get(i).getMessage());
                chatBean.setProductName(userData.get(i).getProductName());
                chatBean.setProductPrice(userData.get(i).getProductPrice());
                chatBean.setReciverId(userData.get(i).getReciverId());
                chatBean.setProductId(userData.get(i).getProductId());
                chatBean.setProductOwnerId(userData.get(i).getProductOwnerId());
                chatBean.setProductImage(userData.get(i).getProductImage());
                chatBean.setProfileImage(userData.get(i).getProfileImage());
                chatBean.setSenderId(userData.get(i).getSenderId());
                chatBean.setDateTime(userData.get(i).getDateTime());
                item.add(chatBean);
                id.add(userData.get(i).getProductId());
                recieverId.add(userData.get(i).getReciverId());
            }
        }
        conversationAdapter.notifyDataSetChanged();
    }
}
