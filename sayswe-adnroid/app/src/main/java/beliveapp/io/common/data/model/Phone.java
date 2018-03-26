package beliveapp.io.common.data.model;

/**
 * Created by phungkhactuan on 3/26/18.
 */

public class Phone {

    private String name;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Phone(String name, String number) {
        this.name = name;
        this.number = number;
    }
}
