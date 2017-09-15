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

import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.StyleRes

/**
 * Created by Mini-Stren on 28.08.2017.
 */

class ColorTheme(private val context: Context, val tag: String, @StyleRes val styleResID: Int) {

    override fun toString(): String = "ColorTheme { tag='$tag', styleResID='$styleResID' }"

    @ColorInt
    fun getAttrColor(@AttrRes attr: Int): Int {
        val typedArray = context.obtainStyledAttributes(styleResID, intArrayOf(attr))
        val color = typedArray.getColor(0, 0)
        typedArray.recycle()
        return color
    }

    @ColorInt
    fun getColorPrimary() = getAttrColor(R.attr.colorPrimary)

    @ColorInt
    fun getColorPrimaryDark() = getAttrColor(R.attr.colorPrimaryDark)

    @ColorInt
    fun getColorAccent() = getAttrColor(R.attr.colorAccent)
}
