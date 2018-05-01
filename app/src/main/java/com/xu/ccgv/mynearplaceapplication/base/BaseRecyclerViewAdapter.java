package com.xu.ccgv.mynearplaceapplication.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    //register the click events
    protected OnItemClickListener onItemClickListenerll;
    protected OnItemLongClickListener onItemLongClickListener;
    //
    private Context context;
    private LayoutInflater inflater;
    private List<T> datas;
    private int layoutId;

    public BaseRecyclerViewAdapter(Context context, List<T> datas, int layoutId) {
        this.context = context;
        this.datas = datas;
        this.layoutId = layoutId;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(layoutId, parent, false);
        final BaseViewHolder holder = new BaseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        bindData(holder, datas.get(position), position);
    }


    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    protected abstract void bindData(BaseViewHolder holder, T data, int position);


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListenerll = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener extends View.OnClickListener {
        void onItemClickListener(View v, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClickListener(View v, int position);
    }
}
