package tecafrix.work.com.flitpay_android.entity;

import java.io.Serializable;

/**
 * Created by techafrkix0 on 24/04/2017.
 */

public class Operator implements Serializable {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Operator(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Operator(){
    }

    @Override
    public String toString() {
        return "Operator{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
