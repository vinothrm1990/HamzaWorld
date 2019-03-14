package com.app.hamzaworld.adapter;

import android.annotation.SuppressLint;
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
import com.app.hamzaworld.data.AllColor;
import com.app.hamzaworld.other.OnColorChangeListener;

import java.util.ArrayList;
import static com.app.hamzaworld.activity.GrabProductActivity.btnLayout;
import static com.app.hamzaworld.activity.GrabProductActivity.detailLayout;


public class GrabProductColorAdapter extends RecyclerView.Adapter<GrabProductColorAdapter.ViewHolder> {

    Context context;
    ArrayList<AllColor> colorList;
    OnColorChangeListener mOnColorChangeListener;
    RadioGroup lastCheckedRadioGroup = null;

    public GrabProductColorAdapter(Context context, ArrayList<AllColor> colorList) {
        this.context = context;
        this.colorList = colorList;
    }

    public void setOnColorChangeListener(Context mcontext, OnColorChangeListener onColorChangeListener) {
        mOnColorChangeListener = onColorChangeListener;
        context = mcontext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.color_adapter, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        AllColor color = colorList.get(i);

        int id = (i+1);

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

        rb.setText(color.getColor());
        viewHolder.rgColor.addView(rb);

        if (!rb.isChecked()){
            btnLayout.setVisibility(View.GONE);
            detailLayout.fullScroll(View.FOCUS_DOWN);

        }

        viewHolder.rgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                RadioButton rb = radioGroup.findViewById(checkedId);

                if (checkedId != -1){

                    String defcolor = String.valueOf(rb.getText());
                    if(mOnColorChangeListener != null){
                        mOnColorChangeListener.onColorChanged(defcolor);
                    }
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
        return colorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvColor;
        RadioGroup rgColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvColor = itemView.findViewById(R.id.tv_color);
            rgColor = itemView.findViewById(R.id.radio_color);
        }
    }

}
