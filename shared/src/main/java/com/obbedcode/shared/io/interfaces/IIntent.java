package com.obbedcode.shared.io.interfaces;

import android.content.Intent;

public interface IIntent {
    void fromIntent(Intent intent);
    void toIntent(Intent intent);
}
