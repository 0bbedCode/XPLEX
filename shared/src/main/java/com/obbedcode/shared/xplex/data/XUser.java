package com.obbedcode.shared.xplex.data;

public class XUser {
    public static final XUser DEFAULT = new XUser(XIdentity.GLOBAL_USER, XIdentity.GLOBAL_NAMESPACE);

    public int id;
    public String name;
    //Lets also note we have "identity" base

    public XUser() { }
    public XUser(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
