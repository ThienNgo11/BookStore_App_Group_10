package com.example.bookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookapp.R;
import com.example.bookapp.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemListener listener;

    public interface OnCartItemListener {
        void onItemCheckedChanged();
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemDeleted(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Checkbox
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(item.isSelected());
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            listener.onItemCheckedChanged();
        });

        // Thông tin sách
        holder.tvTitle.setText(item.getBookTitle());
        holder.tvPrice.setText(String.format("%,.0f đ", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Hiển thị tình trạng kho
        if (item.getStock() < item.getQuantity()) {
            holder.tvStock.setVisibility(View.VISIBLE);
            holder.tvStock.setText("Kho chỉ còn " + item.getStock() + " cuốn");
            holder.tvStock.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvStock.setVisibility(View.GONE);
        }

        // Load ảnh
        Glide.with(context)
                .load(item.getBookImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivImage);

        // Nút tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            if (newQuantity > item.getStock()) {
                holder.tvStock.setVisibility(View.VISIBLE);
                holder.tvStock.setText("Đã đạt số lượng tối đa");
                return;
            }
            item.setQuantity(newQuantity);
            holder.tvQuantity.setText(String.valueOf(newQuantity));
            listener.onQuantityChanged(item, newQuantity);
        });

        // Nút giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity < 1) {
                listener.onItemDeleted(item);
                return;
            }
            item.setQuantity(newQuantity);
            holder.tvQuantity.setText(String.valueOf(newQuantity));
            listener.onQuantityChanged(item, newQuantity);
        });

        // Nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            listener.onItemDeleted(item);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // Chọn tất cả
    public void selectAll(boolean isSelected) {
        for (CartItem item : cartItems) {
            item.setSelected(isSelected);
        }
        notifyDataSetChanged();
    }

    // Lấy danh sách items đã chọn
    public List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView ivImage;
        TextView tvTitle, tvPrice, tvQuantity, tvStock;
        ImageButton btnIncrease, btnDecrease, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            ivImage = itemView.findViewById(R.id.ivBookImage);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvPrice = itemView.findViewById(R.id.tvBookPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvStock = itemView.findViewById(R.id.tvStock);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}