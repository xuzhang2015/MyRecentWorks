package com.xu.ccgv.mynearplaceapplication.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;
import com.xu.ccgv.mynearplaceapplication.base.BaseRecyclerViewAdapter;
import com.xu.ccgv.mynearplaceapplication.base.BaseViewHolder;

import java.util.List;

/**
 * Created by xz on 29/04/2018.
 */

public class LocationListAdapter extends BaseRecyclerViewAdapter<LocationEntity> {

    public LocationListAdapter(Context context, List<LocationEntity> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void bindData(final BaseViewHolder holder, final LocationEntity data, int position) {
        //Picasso for future use, in case of large images
//        String uri=UtilsMethod.getImageUri(data.getIcon_bm());
//        Picasso picasso = new Picasso.Builder(context).build();
//                picasso.load(uri)
//                .fit()
//                .centerInside()
//                .placeholder(R.drawable.ic_launcher_foreground)
//                .error(R.drawable.ic_launcher_foreground)
//                .into(holder.<ImageView>getView(R.id.location_icon_iv));
        if (null == data.getIcon_bm())
            holder.<ImageView>getView(R.id.location_icon_iv).setImageResource(R.drawable.ic_launcher_foreground);
        else holder.<ImageView>getView(R.id.location_icon_iv).setImageBitmap(data.getIcon_bm());
        holder.<TextView>getView(R.id.location_detail_tv).setText(data.getName() + "\n" + data.getVicinity());
        holder.<TextView>getView(R.id.location_distance_tv).setText(data.getDirection() + " " + UtilsMethod.distanceFormat(data.getDistance()) + "m");
        //

        holder.<TextView>getView(R.id.location_detail_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onItemClickListenerll) {
                    int pos = holder.getLayoutPosition();
                    //register the clicklistener to the onItemClickListenerll
                    onItemClickListenerll.onItemClickListener(view, pos);
                }
            }
        });
    }
}
