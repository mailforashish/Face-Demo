package com.zeeplive.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zeeplive.app.R;
import com.zeeplive.app.response.Chat.ChatList;
import com.zeeplive.app.response.UserListResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.ContactViewHolder> {
    private List<ChatList> chatListArrayList;
    private int rowLayout;
    private Context context;

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout contactLayout, rl_unread;
        ImageView img_profile;
        TextView tv_username, tv_tag, tv_unread, tv_time;

        public ContactViewHolder(View v) {
            super(v);
            contactLayout = (RelativeLayout) v.findViewById(R.id.contact_layout);
            rl_unread = (RelativeLayout) v.findViewById(R.id.rl_unread);
            img_profile = (ImageView) v.findViewById(R.id.img_profile);
            tv_username = (TextView) v.findViewById(R.id.tv_username);
            tv_tag = (TextView) v.findViewById(R.id.tv_tag);
            tv_unread = (TextView) v.findViewById(R.id.tv_unread);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
        }
    }

    public ChatlistAdapter(List<ChatList> chatListArrayList, int rowLayout, Context context) {
        this.chatListArrayList = chatListArrayList;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);

        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.tv_username.setTypeface(Typeface.createFromAsset(context.getAssets(),
                "fonts/POPPINS-SEMIBOLD_0.TTF"));
        holder.tv_tag.setTypeface(Typeface.createFromAsset(context.getAssets(),
                "fonts/Poppins-Regular_0.ttf"));

        holder.setIsRecyclable(false);
        holder.tv_username.setText(chatListArrayList.get(position).getChattingWith().getName());
        try {

            holder.tv_tag.setText(chatListArrayList.get(position).getLastMessage().getBody());
            holder.tv_time.setText(getDate(chatListArrayList.get(position).getLastMessage().getTimestamp()));

            if (chatListArrayList.get(position).getUnread() == 0) {
                holder.rl_unread.setVisibility(View.GONE);
            } else {
                holder.rl_unread.setVisibility(View.VISIBLE);
                holder.tv_unread.setText(String.valueOf(chatListArrayList.get(position).getUnread()));
            }
        } catch (Exception e) {
        }

        if (!chatListArrayList.get(position).getChattingWith().getImage().equals("")) {
            Picasso.get()
                    .load(chatListArrayList.get(position).getChattingWith().getImage())
                    .placeholder(R.drawable.defchat)
                    .error(R.drawable.defchat)
                    .into(holder.img_profile);
        }

    }

    private String getDate(long server_time) {

        //  long currentDateTime = System.currentTimeMillis();

        //creating Date from millisecond
        Date currentDate = new Date(server_time);

        //printing value of Date
        //System.out.println("current Timestamp: " + currentDateTime);
  /*      System.out.println("current TimestampSer: " + server_time);
        System.out.println("current Date: " + currentDate);
*/
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        //formatted value of current Date
//        System.out.println("Milliseconds to Date: " + df.format(currentDate));

        //Converting milliseconds to Date using Calendar
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(server_time);
  /*      System.out.println("Milliseconds to Date using Calendar:"
                + df.format(cal.getTime()));
*/
        //copying one Date's value into another Date in Java
        Date now = new Date();
        Date copiedDate = new Date(now.getTime());

       /* System.out.println("original Date: " + df.format(now));
        System.out.println("copied Date: " + df.format(copiedDate));*/

        return df.format(currentDate);
    }


    @Override
    public int getItemCount() {
        return chatListArrayList.size();
    }


    public void add(ChatList results) {
        chatListArrayList.add(results);
        notifyItemInserted(chatListArrayList.size() - 1);
    }

    public void addAll(List<ChatList> moveResults) {
        for (ChatList result : moveResults) {
            add(result);
        }
    }

}
