package com.example.goddamit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.provider.MediaStore;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class Screen2 extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private RecyclerView chatsRV;
    private ImageButton sendMsgIB;
    private EditText userMsgEdt;
    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";
    // creating a variable for
    // our volley request queue.
    private RequestQueue mRequestQueue;

    // creating a variable for array list and adapter class.
    private ArrayList<MessageModel> messageModalArrayList;
    private MessageRVAdaptor messageRVAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen2);

        ImageButton openCameraButton = findViewById(R.id.CameraButton);
        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // on below line we are initializing all our views.
        chatsRV = findViewById(R.id.charRecyclerView);
        sendMsgIB = findViewById(R.id.sendButton);
        userMsgEdt = findViewById(R.id.messageBox);

        // below line is to initialize our request queue.
        mRequestQueue = Volley.newRequestQueue(Screen2.this);
        mRequestQueue.getCache().clear();

        // creating a new array list
        messageModalArrayList = new ArrayList<>();

        // adding on click listener for send message button.
        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking if the message entered
                // by user is empty or not.
                if (userMsgEdt.getText().toString().isEmpty()) {
                    // if the edit text is empty display a toast message.
                    Toast.makeText(Screen2.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // calling a method to send message
                // to our bot to get response.
                sendMessage(userMsgEdt.getText().toString());

                // below line we are setting text in our edit text as empty
                userMsgEdt.setText("");
            }
        });


    // on below line we are initializing our adapter class and passing our array list to it.
    messageRVAdapter = new MessageRVAdaptor(messageModalArrayList, this);

    // below line we are creating a variable for our linear layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Screen2.this, RecyclerView.VERTICAL, false);

    // below line is to set layout
    // manager to our recycler view.
        chatsRV.setLayoutManager(linearLayoutManager);

    // below line we are setting
    // adapter to our recycler view.
        chatsRV.setAdapter(messageRVAdapter);
}

    private void sendMessage(String userMsg) {
        // below line is to pass message to our
        // array list which is entered by the user.
        MessageModel messageModel = new MessageModel();
        messageModel.setMessage(userMsg);
        messageModel.setSender(USER_KEY);
        messageModalArrayList.add(messageModel);
        messageRVAdapter.notifyDataSetChanged();

        // url for our brain
        // make sure to add mshape for uid.
        // make sure to add your url.
        String url = "http://api.brainshop.ai/get?bid=176544&key=HegrA32wqcIL6IAW&uid=[uid]&msg=[msg]" + userMsg;

        // creating a variable for our request queue.
        RequestQueue queue = Volley.newRequestQueue(Screen2.this);

        // on below line we are making a json object request for a get request and passing our url .
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // in on response method we are extracting data
                    // from json response and adding this response to our array list.
                    String botResponse = response.getString("cnt");
                    MessageModel messageModel = new MessageModel();
                    messageModel.setMessage(botResponse);
                    messageModel.setSender(BOT_KEY);
                    messageModalArrayList.add(messageModel);

                    // notifying our adapter as data changed.
                    messageRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                    // handling error response from bot.
                    MessageModel messageModel = new MessageModel();
                    messageModel.setMessage("No response");
                    messageModel.setSender(BOT_KEY);
                    messageModalArrayList.add(messageModel);
                    messageRVAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error handling.
                MessageModel messageModel = new MessageModel();
                messageModel.setMessage("Sorry no response found");
                messageModel.setSender(BOT_KEY);
                messageModalArrayList.add(messageModel);
                Toast.makeText(Screen2.this, "No response from the bot..", Toast.LENGTH_SHORT).show();
            }
        });

        // at last adding json object
        // request to our queue.
        queue.add(jsonObjectRequest);
    }

    private void openImagePicker() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (pickIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickIntent, REQUEST_IMAGE_PICK);
        }
    }

    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                // Image picked from gallery
                // You can handle the image data here.
                // For example, you can display the image in an ImageView or save it to storage.
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Image captured from the camera
                // You can handle the image data here.
                // For example, you can display the image in an ImageView or save it to storage.
            }
        }
    }
}