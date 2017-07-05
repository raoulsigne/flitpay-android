package tecafrix.work.com.flitpay_android;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import tecafrix.work.com.flitpay_android.entity.Country;
import tecafrix.work.com.flitpay_android.entity.Operator;
import tecafrix.work.com.flitpay_android.entity.Token;

/**
 * Created by techafrkix0 on 20/06/2017.
 */

public class FlitPay {

    private String publicKey;
    private boolean initialise = false;

    private static String BASE_API = "https://flitpay-api.herokuapp.com/v1";
    private static String COUNTRY_API = "/list-countries";
    private static String OPERATOR_API = "/list-operators-by-phone-code/";
    private static String GENERATE_TOKEN_API = "/initiate-payment";
    private static String VALIDATE_PAYMENT_API = "/validate-payment";
    private static String INITIATE_PAIEMENT_API = "https://flitpay-demostripe.herokuapp.com/flitpay/charge/test?";
    private static String TAG = "Flitpay";

    private HttpURLConnection client;
    private OutputStream outputPost;
    private URL url;
    private Context _context;

    private static FlitPay framework = null;

    private FlitPay(Context ctx) {
        this._context = ctx;
    }

    public void setPublicKey(String key) {
        publicKey = key;
    }

    public String getPublicKey() {
        return publicKey;
    }

    /**
     * on implemente le pattern singleton pour n'avoir a tout moment qu'une seule instance de cette classe
     * dans l'application
     * @param ctx
     * @return
     */
    public static FlitPay getInstance(Context ctx) {
        if (framework != null)
            return framework;
        else{
            framework = new FlitPay(ctx);
            return framework;
        }

    }

    /**
     * function to initialise the sdk, by giving coupe keys public/private
     * @param publicKey
     */
    public void init_sdk(String publicKey){
        this.publicKey = publicKey;
        this.initialise = true;
    }

    /**
     * fonction qui permete d'avoir la liste des pays contenu dans le système flitpay
     * @return retourne une liste d'bjects
     */
    public void getCountry(final apiListener listener){

        new Thread() {
            @Override
            public void run() {
                try {
                    url = new URL(BASE_API + COUNTRY_API);
                    Log.i(TAG, url.toString());
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("GET");

                    StringBuilder builder = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    br.close();
                    try {
                        JSONObject json = new JSONObject(builder.toString());
                        Log.i(TAG, json.toString());
                        boolean statut = Boolean.valueOf(json.getBoolean("err"));
                        if (!statut) {
                            JSONArray jArr = json.getJSONArray("data");
                            Country country = new Country();
                            ArrayList<Country> listes = new ArrayList<Country>();
                            for (int i = 0; i < jArr.length(); i++) {
                                JSONObject json2 = jArr.getJSONObject(i);
                                country = new Country(json2.getInt("phone_code"), json2.getString("label"), json2.getString("short_label"));
                                listes.add(country);
                            }
                            listener.onSuccess(listes);
                        } else {
                            listener.onError(json);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException " + e.getMessage());
                        listener.onError(null);
                    }
                } catch (MalformedURLException error) {
                    Log.e(TAG, "MalformedURLException " + error.getMessage());
                    listener.onError(null);
                } catch (SocketTimeoutException error) {
                    Log.e(TAG, "SocketTimeoutException " + error.getMessage());
                    listener.onError(null);
                } catch (IOException error) {
                    Log.e(TAG, "IOException " + error.toString());
                    listener.onError(null);
                } finally {
                    client.disconnect();
                }
            }
        }.start();
    }

    /**
     * fonction qui retourne la liste des opérateurs téléphonique d'un pays associé à flitpay
     * @param phone_code phone code of the choosen country
     * @return liste des opérateurs
     */
    public void getPhoneOperator(final int phone_code, final apiListener listener){
        new Thread() {
            @Override
            public void run() {

                try {
                    url = new URL(BASE_API + OPERATOR_API + phone_code);
                    Log.i("url", url.toString());
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("GET");

                    StringBuilder builder = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String line;
                    while ((line = br.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    br.close();
                    try {
                        JSONObject json = new JSONObject(builder.toString());
                        boolean statut = Boolean.valueOf(json.getBoolean("err"));
                        if (!statut) {
                            JSONArray jArr = json.getJSONArray("data");
                            Operator operator;
                            ArrayList<Operator> listes = new ArrayList<Operator>();
                            for (int i = 0; i < jArr.length(); i++) {
                                JSONObject json2 = jArr.getJSONObject(i);
                                operator = new Operator(json2.getInt("phone_operator_id"), json2.getString("name"));
                                listes.add(operator);
                            }
                            listener.onSuccess(listes);
                        } else {
                            listener.onError(json);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException " + e.getMessage());
                        listener.onError(null);
                    }
                } catch (MalformedURLException error) {
                    Log.e(TAG, "MalformedURLException " + error.getMessage());
                    listener.onError(null);
                } catch (SocketTimeoutException error) {
                    Log.e(TAG, "SocketTimeoutException " + error.getMessage());
                    listener.onError(null);
                } catch (IOException error) {
                    Log.e(TAG, "IOException " + error.toString());
                    listener.onError(null);
                } finally {
                    client.disconnect();
                }
            }
        }.start();
    }

    /**
     * generate token paiement
     * @param phone_number user phone number
     * @param email user email
     * @param phone_code phone code of the country
     * @param phone_operator_id phone operator id on which the phone number belongs
     * @param amount amount of the transaction
     * @param listener
     */
    public void generate_token(String phone_number, String email, int phone_code, int phone_operator_id,
                                  int amount, final apiListener listener){
        try {
            url = new URL(BASE_API + GENERATE_TOKEN_API);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);

            OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputPost, "UTF-8"));

            ContentValues values = new ContentValues();
            values.put("phone_number", phone_number);
            values.put("email", email);
            values.put("phone_code", phone_code);
            values.put("phone_operator_id", phone_operator_id);
            values.put("amount", amount);
            values.put("public_key", this.getPublicKey());

            writer.write(getQuery(values));
            writer.flush();
            writer.close();
            outputPost.close();
            Log.i(TAG, url.toString());

            StringBuilder builder = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line + "\n");
            }
            br.close();
            Log.i(TAG, "reponse = " + builder.toString());

            try {
                JSONObject json = new JSONObject(builder.toString());
                boolean statut = Boolean.valueOf(json.getBoolean("err"));
                if (!statut) {
                    JSONObject json2 = json.getJSONObject("data");
                    Token token = new Token(json2.getString("phone_number"), json2.getString("token"), json2.getInt("amount"));
                    listener.onSuccess(token);
                } else {
                    listener.onError(json);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onError(null);
            }

        } catch (MalformedURLException error) {
            //Handles an incorrectly entered URL
            Log.e(TAG, "MalformedURLException " + error.getMessage());
            listener.onError(null);
        } catch (SocketTimeoutException error) {
            //Handles URL access timeout.
            Log.e(TAG, "SocketTimeoutException " + error.getMessage());
            listener.onError(null);
        } catch (IOException error) {
            //Handles flitpay_input and output errors
            Log.e(TAG, "IOException " + error.toString());
            listener.onError(null);
        } finally {
            client.disconnect();
        }
    }

    /**
     * initiate paiement from backend
     * @param token token du paiement
     * @param amount montant du paiement
     * @param phone_number numero du client
     * @param listener
     */
    public void initiate_paiement(String token, int amount, String phone_number, final apiListener listener){
        try {
            url = new URL(INITIATE_PAIEMENT_API + "public_key=" + this.getPublicKey() +
                    "&token=" + token + "&amount=" + amount + "&phone_number=" + phone_number);
            Log.i("url", url.toString());
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");

            StringBuilder builder = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line + "\n");
            }
            br.close();
            Log.i(TAG, "reponse = " + builder.toString());

            try {
                JSONObject json = new JSONObject(builder.toString());
                JSONObject json2 = json.getJSONObject("body");
                boolean statut = Boolean.valueOf(json2.getBoolean("err"));
                if (!statut) {
                    listener.onResult(true);
                } else {
                    listener.onError(json2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onError(null);
            }

        } catch (MalformedURLException error) {
            //Handles an incorrectly entered URL
            Log.e(TAG, "MalformedURLException " + error.getMessage());
            listener.onError(null);
        } catch (SocketTimeoutException error) {
            //Handles URL access timeout.
            Log.e(TAG, "SocketTimeoutException " + error.getMessage());
            listener.onError(null);
        } catch (IOException error) {
            //Handles flitpay_input and output errors
            Log.e(TAG, "IOException " + error.toString());
            listener.onError(null);
        } finally {
            client.disconnect();
        }
    }

    /**
     * payement validation
     * @param token token concerning the previous initiate payement
     * @param reference_number reference number receive after mobile money transaction
     */
    public void validate_payement(String token, String reference_number, final apiListener listener){
        try {
            url = new URL(BASE_API + VALIDATE_PAYMENT_API);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);

            OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputPost, "UTF-8"));

            ContentValues values = new ContentValues();
            values.put("token", token);
            values.put("reference_number", reference_number);

            writer.write(getQuery(values));
            writer.flush();
            writer.close();
            outputPost.close();
            Log.i(TAG, url.toString());

            StringBuilder builder = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line + "\n");
            }
            br.close();
            Log.i(TAG, "reponse = " + builder.toString());

            try {
                JSONObject json = new JSONObject(builder.toString());
                JSONObject json2 = json.getJSONObject("data");
                boolean statut = Boolean.valueOf(json.getBoolean("err"));
                if (!statut) {
                    listener.onSuccess(json2);
                } else {
                    listener.onError(json2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onError(null);
            }

        } catch (MalformedURLException error) {
            //Handles an incorrectly entered URL
            Log.e(TAG, "MalformedURLException " + error.getMessage());
            listener.onError(null);
        } catch (SocketTimeoutException error) {
            //Handles URL access timeout.
            Log.e(TAG, "SocketTimeoutException " + error.getMessage());
            listener.onError(null);
        } catch (IOException error) {
            //Handles flitpay_input and output errors
            Log.e(TAG, "IOException " + error.toString());
            listener.onError(null);
        } finally {
            client.disconnect();
        }
    }

    /**
     * fonction qui construit la chaine devant être passée à une requête de type GET
     *
     * @param params un ensemble de couple clé-valeur de string
     * @return retourne une chaine de caractère pour être passé à la requète
     * @throws UnsupportedEncodingException
     */
    private String getQuery(ContentValues params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Log.i("test", params.toString());
        for (Map.Entry<String, Object> entry : params.valueSet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }

        return result.toString();
    }

    /**
     * interface des callback à implementer chez le client pour interpreter les différents événements
     * survenus lors de l'excécution des requêtes
     */
    public interface apiListener{
        public void onSuccess(Object object);
        public void onSuccess(ArrayList<?> list);
        public void onError(JSONObject json);
        public void onResult(boolean bool);
    }
}
