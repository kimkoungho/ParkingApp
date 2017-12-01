package com.parking.kani.parking.utility;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by koungho632 on 2016. 5. 7..
 */
//뷰홀더 동적 사용
public class ViewHolder {
    @SuppressWarnings("unckecked")
    public static <T extends View> T get(View view, int id){
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
