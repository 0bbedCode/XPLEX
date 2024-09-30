package com.obbedcode.xplex.views.fragment.base;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {
    public VB _binding;
    protected VB get() {
        if (_binding == null) {
            throw new IllegalStateException("Binding is only valid between onCreateView and onDestroyView.");
        }
        return _binding;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            _binding = (VB) method.invoke(null, inflater, container, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inflate ViewBinding", e);
        }
        return get().getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}