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

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;

/**
 * Created by Mini-Stren on 06.01.2017
 */

public final class Utils {

    static String LOG_TAG = "MultiThemer";

    private Utils() {
        throw new AssertionError();
    }

    @ColorInt
    public static int getColorStyleAttr(Context context, @StyleRes int styleResId, @AttrRes int attr) {
        TypedArray typedArray = context.obtainStyledAttributes(styleResId, new int[]{attr});
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }

    @ColorInt
    public static int getColorThemeAttr(ColorTheme theme, @AttrRes int attr) {
        return getColorStyleAttr(theme.getContext(), theme.getStyleResID(), attr);
    }
}
