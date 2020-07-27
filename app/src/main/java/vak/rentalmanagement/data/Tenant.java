package vak.rentalmanagement.data;

import androidx.annotation.NonNull;

/**
 * This class defines a tenant who can belong to a Unit.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class Tenant implements Comparable<Tenant>{
    private String tenantId;
    private String landlordId;
    private String firstName;
    private String lastName;
    private String photo;
    private String email;
    private String phone;

    /**
     * Default constructor for Firebase
     */
    public Tenant() {

    }

    /**
     * Instantiates a new Tenant.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @param photo     the photo
     * @param email     the email
     * @param phone     the phone
     */
    public Tenant(String firstName, String lastName, String photo, String email,
                  String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Instantiates a new Tenant.
     *
     * @param firstName the first name
     * @param lastName  the last name
     * @param photo     the photo
     * @param email     the email
     * @param phone     the phone
     */
    public Tenant(String firstName, String lastName, String photo, String email,String tenantId,
                  String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.email = email;
        this.phone = phone;
        this.tenantId = tenantId;
    }

    /**
     * Gets tenant id.
     *
     * @return the tenant id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets tenant id.
     *
     * @param id the new id
     */
    public void setTenantId(String id) {
        this.tenantId = id;
    }

    /**
     * Gets the tenant's landlord ID
     *
     * @return the tenant's landlord ID
     */
    public String getTenantLandlordId() {
        return landlordId;
    }

    /**
     * Sets the tenant's landlord ID
     */
    public void setTenantLandlordId(String landlordId) {
        this.landlordId = landlordId;
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
     * Gets photo.
     *
     * @return the photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets phone.
     *
     * @return the phone
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
     * Sets photo.
     *
     * @param photo the photo
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns true if both tenantIds are the same.
     *
     * @param o the other Tenant
     * @return boolean - true if both tenants have the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return tenantId.equals(tenant.tenantId);
    }

    /**
     * Hashes the tenants ID.
     * @return int - the hashed tenantId.
     */
    @Override
    public int hashCode() {
        return tenantId.hashCode();
    }

    /**
     * Compares two Tenants alphabetically. If the first names are the same, it will compare from
     * last name.
     *
     * @param other the tenant to compare with
     * @return < 0 if this pet comes before the other, > 0 if this pet comes after the other,
     * = 0 if they are in the same spot.
     */
    @Override
    public int compareTo(@NonNull Tenant other) {
        int firstCompare = firstName.compareTo(other.getFirstName());
        if (firstCompare == 0) {
            return lastName.compareTo(other.getLastName());
        } else {
            return firstCompare;
        }
    }

    /**
     * Returns the first and last name of the tenant.
     * @return the first and last name of the tenant.
     */
    @NonNull
    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
