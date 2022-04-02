package com.bedessee.salesca.modal;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bedessee.salesca.customview.ItemType;
import com.bedessee.salesca.product.Product;
@Entity
public class ShoppingCartNew {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getmProduct() {
        return mProduct;
    }

    public void setmProduct(Product mProduct) {
        this.mProduct = mProduct;
    }

    public int getmQuantity() {
        return mQuantity;
    }

    public void setmQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public ItemType getmItemType() {
        return mItemType;
    }

    public void setmItemType(ItemType mItemType) {
        this.mItemType = mItemType;
    }

    public String getmEnteredPrice() {
        return mEnteredPrice;
    }

    public void setmEnteredPrice(String mEnteredPrice) {
        this.mEnteredPrice = mEnteredPrice;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private Product mProduct;
    private int mQuantity;
    private ItemType mItemType;
    private String mEnteredPrice;
}
