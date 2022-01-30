package com.bedessee.sales.product.status;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager for product statuses.
 */
public enum Status {

    SOON("ARRIVING SOON"),
    SPEC("SPECIAL"),
    NEW("NEW PRODUCT"),
    LAST("CLEAR OUT"),
    LOW("NEW LOWER PRICE"),
    LOWQ("LOW STOCK ALERT"),
    SOAP("SOAP PRODUCTS"),
    REST("RESTAURANT PROD");

    private String mDescription;

    Status(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }


    public static List<String> allStatusDecriptions() {
        List<String> names = new ArrayList<String>();
        for (int i = 0; i < values().length; i++) {
            names.add(values()[i].getDescription());
        }
        return names;
    }

    public static Status getStatusByDescription(String description) {
        for (Status status : values()) {
            if(description.equals(status.getDescription())) {
                return status;
            }
        }
        return null;
    }

}
