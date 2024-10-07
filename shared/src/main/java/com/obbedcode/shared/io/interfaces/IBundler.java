package com.obbedcode.shared.io.interfaces;

import android.os.Bundle;

public interface IBundler {
    Bundle toBundle();
    void fromBundle(Bundle bundle);
}
