package vak.rentalmanagement.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;

 /**
 * This class defines a Landlord who owns one or more Units.
 *  * It saves an ArrayList of Units and all of their contact info.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class Landlord {
    private String landlordId;
    private String firstName;
    private String lastName;
    private String photo;
    private String email;
    private String phone;
    private ArrayList<String> unitIds = new ArrayList<>();

    /**
     * Default constructor
     */
    public Landlord() {

    }

    /**
     * Creates a new Landlord with all of its properties except for the ArrayList of Units.
     *
     * @param firstName   the Landlord's first name
     * @param lastName    the Landlord's last name
     * @param photo       the Landlord's photo
     * @param email       the Landlord's email address
     * @param phone       the Landlord's phone number
     */
    public Landlord(String firstName, String lastName, String photo, String email,
                    String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.email = email;
        this.phone = phone;
        this.unitIds = new ArrayList<>();
    }

    /**
     * Creates a new Landlord with all of its properties including the ArrayList of Units.
     *
     * @param firstName   the Landlord's first name
     * @param lastName    the Landlord's last name
     * @param photo       the Landlord's photo
     * @param email       the Landlord's email address
     * @param phone       the Landlord's phone number
     * @param unitIds       the Units owned by the Landlord.
     */
    public Landlord(String firstName, String lastName, String photo, String email,
                    String phone, ArrayList<String> unitIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.email = email;
        this.phone = phone;
        this.unitIds = unitIds;
    }

    /**
     * Creates a new Landlord with all of its properties including the ArrayList of Units.
     *
     * @param firstName   the Landlord's first name
     * @param lastName    the Landlord's last name
     * @param email       the Landlord's email address
     * @param phone       the Landlord's phone number
     */
    public Landlord(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Gets Landlord's ID
     *
     * @return the landlord id
     */
    public String getLandlordId() {
        return landlordId;
    }

    /**
     * Sets the landlordId.
     */
    public void setLandlordId(String id) {
        this.landlordId = id;
    }

    /**
     * Sets Landlord's ID
     *
     * @param id the new Landlord Id
     */
    public void getLandlordId(String id) {
        this.landlordId = id;
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the Landlord's photo.
     *
     * @return the Landlord's photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Gets the Landlord's email.
     *
     * @return the Landlord's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the Landlord's phone number.
     *
     * @return the Landlord's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the Landlord's photo.
     *
     * @param photo the Landlord's photo
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Sets the Landlord's email address.
     *
     * @param email the the Landlord's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the Landlord's phone number.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the Landlord's unitIds.
     *
     * @return the unitIds
     */
    public ArrayList<String> getUnitIds() {
        return unitIds;
    }

    /**
     * Sets the Landlord's unitIds.
     *
     * @param unitIds the unitIds
     */
    public void setUnitIds(ArrayList<String> unitIds) {
        this.unitIds = unitIds;
    }

    /**
     * Returns true if both Landlord's Ids are the same.
     *
     * @param o the second Landlord to compare it to.
     * @return true if the Landlord's IDs are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Landlord landlord = (Landlord) o;
        return landlordId.equals(landlord.landlordId);
    }

    /**
     * Generates a hash code for the Landlord from its ID.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        return landlordId.hashCode();
    }

    /**
     * Returns the first and last name of the landlord.
     * @return the first and last name of the landlord.
     */
    @NonNull
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}
