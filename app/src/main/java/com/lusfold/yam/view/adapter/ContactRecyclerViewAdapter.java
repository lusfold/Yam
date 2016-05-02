package com.lusfold.yam.view.adapter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.lusfold.yam.R;
import com.lusfold.yam.event.DeleteContactEvent;
import com.lusfold.yam.repository.bean.Contact;
import com.lusfold.yam.view.ContactEditActivity;
import com.lusfold.yam.view.widget.GlideCircleTransform;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by lusfold on 5/1/16.
 */
public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ItemViewHolder> {
    private List<Contact> data;
    private Fragment fragment;

    public ContactRecyclerViewAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setData(List<Contact> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Contact contact = data.get(position);
        Glide.with(fragment)
                .load(contact.getAvatarUrl())
                .placeholder(R.mipmap.default_avatar)
                .error(R.mipmap.default_avatar)
                .transform(new GlideCircleTransform(fragment.getContext()))
                .into(holder.avatar);
        holder.email.setText(contact.getEmail());
        holder.nickname.setText(contact.getNick());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar)
        ImageView avatar;
        @BindView(R.id.email)
        TextView email;
        @BindView(R.id.nickname)
        TextView nickname;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.main_layout)
        public void onClick(View view) {
            Intent intent = new Intent(fragment.getActivity(), ContactEditActivity.class);
            intent.putExtra(ContactEditActivity.KEY_CONTACT, (Serializable) data.get(getLayoutPosition()));
            fragment.startActivity(intent);
        }

        @OnLongClick(R.id.main_layout)
        public boolean onLongClick(View view) {
            new MaterialDialog.Builder(fragment.getActivity())
                    .backgroundColor(fragment.getActivity().getResources().getColor(R.color.white))
                    .itemsColorRes(R.color.colorPrimary)
                    .items(new String[]{fragment.getActivity().getResources().getString(R.string.delete)})
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            EventBus.getDefault().post(new DeleteContactEvent(data.get(getLayoutPosition())));
                        }
                    })
                    .show();
            return true;
        }
    }
}
