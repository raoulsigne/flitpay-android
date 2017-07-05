package tecafrix.work.com.flitpay_android.entity;

import java.io.Serializable;

/**
 * Created by techafrkix0 on 24/04/2017.
 */

public class Country implements Serializable {

    private int phone_code;
    private String name;
    private String short_name;

    public Country(){
    }

    public Country(int phone_code, String name, String short_name){
        this.phone_code = phone_code;
        this.name = name;
        this.short_name = short_name;
    }

    public int getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(int phone_code) {
        this.phone_code = phone_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    @Override
    public String toString() {
        return "Country{" +
                "phone_code=" + phone_code +
                ", name='" + name + '\'' +
                ", short_name='" + short_name + '\'' +
                '}';
    }
}
