package com.zeeplive.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.zeeplive.app.R;
import com.zeeplive.app.activity.ComplaintDescription;
import com.zeeplive.app.response.ViewTicketResponse;

import java.util.List;

public class ViewTicketAdapter extends RecyclerView.Adapter<ViewTicketAdapter.myViewHolder> {

    Context context;
    List<ViewTicketResponse.Result> list;

    public ViewTicketAdapter(Context context, List<ViewTicketResponse.Result> list) {

        this.context = context;
        this.list = list;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_view_ticket, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {

        holder.issue_heading.setText(list.get(position).getIssue_heading() + " (" + list.get(position).getTicket_no() + ")");
        holder.date.setText(list.get(position).getCreated_at());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView issue_heading, date;
        CardView container;

        public myViewHolder(View itemView) {
            super(itemView);

            issue_heading = itemView.findViewById(R.id.issue_heading);
            date = itemView.findViewById(R.id.date);
            container = itemView.findViewById(R.id.container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(context, ComplaintDescription.class);
                    intent.putExtra("query", list.get(0).getDescription());
                    intent.putExtra("answer", list.get(0).getResponse());
                    context.startActivity(intent);
                }
            });
        }
    }
}