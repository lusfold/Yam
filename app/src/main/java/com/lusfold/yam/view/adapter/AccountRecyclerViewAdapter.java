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
import com.lusfold.yam.event.UpdateAccountEvent;
import com.lusfold.yam.repository.CacheService;
import com.lusfold.yam.repository.bean.Account;
import com.lusfold.yam.view.AccountEditActivity;
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
public class AccountRecyclerViewAdapter extends RecyclerView.Adapter<AccountRecyclerViewAdapter.ItemViewHolder> {
    private List<Account> data;
    private Fragment fragment;

    public AccountRecyclerViewAdapter(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setData(List<Account> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Account account = data.get(position);
        Glide.with(fragment)
                .load("")
                .placeholder(R.mipmap.default_avatar)
                .error(R.mipmap.default_avatar)
                .transform(new GlideCircleTransform(fragment.getContext()))
                .into(holder.avatar);
        holder.mail.setText(account.getAddress());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.avatar)
        ImageView avatar;
        @BindView(R.id.mail)
        TextView mail;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.main_layout)
        public void onClick(View view) {
            Intent intent = new Intent(fragment.getActivity(), AccountEditActivity.class);
            intent.putExtra(AccountEditActivity.KEY_ACCOUNT, (Serializable) data.get(getLayoutPosition()));
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
                            CacheService.delete(data.get(getLayoutPosition()));
                            EventBus.getDefault().post(new UpdateAccountEvent());
                        }
                    })
                    .show();
            return true;
        }
    }
}
