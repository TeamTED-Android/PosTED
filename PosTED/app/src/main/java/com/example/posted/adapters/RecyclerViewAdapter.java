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
import com.example.posted.async.AsyncImageDecoder;
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
    private Map<Integer, Bitmap> mPreLoadedBitmaps;

    public RecyclerViewAdapter(Context context, ArrayList<LaptopSqlite> laptops, RecyclerViewAdapter
            .RecyclerViewSelectedElement onItemSelectedListener) {
        this.mContext = context;
        this.mLaptops = laptops;
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    private Map<Integer, Bitmap> getPreLoadedBitmaps() {
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
//            holder.mRam.setText(current.getCapacity_ram());
//            holder.mHdd.setText(current.getCapacity_hdd());
//            holder.mProcessor.setText(current.getProcessor_type());
//            holder.mVideoCard.setText(current.getVideo_card_type());
//            holder.mDisplay.setText(current.getDisplay_size());
            holder.mPrice.setText(current.getPrice());
            holder.mCurrency.setText(current.getCurrency());
            int id = current.getId();
            String base64Img = current.getImage();
            //TODO if image is null
            if (base64Img.contains(",")) {
                base64Img = base64Img.substring(current.getImage().indexOf(','));
            }
            Map<Integer, Bitmap> bitmapCache = this.getPreLoadedBitmaps();
            if (!bitmapCache.containsKey(id)) {
                AsyncImageDecoder imageLoader = new AsyncImageDecoder(holder, position);
                imageLoader.execute(base64Img);
            } else {
                Bitmap bitmap = bitmapCache.get(id);
                holder.setImageViewBitmap(bitmap);
            }
        }
    }

    @Override
    public int getItemCount() {

        return this.mLaptops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AsyncImageDecoder
            .Listener {

        private TextView mModel;
//        private TextView mRam;
//        private TextView mHdd;
//        private TextView mProcessor;
//        private TextView mVideoCard;
//        private TextView mDisplay;
        private TextView mPrice;
        private TextView mCurrency;
        private long mCurrExecTime;
//        private ImageView mImage;

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
//            this.mRam = (TextView) itemView.findViewById(R.id.ram);
//            this.mHdd = (TextView) itemView.findViewById(R.id.hdd);
//            this.mProcessor = (TextView) itemView.findViewById(R.id.processor);
//            this.mVideoCard = (TextView) itemView.findViewById(R.id.video_card);
//            this.mDisplay = (TextView) itemView.findViewById(R.id.display);
            this.mPrice = (TextView) itemView.findViewById(R.id.price);
            this.mCurrency = (TextView) itemView.findViewById(R.id.currency);
//            this.mImage = (ImageView) itemView.findViewById(R.id.image_lpt);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            LaptopSqlite clickedLaptop = RecyclerViewAdapter.this.mLaptops.get(this.getAdapterPosition());
            RecyclerViewAdapter.this.mOnItemSelectedListener.onItemSelected(clickedLaptop);
        }

        @Override
        public void onImageDecoded(Bitmap bitmap, long execTime, int position) {
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
            int id = item.getId();
            Map<Integer, Bitmap> bitmapCache = RecyclerViewAdapter.this.getPreLoadedBitmaps();
            bitmapCache.put(id, bitmap);
        }
    }
}
