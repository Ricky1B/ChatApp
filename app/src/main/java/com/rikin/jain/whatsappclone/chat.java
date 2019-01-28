package com.rikin.jain.whatsappclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class chat extends AppCompatActivity implements View.OnClickListener {
    private ListView chatListView;
    private ArrayList<String> chatList;
    private ArrayAdapter<String> arrayAdapter;
    private String selectedUser;

    public chat() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatListView = findViewById(R.id.chatListView);
        selectedUser = getIntent().getStringExtra("selectedUser");
        setTitle(selectedUser);
        FancyToast.makeText(this, "You can chat with " + selectedUser,FancyToast.LENGTH_SHORT, FancyToast.INFO,false).show();
        findViewById(R.id.btnSend).setOnClickListener(this);
        chatList = new ArrayList();
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,chatList);
        chatListView.setAdapter(arrayAdapter);
        try{
        ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
        ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

        firstUserChatQuery.whereEqualTo("waSender", ParseUser.getCurrentUser().getUsername());
        firstUserChatQuery.whereEqualTo("waTargetRecipient", selectedUser);

        secondUserChatQuery.whereEqualTo("waSender",selectedUser);
        secondUserChatQuery.whereEqualTo("waTargetRecipient", ParseUser.getCurrentUser().getUsername());

        ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
        allQueries.add(firstUserChatQuery);
        allQueries.add(secondUserChatQuery);

        ParseQuery<ParseObject> mQuery = ParseQuery.or(allQueries);
        mQuery.orderByAscending("createdAt");

        mQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(objects.size()>0 && e ==  null){
                    for(ParseObject chatObject: objects){
                        String waMessage = chatObject.get("waMessage") + "";
                        if(chatObject.get("waSender").equals(ParseUser.getCurrentUser().getUsername())){
                            waMessage = ParseUser.getCurrentUser().getUsername() +" : " + waMessage;
                        }
                        if(chatObject.get("waSender").equals(selectedUser)){
                            waMessage = selectedUser + " : " + waMessage;
                        }
                        chatList.add(waMessage);
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });} catch (Exception e){
            e.printStackTrace();
        }




    }

    @Override
    public void onClick(View v) {
        final EditText edtMessage = findViewById(R.id.edtMessage);
        ParseObject chat = new ParseObject("Chat");
        chat.put("waSender", ParseUser.getCurrentUser().getUsername());
        chat.put("waTargetRecipient", selectedUser);
        chat.put("waMessage", edtMessage.getText().toString());
        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    FancyToast.makeText(chat.this, "Message sent",FancyToast.LENGTH_SHORT, FancyToast.INFO,false).show();
                    chatList.add(ParseUser.getCurrentUser().getUsername() + ":" + edtMessage.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                    edtMessage.setText("");
                }
            }
        });

    }
}
