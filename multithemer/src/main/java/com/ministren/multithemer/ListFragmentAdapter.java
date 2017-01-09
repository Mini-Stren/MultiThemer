package com.ministren.multithemer;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mini-Stren on 06.01.2017
 */

class ListFragmentAdapter extends RecyclerView.Adapter<ListFragmentAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multithemer_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ColorTheme theme = MultiThemer.getThemesList().get(position);
        holder.textView.setText(theme.getTag());
        holder.textView.setTextColor(theme.getTextColorPrimary());
        holder.cardView.setCardBackgroundColor(theme.getColorPrimary());

        ColorTheme activeTheme = MultiThemer.getActiveTheme();
        if (activeTheme != null
                && activeTheme.getTag().equals(theme.getTag())
                && activeTheme.getStyleID() == theme.getStyleID()) {
            holder.checkCircle.setVisibility(View.VISIBLE);
        } else {
            holder.checkCircle.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return MultiThemer.getThemesList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView checkCircle;
        TextView textView;
        CardView cardView;

        ViewHolder(View view) {
            super(view);
            checkCircle = (ImageView) view.findViewById(R.id.multithemer_theme_card_check_circle);
            textView = (TextView) view.findViewById(R.id.multithemer_theme_card_text);
            cardView = (CardView) view.findViewById(R.id.multithemer_theme_card);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MultiThemer.setActiveTheme(view.getContext(), textView.getText().toString());
                }
            });
        }
    }
}
