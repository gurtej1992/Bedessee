package com.bedessee.salesca.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.bedessee.salesca.R;
import com.bedessee.salesca.product.Product;
import com.bedessee.salesca.store.StoreManager;
import com.bedessee.salesca.utilities.Utilities;

import org.jetbrains.annotations.NotNull;

import static com.bedessee.salesca.customview.DialogNumberPad.DEFAULT_INITIAL_VALUE;

/**
 * Quantity selector for products.
 */
public class QtySelector extends FrameLayout {

    private static final String TAG = "QtySelector";

    private int mQty;
    private View parentView;
    private Product mProduct;

    public QtySelector(Context context) {
        super(context);
        init(context);
    }

    public QtySelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public QtySelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setProduct(Product product) {
        mProduct = product;
    }

    public void setQtySelectorClickListener(QtySelectorClickListener listener) {
        parentView.findViewById(R.id.btnPlus).setOnClickListener(listener.onPlusButtonClick());
        parentView.findViewById(R.id.btnMinus).setOnClickListener(listener.onMinusButtonClick());
    }



    @SuppressLint("InflateParams")
    private void init(final Context context) {
        addView(parentView = LayoutInflater.from(context).inflate(R.layout.qty_selector, null));

        final EditText editText = findViewById(R.id.editText);

        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fragmentManager = Utilities.getFragmentManager(v.getContext());

                if (StoreManager.isStoreSelected()) {
                    DialogNumberPad.Companion.newInstance(new DialogNumberPad.OnItemSelectedListener() {
                        @Override
                        public void onSelected(@NotNull ItemType itemType, int qty) {
                            Utilities.updateShoppingCart("inc",TAG, context, mProduct, qty, null, itemType, null, null);
                        }
                    }, new DialogNumberPad.DefaultNumberPad(ItemType.CASE, "0"), DEFAULT_INITIAL_VALUE).show(fragmentManager, TAG);
                } else {
                    Toast.makeText(context, "Please select store to continue.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public int getSelectedQty() {
        return mQty;
    }

    public void setQty(int qty) {
        ((EditText) parentView.findViewById(R.id.editText)).setText(String.valueOf(mQty = qty));
    }


    public void incrementQty() {
        ((EditText) parentView.findViewById(R.id.editText)).setText(String.valueOf(++mQty));
    }

    public void decrementQty() {
        if(mQty > 0) {
            ((EditText) parentView.findViewById(R.id.editText)).setText(String.valueOf(--mQty));
        }
    }

    public ItemType getItemType() {
        return ((RadioButton)parentView.findViewById(R.id.radioCase)).isChecked() ? ItemType.CASE : ItemType.PIECE;
    }


    public void showType(final boolean show) {
        parentView.findViewById(R.id.radioType).setVisibility(show ? VISIBLE : GONE);
    }


    public interface QtySelectorClickListener {
        public OnClickListener onPlusButtonClick();
        public OnClickListener onMinusButtonClick();
    }
}
