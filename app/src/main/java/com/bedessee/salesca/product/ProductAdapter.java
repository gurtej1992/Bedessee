package com.bedessee.salesca.product;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bedessee.salesca.R;
import com.bedessee.salesca.customview.ItemType;
import com.bedessee.salesca.customview.QtySelector;
import com.bedessee.salesca.mixpanel.MixPanelManager;
import com.bedessee.salesca.provider.Contract;
import com.bedessee.salesca.provider.ProviderUtils;
import com.bedessee.salesca.sharedprefs.SharedPrefsManager;
import com.bedessee.salesca.store.StoreManager;
import com.bedessee.salesca.utilities.BitmapWorkerTask;
import com.bedessee.salesca.utilities.FieldUtilities;
import com.bedessee.salesca.utilities.ProductEnteredFrom;
import com.bedessee.salesca.utilities.Utilities;

public class ProductAdapter extends CursorAdapter implements Filterable {

    private static final String TAG = "ProductAdapter";

    boolean isBusy = false;
    private Context mContext;
    private int width = 0;
    private int height = 0;
    private boolean showUom;
    private boolean showType;
    private Listener listener;


    public ProductAdapter(final Context context, int[] screenDimens) {
        super(context, null, false);
        mContext = context;
        final SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        showUom = sharedPrefsManager.getShowUomInProductsGrid();
        showType = sharedPrefsManager.getShowTypeInProductsGrid();

        updateDimens(screenDimens);
    }

    private void updateDimens(int[] dimens){
        width = dimens[0] / 12;
        height = dimens[0] / 12;
    }

    public void setListener(ProductAdapter.Listener listener){
        this.listener = listener;
    }

    interface Listener {
        void onClick(Product product);
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        FilterQueryProvider filter = getFilterQueryProvider();
        if (filter != null) {
            return filter.runQuery(constraint);
        }
        return mContext.getContentResolver().query(Contract.Product.CONTENT_URI, null, constraint.toString(), null, Contract.ProductColumns.COLUMN_BRAND + " ASC");
    }


    @Override
    public void changeCursor(Cursor cursor) {
        swapCursor(cursor);
    }


    @Override
    public Product getItem(int position) {
        final Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return ProviderUtils.cursorToProduct(cursor);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view;
        ViewHolder viewHolder = new ViewHolder();

        view = LayoutInflater.from(context).inflate(R.layout.product_grid_item, parent, false);

        viewHolder.mItemBody = view.findViewById(R.id.item_body);
        viewHolder.mImageView = view.findViewById(R.id.product_image);
        viewHolder.mTextViewBrand = view.findViewById(R.id.textView_brand);
        viewHolder.mTextUom = view.findViewById(R.id.textView_uom);
        viewHolder.mTextUomUnit = view.findViewById(R.id.textView_unit_uom);
        viewHolder.mTextPriceUnit = view.findViewById(R.id.textView_unit_price);
        viewHolder.mBtnAddToCart = view.findViewById(R.id.btnAddToCart);
        viewHolder.mQtySelector = view.findViewById(R.id.list_item_qty_selector);
        viewHolder.mQtySelector.showType(showType);

        viewHolder.mTextUomUnit.setVisibility(showUom ? View.VISIBLE : View.GONE);
        viewHolder.mTextPriceUnit.setVisibility(showUom ? View.VISIBLE : View.GONE);

        viewHolder.add = view.findViewById(R.id.add);
        viewHolder.product_type = view.findViewById(R.id.product_type);


        view.setTag(viewHolder);

        return view;
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        final Product product = ProviderUtils.cursorToProduct(cursor);
        final ViewHolder holder = (ViewHolder) view.getTag();

        final String filePath = product.getImagePath();

        holder.mImageView.setImageBitmap(null);

        if (!isBusy) {

            View.OnClickListener quickListener = v -> {
                if (listener != null) {
                    listener.onClick(product);
                }
                MixPanelManager.trackButtonClick(context, "Button Click: view product");
            };
            holder.mImageView.setOnClickListener(quickListener);
            holder.mTextViewBrand.setOnClickListener(quickListener);

            BitmapWorkerTask task = new BitmapWorkerTask(mContext, holder.mImageView, new int[]{width, height});
            Log.d("ProductAdapter", "downloading " + filePath);
            task.execute(filePath);
            holder.mImageView.setLongClickable(true);
            holder.mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utilities.shareImage(context, "file://" + filePath);
                    return true;
                }
            });
        } else {
            holder.mImageView.setImageBitmap(null);
        }

        holder.mTextViewBrand.setText(product.getBrand() + "\n" + product.getDescription());
        holder.mTextUomUnit.setText(product.getPieceUom());
        holder.mTextPriceUnit.setText(product.getPiecePrice());
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mQtySelector.setVisibility(View.VISIBLE);
            }
        });

        Integer priceColor = Utilities.parseSaveColor(product.getLPriceColor());
        if (priceColor != null) {
            holder.mTextPriceUnit.setTextColor(priceColor);
            holder.mTextUomUnit.setTextColor(priceColor);
            holder.product_type.setBackgroundColor(priceColor);
        }

        Integer priceBackgroundColor = Utilities.parseSaveColor(product.getLPriceBackgroundColor());
        if (priceBackgroundColor != null) {
            holder.mTextPriceUnit.setBackgroundColor(priceBackgroundColor);
            holder.mTextUomUnit.setBackgroundColor(priceBackgroundColor);
            holder.product_type.setBackgroundColor(priceBackgroundColor);

        }

        FieldUtilities.Companion.setupField(
                holder.mTextUom,
                context.getString(R.string.field_string_formatter, product.getCaseUom()),
                context.getString(R.string.quantity_string_formatter, product.getCasePrice()),
                product.getLPriceColor(),
                product.getLPriceBackgroundColor()
        );

        if (!isBusy) {

            holder.mBtnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StoreManager.isStoreSelected()) {

                        MixPanelManager.trackButtonClick(context, "Button Click: Add to cart");
                        if (product.getPopUpPriceFlag().equalsIgnoreCase("Y")) {

                            AlertDialog.Builder qtyBuilder = new AlertDialog.Builder(context);
                            final View view = LayoutInflater.from(context).inflate(R.layout.dialog_product_qty, null, false);
                            final TextView price = view.findViewById(R.id.edt_price);
                            price.setText(product.getPopUpPrice());

                            view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(null);
                                }
                            });

                            view.findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "1");
                                }
                            });

                            view.findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "2");
                                }
                            });

                            view.findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "3");
                                }
                            });

                            view.findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "4");
                                }
                            });

                            view.findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "5");
                                }
                            });

                            view.findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "6");
                                }
                            });

                            view.findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "7");
                                }
                            });

                            view.findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "8");
                                }
                            });

                            view.findViewById(R.id.btn9).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "9");
                                }
                            });

                            view.findViewById(R.id.btn0).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + "0");
                                }
                            });

                            view.findViewById(R.id.btnComma).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    price.setText(price.getText() + ".");
                                }
                            });

                            qtyBuilder.setView(view);

                            final AlertDialog dialog = qtyBuilder.create();

                            dialog.show();

                            view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog.hide();

                                    String newPrice = ((TextView) view.findViewById(R.id.edt_price)).getText().toString();

                                    if (holder.mQtySelector.getSelectedQty() > 0) {
                                        try {
                                            if (Double.parseDouble(newPrice) < Double.parseDouble(product.getPriceRangeFrom())) {
                                                Toast.makeText(mContext, "Minimum price is " + product.getPriceRangeFrom(), Toast.LENGTH_LONG).show();
                                            } else if (Double.parseDouble(newPrice) > Double.parseDouble(product.getPriceRangeTo())) {
                                                Toast.makeText(mContext, "Maximum price is " + product.getPriceRangeTo(), Toast.LENGTH_LONG).show();
                                            } else {
                                                Utilities.updateShoppingCart(TAG, mContext, product, holder.mQtySelector.getSelectedQty(), newPrice, holder.mQtySelector.getItemType(), ProductEnteredFrom.PRODUCT_LIST, new Utilities.OnProductUpdatedListener() {
                                                    @Override
                                                    public void onUpdated(int qty, ItemType itemType) {
                                                        holder.mQtySelector.setQty(0);
                                                        holder.mQtySelector.invalidate();
                                                    }
                                                });
                                            }
                                        } catch (NumberFormatException e) {
                                            Utilities.updateShoppingCart(TAG, mContext, product, holder.mQtySelector.getSelectedQty(), newPrice, holder.mQtySelector.getItemType(), ProductEnteredFrom.PRODUCT_LIST, new Utilities.OnProductUpdatedListener() {
                                                @Override
                                                public void onUpdated(int qty, ItemType itemType) {
                                                    holder.mQtySelector.setQty(0);
                                                    holder.mQtySelector.invalidate();
                                                }
                                            });
                                        }

                                    } else {
                                        Toast.makeText(mContext, "Please provide quantity before adding a product.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        } else {

                            if (holder.mQtySelector.getSelectedQty() > 0) {
                                Utilities.updateShoppingCart(TAG, mContext, product, holder.mQtySelector.getSelectedQty(), null, holder.mQtySelector.getItemType(), ProductEnteredFrom.PRODUCT_LIST, new Utilities.OnProductUpdatedListener() {
                                    @Override
                                    public void onUpdated(int qty, ItemType itemType) {
                                        holder.mQtySelector.setQty(0);
                                        holder.mQtySelector.invalidate();
                                    }

                                });

                            } else {
                                Toast.makeText(mContext, "Please provide quantity before adding a product.", Toast.LENGTH_SHORT).show();
                            }

                        }

                    } else {
                        Toast.makeText(mContext, "Please select store to continue.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.mItemBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        ProductDetailDialog.Companion.create(product);
                }
            });


            QtySelector.QtySelectorClickListener qtySelectorClickListener = new QtySelector.QtySelectorClickListener() {
                @Override
                public View.OnClickListener onPlusButtonClick() {
                    return new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (StoreManager.isStoreSelected()) {
                                holder.mQtySelector.incrementQty();
                                holder.mBtnAddToCart.performClick();
                            } else {
                                Toast.makeText(mContext, "Please select store to continue.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                }

                @Override
                public View.OnClickListener onMinusButtonClick() {
                    return new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (StoreManager.isStoreSelected()) {
                                holder.mQtySelector.decrementQty();
                            } else {
                                Toast.makeText(mContext, "Please select store to continue.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                }
            };

            holder.mQtySelector.setQtySelectorClickListener(qtySelectorClickListener);

            holder.mQtySelector.setQty(0);
            holder.mQtySelector.setProduct(product);
        }
    }


    static class ViewHolder {
        RelativeLayout mItemBody;
        ImageView mImageView,add;
        TextView mTextViewBrand;
        TextView mTextUom;
        TextView mTextUomUnit;
        TextView mTextPriceUnit;
        Button mBtnAddToCart;
        View product_type;
        QtySelector mQtySelector;
    }

}