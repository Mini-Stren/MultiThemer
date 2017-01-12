/*
 * Copyright (C) 2015 Mini-Stren.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
        ColorTheme theme = MultiThemer.getInstance().getThemesList().get(position);
        holder.textView.setText(theme.getTag());
        holder.textView.setTextColor(theme.getTextColorPrimary());
        holder.cardView.setCardBackgroundColor(theme.getColorPrimary());

        ColorTheme activeTheme = MultiThemer.getInstance().getActiveTheme();
        if (activeTheme != null
                && activeTheme.getTag().equals(theme.getTag())
                && activeTheme.getStyleResID() == theme.getStyleResID()) {
            holder.checkCircle.setVisibility(View.VISIBLE);
        } else {
            holder.checkCircle.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return MultiThemer.getInstance().getThemesList().size();
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
                    MultiThemer.getInstance().changeTheme(textView.getText().toString());
                }
            });
        }
    }
}
