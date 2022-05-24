package com.bedessee.salesca.customview;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bedessee.salesca.R;
import com.bedessee.salesca.utilities.Utilities;
import com.bedessee.salesca.utilities.ViewUtilities;

import java.text.DecimalFormat;

import static com.bedessee.salesca.customview.DialogNumberPad.DEFAULT_INITIAL_VALUE;

/**
 * Markup Calculator Activity
 */
public class MarginCalculator extends AppCompatActivity {

    private static final String TAG = "MarginCalculator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.markup_calculator);
        SharedPreferences sh = getSharedPreferences("setting", Context.MODE_PRIVATE);
       String orient= sh.getString("orientation","landscape");
        if(orient.equals("landscape")){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        final EditText costEdt = findViewById(R.id.editText_costprice);
        final EditText sellEdt = findViewById(R.id.editText_sellingprice);
        final EditText markupEdt = findViewById(R.id.editText_markup);

        final TextView resultSell = findViewById(R.id.resultSell);
        final TextView resultCost = findViewById(R.id.resultCost);
        final TextView resultMarkup = findViewById(R.id.resultMarkup);
        final TextView resultProfit = findViewById(R.id.resultProfit);

        final DecimalFormat format = new DecimalFormat("0.00");

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View.OnClickListener qtyClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNumberPad.Companion.newInstance(
                        new MarginOnNumberSelectedListener((EditText) v), false, true, false, DEFAULT_INITIAL_VALUE
                ).show(getSupportFragmentManager(), TAG);
            }
        };

        costEdt.setOnClickListener(qtyClickListener);
        sellEdt.setOnClickListener(qtyClickListener);
        markupEdt.setOnClickListener(qtyClickListener);

        findViewById(R.id.btnCalculate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String costStr = costEdt.getText().toString();
                final String sellStr = sellEdt.getText().toString();
                final String markupStr = markupEdt.getText().toString();


                if (!costStr.isEmpty() && Double.parseDouble(costStr) > 0
                        && !sellStr.isEmpty() && Double.parseDouble(sellStr) > 0
                        && (markupStr.isEmpty() || Double.parseDouble(markupStr) == 0)) {
                    final double cost = Double.parseDouble(costStr);
                    final double sell = Double.parseDouble(sellStr);
                    final double markup = (1 - (cost / sell)) * 100;

                    resultCost.setText("Cost Price: $" + format.format(cost));
                    resultSell.setText("Sell Price: $" + format.format(sell));
                    resultMarkup.setText("Margin: " + format.format(markup) + "%");
                    resultProfit.setText("Profit: $" + format.format(sell - cost));

                } else if (!costStr.isEmpty() && Double.parseDouble(costStr) > 0
                        && (sellStr.isEmpty() || Double.parseDouble(sellStr) == 0)
                        && !markupStr.isEmpty() && Double.parseDouble(markupStr) > 0) {
                    final double cost = Double.parseDouble(costStr);
                    final double markup = Double.parseDouble(markupStr);
                    final double sell = cost / (1 - (markup / 100));

                    resultCost.setText("Cost Price: $" + format.format(cost));
                    resultSell.setText("Sell Price: $" + format.format(sell));
                    resultMarkup.setText("Margin: " + format.format(markup) + "%");
                    resultProfit.setText("Profit: $" + format.format(sell - cost));

                } else if ((costStr.isEmpty() || Double.parseDouble(costStr) == 0)
                        && !sellStr.isEmpty() && Double.parseDouble(sellStr) > 0
                        && !markupStr.isEmpty() && Double.parseDouble(markupStr) > 0) {
                    final double sell = Double.parseDouble(sellStr);
                    final double markup = Double.parseDouble(markupStr);
                    final double cost = sell * (1 - (markup / 100));

                    resultCost.setText("Cost Price: $" + format.format(cost));
                    resultSell.setText("Sell Price: $" + format.format(sell));
                    resultMarkup.setText("Margin: " + format.format(markup) + "%");
                    resultProfit.setText("Profit: $" + format.format(sell - cost));

                } else {
                    Toast.makeText(MarginCalculator.this, "Please enter 2 values. Not more, not less.", Toast.LENGTH_LONG).show();
                }

                resultProfit.requestFocus();

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                Utilities.hideSoftKeyboard(MarginCalculator.this);
            }
        });

        findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                costEdt.setText(null);
                sellEdt.setText(null);
                markupEdt.setText(null);
                resultCost.setText("Cost Price:");
                resultSell.setText("Sell Price:");
                resultMarkup.setText("Markup:");
                resultProfit.setText("Profit: ");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewUtilities.Companion.setDialogWindowSize(getWindow(), true);
    }

    private class MarginOnNumberSelectedListener implements DialogNumberPad.OnNumberSelectedListener {
        final DecimalFormat format = new DecimalFormat("0.00");
        private EditText mEditText;

        private MarginOnNumberSelectedListener(EditText editText) {
            mEditText = editText;
        }

        @Override
        public void onSelected(double number) {
            mEditText.setText(format.format(number));
        }
    }
}
