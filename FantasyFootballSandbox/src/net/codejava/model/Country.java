package net.codejava.model;

/**
 * Entity to represent a Country object
 *
 * @author wwww.codejava.net
 */
public class Country {

    private String name;
    private String code;

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    @Override
    public String toString() { 
        return name; 
    }    
}
