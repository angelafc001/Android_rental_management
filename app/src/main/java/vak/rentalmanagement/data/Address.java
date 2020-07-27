package vak.rentalmanagement.data;

/**
 * Defines an Address type
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class Address {
    private String firstLine;
    private String secondLine;
    private String city;
    private String state;
    private int zipCode;

    /**
     * Default constructor
     */
    public Address() {

    }

    /**
     * Makes a new Address.
     *
     * @param firstLine  the first line
     * @param secondLine the second line
     * @param city       the city
     * @param state      the state
     * @param zipCode    the zip code
     */
    public Address(String firstLine, String secondLine, String city, String state, int zipCode) {
        this.firstLine = firstLine;
        this.secondLine = secondLine;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    /**
     * Makes a new Address.
     *
     * @param firstLine the first line
     * @param city      the city
     * @param state     the state
     * @param zipCode   the zip code
     */
    public Address(String firstLine, String city, String state, int zipCode) {
        this.firstLine = firstLine;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    /**
     * Gets first line.
     *
     * @return the first line
     */
    public String getFirstLine() {
        return firstLine;
    }

    /**
     * Sets first line.
     *
     * @param firstLine the first line
     */
    public void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    /**
     * Gets second line.
     *
     * @return the second line
     */
    public String getSecondLine() {
        return secondLine;
    }

    /**
     * Sets second line.
     *
     * @param secondLine the second line
     */
    public void setSecondLine(String secondLine) {
        this.secondLine = secondLine;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets zip code.
     *
     * @return the zip code
     */
    public int getZipCode() {
        return zipCode;
    }

    /**
     * Sets zip code.
     *
     * @param zipCode the zip code
     */
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Prints the address with correct formatting.
     * 
     * @return String - the address formatted correctly.
     */
    @Override
    public String toString() {
        if (secondLine == null) {
            return firstLine + "\n" + city + ", " + state + " " + zipCode;
        } else {
            return firstLine + "\n" + secondLine + "\n" + city + ", " + state + " " + zipCode;
        }
    }
}
