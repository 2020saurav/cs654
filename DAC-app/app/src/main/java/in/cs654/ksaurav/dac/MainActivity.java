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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements OnClickListener {

    private        TextView tvInput;
    private        TextView tvResult;
    private static Button btnAdd, btnSubtract, btnMultiply, btnDivide;
    private static Button btnZero, btnOne, btnTwo, btnThree, btnFour;
    private static Button btnFive, btnSix, btnSeven, btnEight, btnNine;
    private static Button btnBackSpace, btnClear, btnEquals, btnDecimal;
    public static final String EMPTY = "";
    public static final String DAC_API_URL = "http://172.24.1.62/dac-server/api.php";
    public static final String BAD_INPUT_MSG = "Bad Input";
    public static final String NETWORK_ERROR_MSG = "Network Error";
    public static final String UTF8 = "utf-8";
    public static final String REQ_KEY = "input";

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
        final Button[] buttons = new Button[] {btnAdd, btnSubtract, btnMultiply, btnDivide, btnZero,
                btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine,
                btnBackSpace, btnClear, btnEquals, btnDecimal};
        for (final Button button : buttons) {
            button.setOnClickListener(this);
        }
    }

    /**
     * Helper function to link the UI entities with corresponding objects
     */
    private void initializeButtons() {
        btnAdd       = (Button) findViewById(R.id.btnAdd);
        btnSubtract  = (Button) findViewById(R.id.btnSubtract);
        btnMultiply  = (Button) findViewById(R.id.btnMultiply);
        btnDivide    = (Button) findViewById(R.id.btnDivide);
        btnBackSpace = (Button) findViewById(R.id.btnBackSpace);
        btnClear     = (Button) findViewById(R.id.btnClear);
        btnEquals    = (Button) findViewById(R.id.btnEquals);
        btnDecimal   = (Button) findViewById(R.id.btnDecimal);
        btnZero      = (Button) findViewById(R.id.btnZero);
        btnOne       = (Button) findViewById(R.id.btnOne);
        btnTwo       = (Button) findViewById(R.id.btnTwo);
        btnThree     = (Button) findViewById(R.id.btnThree);
        btnFour      = (Button) findViewById(R.id.btnFour);
        btnFive      = (Button) findViewById(R.id.btnFive);
        btnSix       = (Button) findViewById(R.id.btnSix);
        btnSeven     = (Button) findViewById(R.id.btnSeven);
        btnEight     = (Button) findViewById(R.id.btnEight);
        btnNine      = (Button) findViewById(R.id.btnNine);
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
                if (matchPreviousCharacter(input.toString(), "[0-9]")) {
                    input = input.append(btnAdd.getText());
                }
                break;
            case R.id.btnSubtract:
                if (matchPreviousCharacter(input.toString(), "[0-9|×|÷]")) {
                    input = input.append(btnSubtract.getText());
                }
                break;
            case R.id.btnMultiply:
                if (matchPreviousCharacter(input.toString(), "[0-9]")) {
                    input = input.append(btnMultiply.getText());
                }
                break;
            case R.id.btnDivide:
                if (matchPreviousCharacter(input.toString(), "[0-9]")) {
                    input = input.append(btnDivide.getText());
                }
                break;

            // In case of decimal point
            case R.id.btnDecimal:
                if (!isDecimalPresentInLastNumber(input.toString())) {
                    input = input.append(btnDecimal.getText());
                }
                break;

            // In case of backspace, delete the last character.
            case R.id.btnBackSpace:
                final int len = input.toString().length();
                if (len > 0) {
                    input.delete(len-1, len);
                }
                break;

            // In case of clear, clear input and result
            case R.id.btnClear:
                input = input.delete(0, input.length());
                tvResult.setText(EMPTY);
                break;

            // In case of equal, send input for evaluation
            case R.id.btnEquals:
                final String inputString = input.toString().replace('×','*').replace('÷', '/');
                processRequest(inputString);
                break;
        }
        tvInput.setText(input);
    }

    /**
     * @param string given string expression
     * @return true if decimal point is present in the last number
     * This function first finds out the last occurence of any operator, which gives index to the
     * last number in the expression. In that expression, it searches for any occuring decimal point
     */
    private boolean isDecimalPresentInLastNumber(String string) {
        int lastOperatorIndex = Math.max(
                Math.max(string.lastIndexOf("+"), string.lastIndexOf("-")),
                Math.max(string.lastIndexOf("×"), string.lastIndexOf("÷"))
        );
        if (lastOperatorIndex == -1) {
            return string.contains(".");
        } else {
            return string.substring(lastOperatorIndex).contains(".");
        }
    }

    /**
     * @param string given string
     * @param regex regular expression to match with
     * @return true if last character of the string matches the regex, false otherwise
     */
    private boolean matchPreviousCharacter(String string, String regex) {
        return string.length() > 0 && string.substring(string.length() - 1).matches(regex);
    }

    /**
     * @param inputString request string to be sent to DAC API for evaluation
     * This function posts the payload string for evaluation and receives the result and sets it in
     * the result textview.
     * It uses Volley for faster and efficient network communications.
     */
    private void processRequest(final String inputString) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, DAC_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        tvResult.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvResult.setText(NETWORK_ERROR_MSG);
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                final Map<String, String> params = new HashMap<>();
                try {
                    final String expression = URLEncoder.encode(inputString, UTF8);
                    params.put(REQ_KEY, expression);
                } catch (UnsupportedEncodingException e) {
                    tvResult.setText(BAD_INPUT_MSG);
                }
                return params;
            }
        };
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
