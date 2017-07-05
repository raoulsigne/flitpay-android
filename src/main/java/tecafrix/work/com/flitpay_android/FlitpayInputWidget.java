package tecafrix.work.com.flitpay_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import tecafrix.work.com.flitpay_android.entity.Country;
import tecafrix.work.com.flitpay_android.entity.Operator;
import tecafrix.work.com.flitpay_android.entity.Token;

/**
 * Created by techafrkix0 on 22/06/2017.
 */

public class FlitpayInputWidget extends LinearLayout  implements View.OnClickListener{

    private LinearLayout blocpays, blocoperateur, blocedition, layout;
    private Spinner spnpays;
    private TextView txtcode;
    private EditText edtnumber, edtemail;
    private Button btnpayer;
    private ProgressDialog ringProgressDialog;
    private Context _context;

    ArrayList<String> pays;
    ArrayList<Country> countries;
    FlitPay framework;
    ArrayList<Operator> operators;
    boolean initialize = false;
    Country selectedcountry;
    Operator selectedOperator;

    public FlitpayInputWidget(Context context) {
        super(context);
        initialize(context);
        traitement();
    }

    public FlitpayInputWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
        traitement();
    }

    private Context get_context(){
        return this._context;
    }

    public Country getSelectedcountry() {
        return selectedcountry;
    }

    public Operator getSelectedOperator() {
        return selectedOperator;
    }

    private void initialize(final Context context){
        this._context = context;
        inflate(context, R.layout.flitpay_input, this);

        pays = new ArrayList<>();
        countries = new ArrayList<>();
        operators = new ArrayList<>();

        //initializing components
        blocpays = (LinearLayout) findViewById(R.id.blocPays);
        blocoperateur = (LinearLayout) findViewById(R.id.blocOperateur);
        blocedition = (LinearLayout) findViewById(R.id.blocInfos);
        layout = (LinearLayout) findViewById(R.id.layout);
        spnpays = (Spinner) findViewById(R.id.spnPays);
        txtcode = (TextView) findViewById(R.id.txtCode);
        edtnumber = (EditText) findViewById(R.id.edtNumber);
        edtemail = (EditText) findViewById(R.id.edtEmail);
        btnpayer = (Button) findViewById(R.id.btnPayer);

        blocoperateur.setVisibility(INVISIBLE);
        blocedition.setVisibility(INVISIBLE);
        btnpayer.setVisibility(INVISIBLE);

        ringProgressDialog = ProgressDialog.show(((Activity)context), "Please wait ...", "Initialization ...", true);

        framework = FlitPay.getInstance(this._context);

        new Thread() {
            @Override
            public void run() {
                initialize = true;
                Log.i("paiement", "initialisation réussie");
                framework.getCountry(new FlitPay.apiListener() {
                    @Override
                    public void onSuccess(Object object) {
                        ringProgressDialog.dismiss();
                    }

                    @Override
                    public void onSuccess(ArrayList<?> list) {
                        countries = (ArrayList<Country>) list;
                        for (Country c : countries) {
                            pays.add(c.getName());
                        }
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<String> pays_adapter = new ArrayAdapter<String>(context,
                                        R.layout.spinner_item, pays);
                                spnpays.setAdapter(pays_adapter);
                                ringProgressDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onResult(boolean bool){

                    }

                    @Override
                    public void onError(JSONObject json) {
                        ringProgressDialog.dismiss();
                    }
                });
            }
        }.start();
    }

    private void traitement(){

        //final ProgressDialog dialog = ProgressDialog.show(((Activity)this._context), "Please wait", "Getting phone operators ...", true);
        spnpays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                selectedcountry = countries.get(i);
                txtcode.setText(selectedcountry.getPhone_code()+"");
                new Thread() {
                    @Override
                    public void run() {
                        framework.getPhoneOperator(countries.get(i).getPhone_code(), new FlitPay.apiListener() {
                            @Override
                            public void onSuccess(Object object) {
                                //dialog.dismiss();
                            }

                            @Override
                            public void onSuccess(final ArrayList<?> list) {

                                ((Activity)get_context()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(((LinearLayout) layout).getChildCount() > 0)
                                            ((LinearLayout) layout).removeAllViews();

                                        operators = (ArrayList<Operator>) list;
                                        for (Operator o : operators) {
                                            Button btnTag = new Button(get_context());
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                                            params.setMargins(10, 0, 10, 0);
                                            btnTag.setLayoutParams(params);
                                            btnTag.setText(o.getName());
                                            btnTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                            btnTag.setId(o.getId());
                                            btnTag.setBackgroundDrawable(get_context().getResources().getDrawable(R.drawable.button_border_blue));
                                            btnTag.setTextColor(get_context().getResources().getColor(R.color.myblue));
                                            layout.addView(btnTag);
                                            ((Button) findViewById(o.getId())).setOnClickListener(FlitpayInputWidget.this);
                                        }
                                        blocoperateur.setVisibility(VISIBLE);
                                        //dialog.dismiss();

                                    }
                                });
                            }

                            @Override
                            public void onResult(boolean bool){

                            }

                            @Override
                            public void onError(JSONObject json) {
                                //dialog.dismiss();
                            }
                        });

                    }
                }.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        Button selected = (Button) view;
        selected.setBackgroundDrawable(get_context().getResources().getDrawable(R.drawable.button_blue));
        selected.setTextColor(get_context().getResources().getColor(R.color.white));

        for (Operator o : operators) {
            if (o.getId() == view.getId()){
                blocedition.setVisibility(VISIBLE);
                btnpayer.setVisibility(VISIBLE);
                selectedOperator = o;
                break;
            }
        }

        int count = layout.getChildCount();
        for (int i = 0; i < count; i++) {
            Button other = (Button) layout.getChildAt(i);
            if (other.getId() != selectedOperator.getId()){
                other.setBackgroundDrawable(get_context().getResources().getDrawable(R.drawable.button_border_blue));
                other.setTextColor(get_context().getResources().getColor(R.color.myblue));
            }
        }
    }
}
