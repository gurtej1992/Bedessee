package com.bedessee.sales.shoppingcart;

import com.bedessee.sales.customview.ItemType;
import com.bedessee.sales.customview.QtySelector;
import com.bedessee.sales.product.Product;

import java.io.Serializable;

public class ShoppingCartProduct implements Serializable, Comparable<ShoppingCartProduct> {

    private Product mProduct;
    private int mQuantity;
    private ItemType mItemType;
    private String mEnteredPrice;

    public ShoppingCartProduct(Product product, int quantity, ItemType itemType) {
        mProduct = product;
        mQuantity = quantity;
        mItemType = itemType;
    }

    public Product getProduct() {
        return mProduct;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public ItemType getItemType() {
        return mItemType;
    }

    public void setItemType(ItemType itemType) {
        mItemType = itemType;
    }


    public String getEnteredPrice() {
        return mEnteredPrice;
    }

    public void setEnteredPrice(String enteredPrice) {
        mEnteredPrice = enteredPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShoppingCartProduct)) return false;

        ShoppingCartProduct that = (ShoppingCartProduct) o;

        return mProduct.equals(that.mProduct);

    }

    @Override
    public int hashCode() {
        return mProduct.hashCode();
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
    public int compareTo(ShoppingCartProduct another) {
        return mProduct.compareTo(another.getProduct());
    }
}
