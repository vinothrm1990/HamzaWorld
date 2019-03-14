package com.app.hamzaworld.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.app.hamzaworld.R;
import com.app.hamzaworld.data.AllSize;
import com.app.hamzaworld.other.OnSizeChangeListener;
import java.util.ArrayList;
import static com.app.hamzaworld.activity.GrabOfferActivity.btnLayout;
import static com.app.hamzaworld.activity.GrabOfferActivity.detailLayout;


public class GrabOfferSizeAdapter extends RecyclerView.Adapter<GrabOfferSizeAdapter.ViewHolder> {

    Context context;
    ArrayList<AllSize> sizeList;
    OnSizeChangeListener mOnSizeChangeListener;
    RadioGroup lastCheckedRadioGroup = null;

    public GrabOfferSizeAdapter(Context context, ArrayList<AllSize> sizeList) {
        this.context = context;
        this.sizeList = sizeList;
    }

    public void setOnSizeChangeListener(Context mcontext, OnSizeChangeListener onSizeChangeListener) {
        mOnSizeChangeListener = onSizeChangeListener;
        context = mcontext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.size_adapter, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        AllSize size = sizeList.get(i);

        int id = (i+1)*100;

        Typeface font = Typeface.createFromAsset(context.getAssets(), "share_regular.otf");
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_enabled}
                },
                new int[] {

                        context.getResources().getColor(R.color.colorRed),
                        context.getResources().getColor(R.color.colorRed)


                }
        );

        final RadioButton rb = new RadioButton(context);
        rb.setId(id++);
        rb.setTypeface(font);
        rb.setTextSize(16);
        rb.setTextColor(Color.parseColor("#000000"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rb.setButtonTintList(colorStateList);
        }
        rb.setText(size.getSize());
        viewHolder.rgSize.addView(rb);

        if (!rb.isChecked()){
            btnLayout.setVisibility(View.GONE);
            detailLayout.fullScroll(View.FOCUS_DOWN);
        }

        viewHolder.rgSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                if (checkedId != -1) {

                    String defsize = String.valueOf(rb.getText());
                    if(mOnSizeChangeListener != null){
                        mOnSizeChangeListener.onSizeChanged(defsize);
                    }
                    btnLayout.setVisibility(View.VISIBLE);

                }

                if (lastCheckedRadioGroup != null && lastCheckedRadioGroup.getCheckedRadioButtonId()
                        != radioGroup.getCheckedRadioButtonId() && lastCheckedRadioGroup.getCheckedRadioButtonId() != -1) {

                    lastCheckedRadioGroup.clearCheck();

                }
                lastCheckedRadioGroup = radioGroup;

            }
        });
    }

    @Override
    public int getItemCount() {
        return sizeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSize;
        RadioGroup rgSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSize = itemView.findViewById(R.id.tv_size);
            rgSize = itemView.findViewById(R.id.radio_size);
        }
    }
}
