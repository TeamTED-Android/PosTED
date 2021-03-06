package com.example.posted.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.posted.R;
import com.example.posted.async.AsyncListImageLoader;
import com.example.posted.models.LaptopSqlite;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public interface RecyclerViewSelectedElement {

        void onItemSelected(LaptopSqlite laptop);
    }

    private Context mContext;
    private ArrayList<LaptopSqlite> mLaptops;
    private RecyclerViewAdapter.RecyclerViewSelectedElement mOnItemSelectedListener;
    private Map<String, Bitmap> mPreLoadedBitmaps;

    public RecyclerViewAdapter(Context context, ArrayList<LaptopSqlite> laptops, RecyclerViewAdapter
            .RecyclerViewSelectedElement onItemSelectedListener) {
        this.mContext = context;
        this.mLaptops = laptops;
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    private Map<String, Bitmap> getPreLoadedBitmaps() {
        if (this.mPreLoadedBitmaps == null) {
            this.mPreLoadedBitmaps = new WeakHashMap<>();
        }
        return this.mPreLoadedBitmaps;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.row_layout, parent, false);
        RecyclerViewAdapter.ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        if (holder != null) {
            LaptopSqlite current = this.mLaptops.get(position);

            holder.mModel.setText(current.getModel());
            holder.mPrice.setText(current.getPrice());
            holder.mCurrency.setText(current.getCurrency());
            String id = current.getId();
            String imagePath = current.getImagePath();
            String imageName = current.getImageName();
            if (imagePath == null) {
                return;
            }
            Map<String, Bitmap> bitmapCache = this.getPreLoadedBitmaps();
            Bitmap bitmap = bitmapCache.get(id);
            if (bitmap == null) {
                AsyncListImageLoader imageLoader = new AsyncListImageLoader(holder, position);
                imageLoader.execute(imagePath, imageName);
            } else {
                bitmap = bitmapCache.get(id);
                holder.setImageViewBitmap(bitmap);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.mLaptops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AsyncListImageLoader
            .Listener {

        private TextView mModel;
        private TextView mPrice;
        private TextView mCurrency;
        private long mCurrExecTime;

        private ImageView getImageView() {
            if (this.itemView == null) {
                return null;
            }
            View imageView = this.itemView.findViewById(R.id.image_lpt);
            if (imageView instanceof ImageView) {
                return (ImageView) imageView;
            }
            return null;
        }

        private void setImageViewBitmap(Bitmap bitmap) {
            ImageView imageView = this.getImageView();
            if (imageView == null) {
                return;
            }
            imageView.setImageBitmap(bitmap);
        }

        private void setImageViewResId(int resId) {
            ImageView imageView = this.getImageView();
            if (imageView == null) {
                return;
            }
            imageView.setImageResource(resId);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            this.mModel = (TextView) itemView.findViewById(R.id.model);
            this.mPrice = (TextView) itemView.findViewById(R.id.price);
            this.mCurrency = (TextView) itemView.findViewById(R.id.currency);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            LaptopSqlite clickedLaptop = RecyclerViewAdapter.this.mLaptops.get(this.getAdapterPosition());
            RecyclerViewAdapter.this.mOnItemSelectedListener.onItemSelected(clickedLaptop);
        }

        @Override
        public void onImageLoaded(Bitmap bitmap, long execTime, int position) {
            if (execTime < this.mCurrExecTime) {
                return;
            }
            this.mCurrExecTime = execTime;
            if (bitmap == null) {
                this.setImageViewResId(R.mipmap.no_image_black);
                return;
            }
            this.setImageViewBitmap(bitmap);
            if (position < 0 || position >= RecyclerViewAdapter.this.getItemCount()) {
                return;
            }
            LaptopSqlite item = RecyclerViewAdapter.this.mLaptops.get(position);
            String id = item.getId();
            Map<String, Bitmap> bitmapCache = RecyclerViewAdapter.this.getPreLoadedBitmaps();
            bitmapCache.put(id, bitmap);
        }
    }
}
