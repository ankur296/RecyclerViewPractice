package seedling.corp.recyclerviewpractice.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

import seedling.corp.recyclerviewpractice.R;
import seedling.corp.recyclerviewpractice.utils.Constants;
import seedling.corp.recyclerviewpractice.utils.Logger;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private String[] mDataset;
    Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public String getItem(int position) {
        return mDataset[position];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;
        public ImageView mAlarmImageView;
        public ImageView mImageView;

        public ViewHolder(View container) {
            super(container);
            mTextView = (TextView) container.findViewById(R.id.category_title);
            mAlarmImageView = (ImageView) container.findViewById(R.id.tv_alarm_icon);
            mImageView = (ImageView) container.findViewById(R.id.category_icon);
        }
    }

    public MyAdapter(Context context){
        mContext = context;
        SharedPreferences defaultPreferences =
                mContext.getSharedPreferences(Constants.REMINDER_PREF_FILE_NAME, mContext.MODE_PRIVATE);
        Map remindersMap = defaultPreferences.getAll();
        mDataset = Arrays.copyOf(
                remindersMap.keySet().toArray(),
                remindersMap.keySet().toArray().length,
                String[].class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mTextView.setText(mDataset[position]);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(v, position);
            }
        });

        //set unique transition name for each element
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.mImageView.setTransitionName("image"+position);
        }

        //fetch img path
        String json = mContext.getSharedPreferences(Constants.REMINDER_PREF_FILE_NAME, mContext.MODE_PRIVATE)
                .getString(mDataset[position], null);
        String imgPath = "";
        boolean isEnabled = true;

        try {
            JSONObject jsonObject = new JSONObject(json);
            imgPath = jsonObject.optString(Constants.IMAGE_PATH_FIELD);
            isEnabled = jsonObject.optBoolean(Constants.ENABLED_FIELD, true);
            Logger.e("img path for grid " + imgPath + ", isenabled? "+isEnabled);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isEnabled)
            holder.mAlarmImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_action_alarm_live));
        else
            holder.mAlarmImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_action_alarm));

        if (imgPath.equalsIgnoreCase("")) {
            holder.mImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher));
        }else{
            int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.image_size)
                    * Constants.IMAGE_ANIM_MULTIPLIER;

            Glide.with(mContext)
                    .load(Uri.parse(imgPath))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.color.lighter_gray)
                    .override(imageSize, imageSize)
                    .into(new BitmapImageViewTarget(holder.mImageView) {
                              @Override
                              protected void setResource(Bitmap resource) {
                                  RoundedBitmapDrawable circularBitmapDrawable =
                                          RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                  circularBitmapDrawable.setCircular(true);
                                  holder.mImageView.setImageDrawable(circularBitmapDrawable);
                              }
                          }
                    );
        }
    }


    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
