package com.bedessee.sales.product.category;

/**
 * Created by yusufsaber on 2014-04-17.
 */
public enum Category {

    ACHAR_CONDIMENTS("Achar & Condiments", 'A'),
    BEVERAGES_SYRUPS_SNACK("Beverages, Syrups, Snack", 'B'),
    CANNED_FOODS_MILK_PRODUCTS("Canned Foods & Milk Products", 'C'),
    FROZEN_FISH_FROZEN_VEGETABLES("Frozen Fish & Frozen Vegetables", 'F'),
    HEALTHY_CARE_MEDICINES("Healthy Care, Medicines", 'H'),
    KITCHEN_WARE_HARDWARE("Kitchen Ware & Hardware", 'K'),
    NOODLES_BAKING_PRODUCTS("Noodles & Baking Products", 'N'),
    QUICK_FROZEN_PRODUCTS("Quick Frozen Products", 'Q'),
    RELIGIOUS_BRASS_WARES("Religious & Brass Wares", 'R'),
    SPICES_CURRY_POWDERS("Spices & Curry Powders", 'S'),
    VEGETABLES_OILS_GHEE_PRODUCTS("Vegetable Oils & Ghee Products", 'V'),
    X("X", 'X');


    private String mName;
    private char mChar;

    private Category(String name, char catChar) {
        mName = name;
        mChar = catChar;
    }

    public String getName() {
        return mName;
    }

    public char getChar() { return mChar; }

    public static Category getCategoryFromChar(char catChar) {
        for (Category category : values()) {
            if(catChar == category.getChar()) {
                return category;
            }
        }
        return null;
    }

    public static String[] getCategoryStrings() {
        String[] strings = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            strings[i] = values()[i].getName();
        }
        return strings;
    }

}
