package com.bedessee.sales.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bedessee.sales.R;
import com.bedessee.sales.main.MainActivity;
import com.bedessee.sales.product.Product;
import com.bedessee.sales.store.StoreManager;
import com.bedessee.sales.utilities.Utilities;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import timber.log.Timber;

import static com.bedessee.sales.customview.DialogNumberPad.DEFAULT_INITIAL_VALUE;

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
        addView(parentView = LayoutInflater.from(context).inflate(R.layout.view_qty_selector, null));

        final EditText editText = findViewById(R.id.editText);

        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fragmentManager = Utilities.getFragmentManager(v.getContext());

                if (StoreManager.isStoreSelected()) {
                    DialogNumberPad.Companion.newInstance(new DialogNumberPad.OnItemSelectedListener() {
                        @Override
                        public void onSelected(@NotNull ItemType itemType, int qty) {
                            Utilities.updateShoppingCart(TAG, context, mProduct, qty, null, itemType, null, null);
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
