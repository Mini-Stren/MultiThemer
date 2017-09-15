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

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.multithemer_list_item.view.*

/**
 * Created by Mini-Stren on 28.08.2017.
 */

class ListFragmentAdapter : RecyclerView.Adapter<ListFragmentAdapter.ViewHolder>() {

    private val mThemesList = MultiThemer.getThemesList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.multithemer_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val theme = mThemesList[position]
        holder?.textView?.text = theme.tag
        holder?.cardView?.setCardBackgroundColor(theme.getColorPrimary())

        val activeTheme = MultiThemer.getActiveTheme()
        holder?.checkCircle?.visibility = if (theme == activeTheme) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = mThemesList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.multithemer_theme_card
        val textView: TextView = view.multithemer_theme_card_text
        val checkCircle: ImageView = view.multithemer_theme_card_check_circle

        init {
            cardView.setOnClickListener { MultiThemer.changeTheme(textView.text.toString()) }
        }
    }
}
