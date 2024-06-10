package com.edu.homeedu.puzzle.image_search.utils;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class BundleCompat {
    public static <T extends Serializable> T getSerializable(Bundle bundle, String key, Class<T> clazz) {
        return bundle.getSerializable(key, clazz);
    }

    public static <T extends Serializable> T getSerializable(@NonNull Intent intent, String key, @NonNull Class<T> clazz) {
        return intent.getSerializableExtra(key, clazz);
    }
}
