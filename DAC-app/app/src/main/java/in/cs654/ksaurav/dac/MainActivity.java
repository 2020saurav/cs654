/**
 * The following work is done as an assignment in Software Architecture course (http://cs654.in).
 * It implements a dummy arithmetic calculator which takes input and passes it to a web-service to
 * process and send back the result (or error).
 *
 * @author Saurav Kumar 2020saurav@gmail.com
 */
package in.cs654.ksaurav.dac;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    private        TextView tvInput;
    private        TextView tvResult;
    private static Button btnAdd, btnSubtract, btnMultiply, btnDivide;
    private static Button btnZero, btnOne, btnTwo, btnThree, btnFour;
    private static Button btnFive, btnSix, btnSeven, btnEight, btnNine;
    private static Button btnBackSpace, btnClear, btnEquals, btnDecimal;
    private static String EMPTY = "";

    /**
     * @param savedInstanceState saved instance state
     * This function initializes the entities like buttons and textviews and sets listeners on them
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = (TextView) findViewById(R.id.txtResult);
        tvInput  = (TextView) findViewById(R.id.txtInput);
        tvResult.setText(EMPTY);
        tvInput.setText(EMPTY);
        initializeButtons();
        setListenersOnButton();
    }

    /**
     * Helper function to set on-click listeners to all the buttons
     */
    private void setListenersOnButton() {
        Button[] buttons = new Button[] {btnAdd, btnSubtract, btnMultiply, btnDivide, btnZero,
                btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine,
                btnBackSpace, btnClear, btnEquals, btnDecimal};
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
    }

    /**
     * Helper function to link the UI entities with corresponding objects
     */
    private void initializeButtons() {
        btnAdd       = (Button)   findViewById(R.id.btnAdd);
        btnSubtract  = (Button)   findViewById(R.id.btnSubtract);
        btnMultiply  = (Button)   findViewById(R.id.btnMultiply);
        btnDivide    = (Button)   findViewById(R.id.btnDivide);
        btnBackSpace = (Button)   findViewById(R.id.btnBackSpace);
        btnClear     = (Button)   findViewById(R.id.btnClear);
        btnEquals    = (Button)   findViewById(R.id.btnEquals);
        btnDecimal   = (Button)   findViewById(R.id.btnDecimal);
        btnZero      = (Button)   findViewById(R.id.btnZero);
        btnOne       = (Button)   findViewById(R.id.btnOne);
        btnTwo       = (Button)   findViewById(R.id.btnTwo);
        btnThree     = (Button)   findViewById(R.id.btnThree);
        btnFour      = (Button)   findViewById(R.id.btnFour);
        btnFive      = (Button)   findViewById(R.id.btnFive);
        btnSix       = (Button)   findViewById(R.id.btnSix);
        btnSeven     = (Button)   findViewById(R.id.btnSeven);
        btnEight     = (Button)   findViewById(R.id.btnEight);
        btnNine      = (Button)   findViewById(R.id.btnNine);
    }

    /**
     * @param v View object
     * This function gets the present content of input box and processes it according to the button
     * clicked in the current action. Few checks are done in order to ensure sane input
     */
    @Override
    public void onClick(View v) {
        Editable input = new SpannableStringBuilder(tvInput.getText());
        switch (v.getId()) {

            // In case of a digit, directly append to the input
            case R.id.btnZero:
                input = input.append(btnZero.getText());
                break;
            case R.id.btnOne:
                input = input.append(btnOne.getText());
                break;
            case R.id.btnTwo:
                input = input.append(btnTwo.getText());
                break;
            case R.id.btnThree:
                input = input.append(btnThree.getText());
                break;
            case R.id.btnFour:
                input = input.append(btnFour.getText());
                break;
            case R.id.btnFive:
                input = input.append(btnFive.getText());
                break;
            case R.id.btnSix:
                input = input.append(btnSix.getText());
                break;
            case R.id.btnSeven:
                input = input.append(btnSeven.getText());
                break;
            case R.id.btnEight:
                input = input.append(btnEight.getText());
                break;
            case R.id.btnNine:
                input = input.append(btnNine.getText());
                break;

            // In case of arithmetic operators
            case R.id.btnAdd:
                // TODO check prev is a num
                input = input.append(btnAdd.getText());
                break;
            case R.id.btnSubtract:
                // TODO check prev is a num or mult or div
                input = input.append(btnSubtract.getText());
                break;
            case R.id.btnMultiply:
                // TODO check prev is a num
                input = input.append(btnMultiply.getText());
                break;
            case R.id.btnDivide:
                // TODO check prev is a num
                input = input.append(btnDivide.getText());
                break;

            // In case of decimal point
            case R.id.btnDecimal:
                // TODO check no decimal used before in that num
                input = input.append(btnDecimal.getText());
                break;

            // In case of backspace, delete the last character.
            case R.id.btnBackSpace:
                // TODO check for empty etc
                break;

            // In case of clear, clear input and result
            case R.id.btnClear:
                // TODO checks?
                input = input.delete(0, input.length());
                tvResult.setText(EMPTY);
                break;

            // In case of equal, send input for evaluation
            case R.id.btnEquals:
                // TODO all actions come here
                tvResult.setText("42");
                break;
        }
        tvInput.setText(input);
    }
}
