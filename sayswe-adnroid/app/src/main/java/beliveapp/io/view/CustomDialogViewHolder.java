package beliveapp.io.view;

import android.view.View;

import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import beliveapp.io.R;
import beliveapp.io.common.data.model.Dialog;
import beliveapp.io.common.data.model.User;


public class CustomDialogViewHolder extends DialogsListAdapter.DialogViewHolder<Dialog> {
    private View onlineView;

    public CustomDialogViewHolder(View itemView) {
        super(itemView);
        onlineView = itemView.findViewById(R.id.onlineIndicator);
    }

    @Override
    public void onBind(Dialog dialog) {
        super.onBind(dialog);
        //if not a group dialog, hide indicator
        if (dialog.getUsers().size() > 1) {
            onlineView.setVisibility(View.GONE);
        } else {
            //if not a group dialog, hide indicator
            boolean isOnline = ((User)dialog.getUsers().get(0)).isOnline();
            onlineView.setVisibility(View.VISIBLE);
            if (isOnline) {
                onlineView.setBackgroundResource(R.drawable.shape_bubble_online);
            } else {
                onlineView.setBackgroundResource(R.drawable.shape_bubble_offline);
            }
        }
    }
}
