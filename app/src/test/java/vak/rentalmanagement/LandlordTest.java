package vak.rentalmanagement;

import org.junit.Before;
import org.junit.Test;

import vak.rentalmanagement.data.Landlord;

import static org.junit.Assert.*;

public class LandlordTest {

    private Landlord landlordOne;
    private Landlord landlordTwo;
    private Landlord landlordTwoCopy;

    @Before
    public void setup() {
        landlordOne = new Landlord();
        landlordTwo = new Landlord();
        landlordTwoCopy = new Landlord();
        landlordOne.setFirstName("Koenn");
        landlordOne.setLastName("Becker");
        landlordOne.setLandlordId("1");

        landlordTwo.setFirstName("Becker");
        landlordTwo.setLastName("Koenn");
        landlordTwo.setLandlordId("2");

        landlordTwoCopy.setFirstName("Becker");
        landlordTwoCopy.setLastName("Koenn");
        landlordTwoCopy.setLandlordId("2");
    }

    @Test
    public void equals() {
        assertEquals(landlordTwo, landlordTwoCopy);
        assertNotEquals(landlordOne,landlordTwo);
    }

    @Test
    public void testToString() {
        assertEquals("Koenn Becker", landlordOne.toString());
        assertEquals("Becker Koenn", landlordTwo.toString());
    }
}