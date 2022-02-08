package com.bedessee.salesca.update.json.deserializer;

import android.content.Context;

import com.bedessee.salesca.product.Product;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;


public class ProductsDeserializer implements JsonDeserializer<Product> {

    private Context mContext;

    public ProductsDeserializer(Context context) {
        mContext = context;
    }

    @Override
    public Product deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final SharedPrefsManager sharedPrefs = new SharedPrefsManager(mContext);
        final String sugarSyncDir = sharedPrefs.getSugarSyncDir();
        final String imagePath = sharedPrefs.getLinkToProdImages();

        try {
            final Product product = new Gson().fromJson(json.toString(), Product.class);
            product.setImagePath(sugarSyncDir + imagePath + product.getNumber() + ".jpg");

            return product;
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

}