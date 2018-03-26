package beliveapp.io.features.def;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Date;
import java.util.HashMap;

import beliveapp.io.R;
import beliveapp.io.common.data.fixtures.MessagesFixtures;
import beliveapp.io.common.data.model.Message;
import beliveapp.io.common.data.model.User;
import beliveapp.io.features.MessagesBaseActivity;
import beliveapp.io.utils.AppUtils;

public class DefaultMessagesActivity extends MessagesBaseActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener {

    public static void open(Context context) {
        context.startActivity(new Intent(context, DefaultMessagesActivity.class));
    }

    private MessagesList messagesList;

    private DatabaseReference receiveRef = database.getReference("bl_session_message/wRHk0PB4Vd");

    private DatabaseReference sendRef = database.getReference("bl_session_message/wRHk0PB4Vd");

    private String TAG = "Message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_messages);

        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);

        receiveRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String senderId = dataSnapshot.child("senderId").getValue(String.class);
                if(!senderId.equalsIgnoreCase(getUser().getUid())) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String content = dataSnapshot.child("content").getValue(String.class);
                    String img_url_large = dataSnapshot.child("img_url_large").getValue(String.class);
                    String type = dataSnapshot.child("type").getValue(String.class);
                    String objectId = dataSnapshot.child("objectId").getValue(String.class);
                    String updatedAt = dataSnapshot.child("updatedAt").getValue(String.class);
                    User sender = new User(
                            senderId,
                            name,
                            img_url_large,
                            true);
                    Message message = MessagesFixtures.getTextMessageUser(sender, content);
                    messagesAdapter.addToStart(message, true);
                } else {
                    Log.d(TAG, "onChildAdded: " + senderId);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public boolean onSubmit(CharSequence input) {

        Date date = new Date();
        String child = getUser().getUid() + "_" + date.getTime();

        HashMap<String, Object> message = new HashMap<>();
        message.put("content", input.toString());
        message.put("img_url_large", getUser().getPhotoUrl().toString());
        message.put("name", getUser().getDisplayName());
        message.put("senderId", getUser().getUid());
        message.put("type", "message");
        message.put("updatedAt", date.toString());

        sendRef.child(child).setValue(message);

        super.messagesAdapter.addToStart(
                MessagesFixtures.getTextMessageFirebase(getUser(), input.toString()), true);

        return true;
    }

    @Override
    public void onAddAttachments() {
        super.messagesAdapter.addToStart(
                MessagesFixtures.getImageMessage(), true);
    }

    private void initAdapter() {
        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, super.imageLoader);
        super.messagesAdapter.enableSelectionMode(this);
        super.messagesAdapter.setLoadMoreListener(this);
        super.messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        AppUtils.showToast(DefaultMessagesActivity.this,
                                message.getUser().getName() + " avatar click",
                                false);
                    }
                });
        this.messagesList.setAdapter(super.messagesAdapter);
    }
}
