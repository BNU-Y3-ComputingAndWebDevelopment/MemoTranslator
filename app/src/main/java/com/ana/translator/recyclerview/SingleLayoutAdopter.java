package com.ana.translator.recyclerview;

public abstract class SingleLayoutAdopter extends BaseAdopter {
    private final int layoutId;

    public SingleLayoutAdopter(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return layoutId;
    }

    public abstract int getItemCount();
}
