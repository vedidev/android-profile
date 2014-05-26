/*
 * Copyright (C) 2012 Soomla Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.soomla.social.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by oriargov on 5/22/14.
 */
public class Utils {
    public static String getHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "com.soomla.social.example",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
//                Log.d("KeyHash:", hashKey);
                return hashKey;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds the value of the given enumeration by name, case-insensitive.
     * Throws an IllegalArgumentException if no match is found.
     **/
    public static <T extends Enum<T>> T valueOfIgnoreCase(Class<T> enumeration, String name) {
        for(T enumValue : enumeration.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(name)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException(
                "There is no value with name '" + name +
                        " in Enum " + enumeration.getClass().getName());
    }
}
