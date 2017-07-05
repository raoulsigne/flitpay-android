package tecafrix.work.com.flitpay_android.entity;

/**
 * Created by techafrkix0 on 22/06/2017.
 */

public class Token {

    private String phone_number;
    private String _token;
    private int amount;

    public Token(String phone_number, String _token, int amount) {
        this.phone_number = phone_number;
        this._token = _token;
        this.amount = amount;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String get_token() {
        return _token;
    }

    public void set_token(String _token) {
        this._token = _token;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Token{" +
                "phone_number='" + phone_number + '\'' +
                ", _token='" + _token + '\'' +
                ", amount=" + amount +
                '}';
    }
}
