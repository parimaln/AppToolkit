package com.appmanager.parimal.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import com.appmanager.parimal.R;
import com.appmanager.parimal.model.AppDetail;

/**
 * Created by parimal on 25-03-2015.
 */
public class CardsAppsAdapter extends RecyclerView.Adapter<CardsAppsAdapter.CardsRowController>  {
    Activity mContext;
    List<AppDetail> mAppsList;
    OnItemClickListener mItemClickListener;
    public CardsAppsAdapter(List<AppDetail> appsList, Activity cntx) {
        this.mContext = cntx;
        this.mAppsList = appsList;
    }

    @Override
    public CardsRowController onCreateViewHolder(ViewGroup parent, int i) {
        return(new CardsRowController(mContext, mContext.getLayoutInflater()
                .inflate(R.layout.row_card, parent, false)));
    }

    @Override
    public void onBindViewHolder(CardsRowController cardsRowController, int i) {
        AppDetail tempApp = mAppsList.get(i);
        cardsRowController.bindModel(tempApp);
    }

    @Override
    public int getItemCount() {
        return mAppsList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    public interface OnItemClickListener {
        public void onItemClick(View view, int position, String label, String pkgInfo);
        public void onItemLongClick(View view, int position, String label, String pkgInfo);
    }
    public class CardsRowController extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        TextView label=null;
        ImageView infoImg;
        String template=null;
        Context mContext;

        public CardsRowController(Context _context, View row)
        {
            super(row);
            this.mContext = _context;
            Typeface typefaceCondLight = Typeface.createFromAsset(mContext.getAssets(), "rcondensedlight.ttf"); //rcondensedlight.ttf
            infoImg = (ImageView) row.findViewById(R.id.iconImgView);
            label = (TextView) row.findViewById(R.id.appNameTv);
            label.setTypeface(typefaceCondLight);
            row.setOnClickListener(this);
            row.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, mAppsList.get(getAdapterPosition()).getId(),
                        mAppsList.get(getAdapterPosition()).getLabel().toString(),
                        mAppsList.get(getAdapterPosition()).getName().toString());
            }

        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(v, mAppsList.get(getAdapterPosition()).getId(),
                        mAppsList.get(getAdapterPosition()).getLabel().toString(),
                        mAppsList.get(getAdapterPosition()).getName().toString());
            }
            return true;
        }

        public void bindModel(AppDetail tempBookModel) {
            label.setText(tempBookModel.getLabel());
            infoImg.setImageDrawable(tempBookModel.getIcon());
            //label.setText(tempBookModel.get_name());
        }
    }
}
