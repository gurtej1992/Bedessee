package com.bedessee.salesca.orderhistory;


import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * TODO: Document me...
 */
public class SavedOrder implements Comparable, Serializable {

    private String mId;

    private String mStore;

    private Date mStartTime;

    private Date mEndTime;

    private boolean mIsClosed;

    private String comment;

    private String contact;

    private int mNumProducts;

    List<SavedItem> savedItem;

    public SavedOrder(String id, String store, Date startTime, Date endTime, boolean isClosed, int numProducts,String mcomment,String mcontact) {
        mId = id;
        mStore = store;
        mStartTime = startTime;
        mEndTime = endTime;
        mIsClosed = isClosed;
        mNumProducts = numProducts;
        comment=mcomment;
        contact=mcontact;

    }

    public SavedOrder(String id, String store, Date startTime, Date endTime, boolean isClosed, int numProducts,List<SavedItem> savedItem) {
        mId = id;
        mStore = store;
        mStartTime = startTime;
        mEndTime = endTime;
        mIsClosed = isClosed;
        mNumProducts = numProducts;
        this.savedItem=savedItem;
    }

    public SavedOrder(String mcomment, String mcontact) {
        comment=mcomment;
        contact=mcontact;
    }

    public String getId() {
        return mId;
    }

    public String getStore() {
        return mStore;
    }

    public void setStore(String store) {
        mStore = store;
    }


    public void setId(String id) {
        mId = id;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Date startTime) {
        mStartTime = startTime;
    }

    public Date getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Date endTime) {
        mEndTime = endTime;
    }

    public boolean isClosed() {
        return mIsClosed;
    }

    public void setClosed(boolean isClosed) {
        mIsClosed = isClosed;
    }


    public int getNumProducts() {
        return mNumProducts;
    }

    public void setNumProducts(int numProducts) {
        mNumProducts = numProducts;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(@NonNull Object another) {

        final SavedOrder f = (SavedOrder)another;

        final Date fSartTime = f.getStartTime();

        if(mStartTime != null && fSartTime != null) {
            return fSartTime.compareTo(mStartTime);
        } else {
            return -1;
        }
    }
}
