package vak.rentalmanagement.data;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * This class defines a Unit which a landlord can own
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class Unit implements Comparable<Unit>{

    private String unitId;
    /**
     * The constant HOUSE.
     */
    public static final int HOUSE = 0;
    /**
     * The constant CONDO.
     */
    public static final int CONDO = 1;
    /**
     * The constant APARTMENT.
     */
    public static final int APARTMENT = 2;
    public static final String[] TYPES = {"House", "Condo", "Apartment"};
    private String name;
    private Address address;
    private String photo;
    private int rentDueDay;
    private int yearBuilt;
    private double rent;
    private int squareFeet;
    private int beds;
    private double baths;
    private String tenantId;
    private String landlordId;
    private ArrayList<String> requestIds;
    private int type;

    /**
     * Default constructor
     */
    public Unit() {

    }

    /**
     * Instantiates a new Unit.
     *
     * @param name       the name
     * @param address    the address
     * @param photo      the photo
     * @param rentDueDay the rent due day
     * @param yearBuilt  the year built
     * @param rent       the rent
     * @param squareFeet the square feet
     * @param beds       the beds
     * @param baths      the baths
     * @param landlordId the landlordId
     * @param type       the type of unit
     */
    public Unit(String name, Address address, int type, String photo, int rentDueDay, int yearBuilt,
                double rent, int squareFeet, int beds, double baths,
                String landlordId) {
        super();
        this.type = type;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.rentDueDay = rentDueDay;
        this.yearBuilt = yearBuilt;
        this.rent = rent;
        this.squareFeet = squareFeet;
        this.beds = beds;
        this.baths = baths;
        this.landlordId = landlordId;
        this.requestIds = new ArrayList<>();
    }

    /**
     * Instantiates a Unit with tenantID
     *
     * @param name       the name
     * @param address    the address
     * @param photo      the photo
     * @param rentDueDay the rent due day
     * @param yearBuilt  the year built
     * @param rent       the rent
     * @param squareFeet the square feet
     * @param beds       the beds
     * @param baths      the baths
     * @param tenantId   the tenantId
     * @param landlordId the landlordId
     * @param type       the type of unit
     */
    public Unit(String name, Address address, int type, String photo, int rentDueDay, int yearBuilt,
                double rent, int squareFeet, int beds, double baths, String tenantId,
                String landlordId) {
        super();
        this.type = type;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.rentDueDay = rentDueDay;
        this.yearBuilt = yearBuilt;
        this.rent = rent;
        this.squareFeet = squareFeet;
        this.beds = beds;
        this.baths = baths;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.requestIds = new ArrayList<>();
    }

    /**
     * Instantiates a Unit with tenantID, with requestIDs
     *
     * @param name       the name
     * @param address    the address
     * @param photo      the photo
     * @param rentDueDay the rent due day
     * @param yearBuilt  the year built
     * @param rent       the rent
     * @param squareFeet the square feet
     * @param beds       the beds
     * @param baths      the baths
     * @param tenantId     the tenantId
     * @param landlordId   the landlordId
     * @param requestIds the request ids
     * @param type  the unit type
     */
    public Unit(String name, Address address, int type, String photo, int rentDueDay, int yearBuilt,
                double rent, int squareFeet, int beds, double baths, String tenantId,
                String landlordId, ArrayList<String> requestIds) {
        this.type = type;
        this.name = name;
        this.address = address;
        this.photo = photo;
        this.rentDueDay = rentDueDay;
        this.yearBuilt = yearBuilt;
        this.rent = rent;
        this.squareFeet = squareFeet;
        this.beds = beds;
        this.baths = baths;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.requestIds = requestIds;
    }

    /**
     * Instantiates a new Unit with mini
     *
     * @param name       the name
     * @param beds       the beds
     * @param baths      the baths
     * @param landlordId   the landlordId
     */
    public Unit(String name, int beds, double baths, String landlordId) {
        this.name = name;
        this.beds = beds;
        this.baths = baths;
        this.landlordId = landlordId;
    }

    /**
     * Gets unit id.
     *
     * @return the unit id
     */
    public String getUnitId() {
        return unitId;
    }

    /**
     * Sets the unit id.
     *
     * @param id the new ID.
     */
    public void setUnitId(String id) {
        this.unitId = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Returns the type of the unit as an int.
     * @return int - the type of the unit.
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the type of the unit as a String
     * @return String - the type of the unit.
     */
    public String getTypeString() {
        String strType = "No Type";
        switch (type) {
            case HOUSE:
                strType = TYPES[0];
                break;
            case CONDO:
                strType =  TYPES[1];
                break;
            case APARTMENT:
                strType = TYPES[2];
                break;
        }
        return strType;
    }

    /**
     * Sets the type of the unit.
     * @param type the new type of the unit.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets photo download URL.
     *
     * @return the photo
     */
    public String getPhoto() {
        return photo;
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
     * Gets rent due day.
     *
     * @return the rent due day
     */
    public int getRentDueDay() {
        return rentDueDay;
    }

    /**
     * Sets rent due day.
     *
     * @param rentDueDay the rent due day
     */
    public void setRentDueDay(int rentDueDay) {
        this.rentDueDay = rentDueDay;
    }

    /**
     * Gets year built.
     *
     * @return the year built
     */
    public int getYearBuilt() {
        return yearBuilt;
    }

    /**
     * Sets year built.
     *
     * @param yearBuilt the year built
     */
    public void setYearBuilt(int yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    /**
     * Gets rent.
     *
     * @return the rent
     */
    public double getRent() {
        return rent;
    }

    /**
     * Sets rent.
     *
     * @param rent the rent
     */
    public void setRent(double rent) {
        this.rent = rent;
    }

    /**
     * Gets square feet.
     *
     * @return the square feet
     */
    public int getSquareFeet() {
        return squareFeet;
    }

    /**
     * Sets square feet.
     *
     * @param squareFeet the square feet
     */
    public void setSquareFeet(int squareFeet) {
        this.squareFeet = squareFeet;
    }

    /**
     * Gets beds.
     *
     * @return the beds
     */
    public int getBeds() {
        return beds;
    }

    /**
     * Sets beds.
     *
     * @param beds the beds
     */
    public void setBeds(int beds) {
        this.beds = beds;
    }

    /**
     * Gets baths.
     *
     * @return the baths
     */
    public double getBaths() {
        return baths;
    }

    /**
     * Sets baths.
     *
     * @param baths the baths
     */
    public void setBaths(double baths) {
        this.baths = baths;
    }

    /**
     * Gets tenantId.
     *
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * Sets tenantId.
     *
     * @param tenantId the tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Gets landlordId.
     *
     * @return the landlordId
     */
    public String getLandlordId() {
        return landlordId;
    }

    /**
     * Sets landlordId.
     *
     * @param landlordId the landlordId
     */
    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    /**
     * Gets request ids.
     *
     * @return the request ids
     */
    public ArrayList<String> getRequestIds() {
        return requestIds;
    }

    /**
     * Sets request ids.
     *
     * @param requestIds the request ids
     */
    public void setRequestIds(ArrayList<String> requestIds) {
        this.requestIds = requestIds;
    }

    /**
     * Returns true if the two Units have the same ID.
     * @param o the other Unit
     * @return boolean - true if the units have the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return unitId.equals(unit.unitId);
    }

    /**
     * Hashes the units ID.
     * @return int - the hashed unit ID.
     */
    @Override
    public int hashCode() {

        return unitId.hashCode();
    }

    /**
     * Compares two Units by days late.
     *
     * @param other the unit to compare with
     * @return < 0 if this unit comes before the other, > 0 if this unit comes after the other,
     * = 0 if they are in the same spot.
     */
    @Override
    public int compareTo(@NonNull Unit other) {
        return name.compareTo(other.getName());
    }
}
