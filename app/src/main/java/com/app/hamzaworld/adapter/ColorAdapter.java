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
import com.app.hamzaworld.data.AllColor;
import com.pixplicity.easyprefs.library.Prefs;
import com.tfb.fbtoast.FBToast;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    Context context;
    ArrayList<AllColor> colorList;
    RadioGroup rgChecked = null;

    public ColorAdapter(Context context, ArrayList<AllColor> colorList) {
        this.context = context;
        this.colorList = colorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.color_adapter, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        AllColor color = colorList.get(i);

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
        RadioButton rb = new RadioButton(context);
        rb.setId(id++);
        rb.setTypeface(font);
        rb.setTextSize(16);
        rb.setTextColor(Color.parseColor("#000000"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rb.setButtonTintList(colorStateList);
        }
        rb.setText(color.getColor());
        viewHolder.rgColor.addView(rb);

        int colorId = viewHolder.rgColor.getCheckedRadioButtonId();

        if (colorId != -1){

            String defcolor = String.valueOf(rb.getText());
            FBToast.infoToast(context,
                    defcolor,
                    FBToast.LENGTH_SHORT);

        }


        String bid = Prefs.getString("barid", null);

        //getSize(bid, defcolor);


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

            rgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    if (rgChecked != null
                            && rgChecked.getCheckedRadioButtonId()
                            != radioGroup.getCheckedRadioButtonId()
                            && rgChecked.getCheckedRadioButtonId() != -1) {
                        rgChecked.clearCheck();


                       /* FBToast.infoToast(context,
                                "Radio button clicked " + radioGroup.getCheckedRadioButtonId(),
                                FBToast.LENGTH_SHORT);*/

                    }
                    rgChecked = radioGroup;

                }
            });
        }
    }
}
