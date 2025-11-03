package com.example.bookapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bookapp.R;
import com.example.bookapp.models.Book;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private Context context;

    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText("Tác giả: " + book.getAuthor());
        holder.tvPrice.setText(String.format("%,.0f đ", book.getPrice()));

        // Load image từ URL dùng Glide
        Glide.with(context)
                .load(book.getImage()) // URL string từ model
                .placeholder(R.drawable.ic_launcher_background) // Hình default trong khi load
                .error(R.drawable.ic_launcher_foreground) // Hình nếu lỗi load
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache để load nhanh hơn lần sau
                .into(holder.ivImage);

        // Click để thêm vào cart (chỉ cho user, guest có thể disable)
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Thêm " + book.getTitle() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
            // Logic thêm vào cart table ở đây (sẽ implement sau nếu cần)
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvAuthor, tvPrice;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivBookImage);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvPrice = itemView.findViewById(R.id.tvBookPrice);
        }
    }
}