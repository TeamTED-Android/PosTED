package com.example.posted.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.posted.R;
import com.example.posted.models.Order;
import java.util.ArrayList;

public class RecyclerViewOrdersAdapter extends RecyclerView.Adapter<RecyclerViewOrdersAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Order> mOrders;

    public RecyclerViewOrdersAdapter(Context context, ArrayList<Order> orders) {
        this.mContext = context;
        this.mOrders = orders;

    }

    @Override
    public RecyclerViewOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.row_orders_layout, parent, false);
        RecyclerViewOrdersAdapter.ViewHolder viewHolder = new RecyclerViewOrdersAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewOrdersAdapter.ViewHolder holder, int position) {
        Order currentOrder = this.mOrders.get(position);
        holder.mModel.setText(currentOrder.getModel());
        holder.mRam.setText(currentOrder.getCapacity_ram());
        holder.mHdd.setText(currentOrder.getCapacity_hdd());
        holder.mProcessor.setText(currentOrder.getProcessor_type());
        holder.mVideoCard.setText(currentOrder.getVideo_card_type());
        holder.mDisplaySize.setText(currentOrder.getDisplay_size());
        holder.mPrice.setText(currentOrder.getPrice());
        holder.mCurrency.setText(currentOrder.getCurrency());
        holder.mUser.setText(currentOrder.getUser());
    }

    @Override
    public int getItemCount() {
       return this.mOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mModel;
        private TextView mRam;
        private TextView mHdd;
        private TextView mProcessor;
        private TextView mVideoCard;
        private TextView mDisplaySize;
        private TextView mPrice;
        private TextView mCurrency;
        private TextView mUser;


        public ViewHolder(View itemView) {
            super(itemView);
            this.mModel = (TextView) itemView.findViewById(R.id.model_order);
            this.mRam = (TextView) itemView.findViewById(R.id.ram_order);
            this.mHdd = (TextView) itemView.findViewById(R.id.hdd_order);
            this.mProcessor = (TextView) itemView.findViewById(R.id.processor_order);
            this.mVideoCard = (TextView) itemView.findViewById(R.id.video_card_order);
            this.mDisplaySize = (TextView) itemView.findViewById(R.id.display_size_order);
            this.mPrice = (TextView) itemView.findViewById(R.id.price_order);
            this.mCurrency = (TextView) itemView.findViewById(R.id.currency_order);
            this.mUser = (TextView) itemView.findViewById(R.id.user_order);
        }
    }
}
