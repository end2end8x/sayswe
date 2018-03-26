package beliveapp.io.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.Date;

import beliveapp.io.R;
import beliveapp.io.common.data.fixtures.DialogsFixtures;
import beliveapp.io.common.data.model.Message;
import beliveapp.io.features.def.DefaultMessagesActivity;
import beliveapp.io.listener.OnFragmentInteractionListener;
import beliveapp.io.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import beliveapp.io.common.data.model.Dialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {

    @BindView(R.id.dialogsList)
    DialogsList dialogsList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageLoader imageLoader;
    private DialogsListAdapter<Dialog> dialogsAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        initAdapter();

        return view;
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
        if (!dialogsAdapter.updateDialogWithMessage(dialogId, message)) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or reload all dialogs list
        }
    }

    private void initAdapter() {

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url) {
                Picasso.get().load(url).into(imageView);
            }
        };

        dialogsAdapter = new DialogsListAdapter<>(imageLoader);
        dialogsAdapter.setItems(DialogsFixtures.getDialogs());

        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(dialogsAdapter);
    }

    //for example
    private void onNewMessage(String dialogId, Message message) {
        boolean isUpdated = dialogsAdapter.updateDialogWithMessage(dialogId, message);
        if (!isUpdated) {
            //Dialog with this ID doesn't exist, so you can create new Dialog or update all dialogs list
        }
    }

    //for example
    private void onNewDialog(Dialog dialog) {
        dialogsAdapter.addItem(dialog);
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
        DefaultMessagesActivity.open(getActivity());
    }
}
