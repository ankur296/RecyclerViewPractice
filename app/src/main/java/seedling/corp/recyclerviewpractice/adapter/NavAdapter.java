package seedling.corp.recyclerviewpractice.adapter;

import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import seedling.corp.recyclerviewpractice.R;

public class NavAdapter extends RecyclerView.Adapter<NavAdapter.ViewHolder> {
    private String[] mDataset;
    private int[] mNavIcons;
    private OnItemClickListener mListener;

    /**
     * Interface for receiving click events from cells.
     */
    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    public NavAdapter(String[] myDataset, int[] navMenuIcons, OnItemClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
        mNavIcons = navMenuIcons;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View v = vi.inflate(R.layout.drawer_list_item, parent, false);
        TextView tv = (TextView) v.findViewById(android.R.id.text1);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(mDataset[position]);
        holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(mNavIcons[position],0, 0, 0);


        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
