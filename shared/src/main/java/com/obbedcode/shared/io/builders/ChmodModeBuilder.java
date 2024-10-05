package com.obbedcode.shared.io.builders;

import com.obbedcode.shared.io.ModePermission;

public class ChmodModeBuilder {
    public static ChmodModeBuilder create() { return new ChmodModeBuilder(); }

    private int mOwnerPermissions = 0;
    private int mGroupPermissions = 0;
    private int mOtherPermissions = 0;

    public ChmodModeBuilder addOwnerPermissions(ModePermission permissions) {
        this.mOwnerPermissions = addIfNotExist(this.mOwnerPermissions, permissions);
        return this;
    }

    public ChmodModeBuilder setOwnerPermissions(ModePermission permissions) {
        this.mOwnerPermissions = permissions.getValue();
        return this;
    }

    public ChmodModeBuilder addGroupPermissions(ModePermission permissions) {
        this.mGroupPermissions = addIfNotExist(this.mGroupPermissions, permissions);
        return this;
    }

    public ChmodModeBuilder setGroupPermissions(ModePermission permissions) {
        this.mGroupPermissions = permissions.getValue();
        return this;
    }

    public ChmodModeBuilder addOtherPermissions(ModePermission permissions) {
        this.mOtherPermissions = addIfNotExist(this.mOtherPermissions, permissions);
        return this;
    }

    public ChmodModeBuilder setOtherPermissions(ModePermission permissions) {
        this.mOtherPermissions = permissions.getValue();
        return this;
    }

    public int getMode() { return (mOwnerPermissions * 100) + (mGroupPermissions * 10) + mOtherPermissions; }

    private int addIfNotExist(int old, ModePermission newAdd) {
        if((old & newAdd.getValue()) == 0) old += newAdd.getValue();
        return old;
    }
}
