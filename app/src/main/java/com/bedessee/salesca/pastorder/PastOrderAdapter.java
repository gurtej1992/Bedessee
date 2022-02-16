package com.bedessee.salesca.pastorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bedessee.salesca.R;
import com.bedessee.salesca.customview.DialogNumberPad;
import com.bedessee.salesca.customview.ItemType;
import com.bedessee.salesca.order.PastOrderQuantity;
import com.bedessee.salesca.utilities.ProductEnteredFrom;
import com.bedessee.salesca.utilities.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.bedessee.salesca.customview.DialogNumberPad.DEFAULT_INITIAL_VALUE;

public class PastOrderAdapter extends RecyclerView.Adapter<PastOrderAdapter.ViewHolder> {

    private static final String TAG = "BackOrderAdapter";

    private FragmentManager fragmentManager;
    private List<PastOrderQuantity> mPastOrders;
    private QuantityChangedListener mQuantityListener = null;

    // Pass in the contact array into the constructor
    public PastOrderAdapter(List<PastOrderQuantity> contacts, FragmentManager fragmentManager) {
        mPastOrders = contacts;
        this.fragmentManager = fragmentManager;
    }

    public void setQuantityChangedListener(QuantityChangedListener listener) {
        mQuantityListener = listener;
    }

    @NotNull
    @Override
    public PastOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.shopping_cart_list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final PastOrderAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final PastOrderQuantity order = mPastOrders.get(position);
        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        final Context context = textView.getContext();
        textView.setText(order.getProduct().getBrand());
        viewHolder.totalQtySold.setText(textView.getContext().getString(R.string.total_qty_sold_message, order.getProduct().getTotalQtySold()));
        String text = order.getProduct().getDescription() + " " + order.getProduct().getPieceUom();
        viewHolder.descriptionTextView.setText(text+"\n"+order.getProduct().getOtherInfo() +"\n"+order.getProduct().getOtherInfo2());
        viewHolder.removeView.setVisibility(View.GONE);
        viewHolder.edtQty.setText("0");
        viewHolder.textView_totalCase.setText("$"+order.getProduct().getCasePrice());
        viewHolder.edtQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNumberPad.Companion.newInstance(new DialogNumberPad.OnItemSelectedListener() {
                    @Override
                    public void onSelected(@NotNull ItemType itemType, int qty) {
                        String qtyString = String.format("%s", qty);
                        viewHolder.edtQty.setText(qtyString);
                        switch (itemType) {
                            case CASE:
                                viewHolder.radioCase.setChecked(true);
                                viewHolder.radioPiece.setChecked(false);
                                break;
                            case NONE:
                                viewHolder.radioCase.setChecked(false);
                                viewHolder.radioPiece.setChecked(false);
                                break;
                            case PIECE:
                                viewHolder.radioCase.setChecked(false);
                                viewHolder.radioPiece.setChecked(true);
                                break;
                        }
                        order.setQuantity(qty);
                        order.setType(itemType);
                        if (mQuantityListener != null) {
                            mQuantityListener.onChanged(order);
                        }
                        Utilities.updateShoppingCart(TAG, context, order.getProduct(), qty, null, itemType, ProductEnteredFrom.PAST_ORDER, null);
                    }
                }, new DialogNumberPad.DefaultNumberPad(ItemType.CASE, "0"), DEFAULT_INITIAL_VALUE).show(fragmentManager, TAG);
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPastOrders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        TextView nameTextView;
        TextView descriptionTextView;
        TextView totalQtySold;
        View removeView;
        EditText edtQty;
        RadioButton radioCase;
        RadioButton radioPiece;
        EditText textView_totalCase;
        TextView textView_totalFull;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = itemView.findViewById(R.id.textView_brand);
            descriptionTextView = itemView.findViewById(R.id.textView_description);
            removeView = itemView.findViewById(R.id.btnRemoveItem);
            edtQty = itemView.findViewById(R.id.edtQty);
            radioCase = itemView.findViewById(R.id.radioCase);
            radioPiece = itemView.findViewById(R.id.radioPiece);
            totalQtySold = itemView.findViewById(R.id.textView_totalQtySold);
            textView_totalCase = itemView.findViewById(R.id.textView_totalCase);
            textView_totalFull = itemView.findViewById(R.id.textView_totalFull);

        }
    }
}