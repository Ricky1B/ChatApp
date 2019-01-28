package com.rikin.jain.whatsappclone;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Users extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView usersListView;
    private ArrayList<String> usersList;
    private ArrayAdapter<String> arrayAdapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        setTitle("Users");
        usersListView = findViewById(R.id.usersListView);
        usersListView.setOnItemClickListener(this);
        usersList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, usersList);
        swipeContainer = findViewById(R.id.swipeContainer);
        try{ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(objects.size() > 0 && e == null){
                    for(ParseUser user: objects){
                        usersList.add(user.getUsername());
                    }
                    usersListView.setAdapter(arrayAdapter);
                }
            }
        });} catch (Exception e){
            e.printStackTrace();
        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              try{
                  ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                  parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                  parseQuery.whereNotContainedIn("username", usersList);
                  parseQuery.findInBackground(new FindCallback<ParseUser>() {
                      @Override
                      public void done(List<ParseUser> objects, ParseException e) {
                          if(objects.size() >0){
                              if(e == null){
                                  for(ParseUser user: objects){
                                      usersList.add(user.getUsername());
                                  }
                                  arrayAdapter.notifyDataSetChanged();
                                  if(swipeContainer.isRefreshing()){
                                      swipeContainer.setRefreshing(false);
                                  }
                              }
                          } else{
                              if(swipeContainer.isRefreshing()){
                                  swipeContainer.setRefreshing(false);
                              }
                          }
                      }
                  });
              } catch (Exception e){
                  e.printStackTrace();
              }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if(item.getItemId() == R.id.logoutUserItem){
           final String username = ParseUser.getCurrentUser().getUsername();
           ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
               @Override
               public void done(ParseException e) {
                   if(e== null){
                       Toast.makeText(Users.this, username +" is logged out", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(Users.this, MainActivity.class);
                       startActivity(intent);
                       finish();
                   }
               }
           });
       }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Users.this,chat.class);
        intent.putExtra("selectedUser",usersList.get(position));
        startActivity(intent);
    }
}
