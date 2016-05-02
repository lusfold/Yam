package com.lusfold.yam.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lusfold.yam.R;
import com.lusfold.yam.event.UpdateMailDraftEvent;
import com.lusfold.yam.repository.CacheService;
import com.lusfold.yam.repository.bean.Mail;
import com.lusfold.yam.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

/**
 * Created by lusfold on 5/1/16.
 */
public class MailRecyclerViewAdapter extends RecyclerView.Adapter<MailRecyclerViewAdapter.ItemViewHolder> {
    private List<Mail> data;
    private Fragment fragment;

    public MailRecyclerViewAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setData(List<Mail> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mail, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Mail mail = data.get(position);
        holder.subject.setText(mail.getSubject());
        holder.recipient.setText(mail.getReciptients().get(0).getEmail());
        if (mail.getStatus() == Mail.DRAFT) {
            holder.state.setText("Status:" + StringUtils.getDateTime(mail.getTimeToSend()));
        } else if (mail.getStatus() == Mail.SENT) {
            holder.state.setText("Status:Sent");
        } else {
            holder.state.setText("Status:Fail");
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.subject)
        TextView subject;
        @BindView(R.id.recipient)
        TextView recipient;
        @BindView(R.id.state)
        TextView state;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnLongClick(R.id.main_layout)
        public boolean onLongClick(View view) {
            Mail mail = data.get(getLayoutPosition());
            if (mail.getStatus() == Mail.DRAFT) {
                new MaterialDialog.Builder(fragment.getActivity())
                        .backgroundColor(fragment.getActivity().getResources().getColor(R.color.white))
                        .itemsColorRes(R.color.colorPrimary)
                        .items(new String[]{fragment.getActivity().getResources().getString(R.string.cancle)})
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                CacheService.delete(data.get(getLayoutPosition()));
                                EventBus.getDefault().post(new UpdateMailDraftEvent());
                            }
                        })
                        .show();
            }
            return true;
        }
    }
}
