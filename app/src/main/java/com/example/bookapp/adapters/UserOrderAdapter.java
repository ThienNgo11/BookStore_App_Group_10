package com.example.bookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookapp.R;
import com.example.bookapp.models.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onOrderClick(Order order);
        void onCancelOrder(Order order);
        void onReorder(Order order);
    }

    public UserOrderAdapter(Context context, List<Order> orderList, OnOrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Đơn hàng #" + order.getId());
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvOrderStatus.setText(getStatusText(order.getStatus()));
        holder.tvTotalAmount.setText(String.format("%,d đ", (int) order.getTotalAmount()));

        // Set màu status
        setStatusColor(holder.tvOrderStatus, order.getStatus());

        // Hiển thị nút hành động
        updateActionButtons(holder, order.getStatus());

        // Click listeners
        holder.cardView.setOnClickListener(v -> listener.onOrderClick(order));
        holder.btnViewDetail.setOnClickListener(v -> listener.onOrderClick(order));
        holder.btnCancelOrder.setOnClickListener(v -> listener.onCancelOrder(order));
        holder.btnReorder.setOnClickListener(v -> listener.onReorder(order));
    }

    private String getStatusText(String status) {
        switch (status) {
            case "Pending": return "Chờ xác nhận";
            case "Accepted": return "Đã xác nhận";
            case "Shipped": return "Đang giao hàng";
            case "Completed": return "Hoàn thành";
            case "Rejected": return "Đã hủy";
            default: return status;
        }
    }

    private void setStatusColor(TextView tvStatus, String status) {
        int colorRes;
        switch (status) {
            case "Pending":
                colorRes = R.color.status_pending;
                break;
            case "Accepted":
                colorRes = R.color.status_accepted;
                break;
            case "Shipped":
                colorRes = R.color.status_shipped;
                break;
            case "Completed":
                colorRes = R.color.status_completed;
                break;
            case "Rejected":
                colorRes = R.color.status_rejected;
                break;
            default:
                colorRes = R.color.status_default;
                break;
        }
        tvStatus.setTextColor(context.getResources().getColor(colorRes));
    }

    private void updateActionButtons(OrderViewHolder holder, String status) {
        holder.btnCancelOrder.setVisibility(View.GONE);
        holder.btnReorder.setVisibility(View.GONE);

        switch (status) {
            case "Pending":
                holder.btnCancelOrder.setVisibility(View.VISIBLE);
                break;
            case "Completed":
            case "Rejected":
                holder.btnReorder.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvTotalAmount;
        MaterialButton btnViewDetail, btnCancelOrder, btnReorder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
            btnReorder = itemView.findViewById(R.id.btnReorder);
        }
    }
}