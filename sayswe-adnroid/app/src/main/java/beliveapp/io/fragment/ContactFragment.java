package beliveapp.io.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import beliveapp.io.MainActivity;
import beliveapp.io.R;
import beliveapp.io.common.data.fixtures.DialogsFixtures;
import beliveapp.io.common.data.model.Message;
import beliveapp.io.common.data.model.Phone;
import beliveapp.io.common.data.model.User;
import beliveapp.io.features.def.DefaultMessagesActivity;
import beliveapp.io.listener.OnFragmentInteractionListener;
import beliveapp.io.utils.AppUtils;
import beliveapp.io.view.CustomDialogViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

import beliveapp.io.common.data.model.Dialog;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {

    @BindView(R.id.contactList)
    DialogsList contactList;

    @BindView(R.id.btnSyncContact)
    Button btnSyncContact;

    private String TAG = "Contact";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageLoader imageLoader;
    private DialogsListAdapter<Dialog> adapter;

    private List<Phone> phoneList = new ArrayList<Phone>();

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this, view);

        initAdapter();

        return view;
    }

    @OnClick(R.id.btnSyncContact)
    void submitButton(View view) {
        if (view.getId() == R.id.btnSyncContact) {

            Toast.makeText(getActivity(), "Sync Contact", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void onNewMessage(String dialogId, IMessage message) {
        if (!adapter.updateDialogWithMessage(dialogId, message)) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or reload all dialogs list
        }
    }

    private void initAdapter() {

        phoneList = ((MainActivity) getActivity()).getPhoneList();

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);
            }
        };

        adapter = new DialogsListAdapter<>(R.layout.item_custom_dialog_view_holder,
                CustomDialogViewHolder.class,
                imageLoader);

        List<Dialog> contacts = new ArrayList<>();

        Date date = new Date();

        for (int i = 0; i < phoneList.size(); i++) {
            Phone phone = phoneList.get(i);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -(i * i));
            calendar.add(Calendar.MINUTE, -(i * i));

            User user = new User(
                    phone.getNumber(),
                    phone.getName(),
                    DialogsFixtures.getRandomAvatar(),
                    DialogsFixtures.getRandomBoolean());

            ArrayList<User> users = new ArrayList<User>();
            users.add(user);

            Message message = new Message(
                    phone.getNumber(),
                    user,
                    phone.getNumber(),
                    calendar.getTime());

            Dialog dialog = new Dialog(
                    phone.getNumber(),
                    phone.getName(),
                    DialogsFixtures.getRandomAvatar(),
                    users,
                    message,
                    i < 3 ? 3 - i : 0);

            contacts.add(dialog);
        }
        adapter.setItems(contacts);

        adapter.setOnDialogClickListener(this);
        adapter.setOnDialogLongClickListener(this);

        contactList.setAdapter(adapter);

        ref.child("users").child("+84977555115").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.d(TAG, "ContactFragment " + dataSnapshot.getValue().toString());
                    // use "username" already exists
                    // Let the user know he needs to pick another username.
                } else {
                    Log.d(TAG, "ContactFragment dataSnapshot NOT exists");
                    // User does not exist. NOW call createUserWithEmailAndPassword
                    // Your previous code here.
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "ContactFragment onCancelled databaseError " + databaseError.toString());

            }
        });
    }

    //for example
    private void onNewMessage(String dialogId, Message message) {
        boolean isUpdated = adapter.updateDialogWithMessage(dialogId, message);
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
        }
    }

    //for example
    private void onNewDialog(Dialog dialog) {
        adapter.addItem(dialog);
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {
        AppUtils.showToast(
                getActivity(),
                dialog.getDialogName(),
                false);
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        String id = dialog.getId();
        DefaultMessagesActivity.open(getActivity());
    }
}
