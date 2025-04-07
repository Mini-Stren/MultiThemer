/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.ministren.multithemer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Mini-Stren on 28.08.2017.
 */
public class ListFragmentAdapter : RecyclerView.Adapter<ListFragmentAdapter.ViewHolder>() {

    private val themesList = MultiThemer.getThemesList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.multithemer_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = themesList[position]
        holder.textView.text = theme.tag
        holder.cardView.setCardBackgroundColor(theme.getColorPrimary())

        val activeTheme = MultiThemer.getActiveTheme()
        holder.checkCircle.visibility = if (theme == activeTheme) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = themesList.size

    public inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        public val cardView: CardView =
            view.findViewById(R.id.multithemer_theme_card)

        public val textView: TextView =
            view.findViewById(R.id.multithemer_theme_card_text)

        public val checkCircle: ImageView =
            view.findViewById(R.id.multithemer_theme_card_check_circle)

        init {
            cardView.setOnClickListener { MultiThemer.changeTheme(textView.text.toString()) }
        }
    }
}
