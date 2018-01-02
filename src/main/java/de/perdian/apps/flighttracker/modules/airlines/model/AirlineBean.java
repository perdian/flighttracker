package de.perdian.apps.flighttracker.modules.airlines.model;

import java.io.Serializable;

public class AirlineBean implements Serializable {

    static final long serialVersionUID = 1L;

    private String name = null;
    private String code = null;
    private String countryCode = null;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[name=").append(this.getName());
        result.append(",code=").append(this.getCode());
        result.append(",countryCode=").append(this.getCountryCode());
        return result.append("]").toString();
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getCountryCode() {
        return this.countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
