package vak.rentalmanagement;

import org.junit.Before;
import org.junit.Test;

import vak.rentalmanagement.data.Unit;

import static org.junit.Assert.*;

public class UnitTest {
    private Unit unitOne;
    private Unit unitOneCopy;
    private Unit unitTwo;

    @Before
    public void setUp() {
        unitOne = new Unit();
        unitOneCopy = new Unit();
        unitTwo = new Unit();
        unitOne.setName("a");
        unitOneCopy.setName("a");
        unitOneCopy.setUnitId("1");
        unitTwo.setName("b");
        unitOne.setUnitId("1");
        unitTwo.setUnitId("2");

    }

    @Test
    public void equals() {
        assertEquals(unitOne, unitOneCopy);
        assertNotEquals(unitOne, unitTwo);
    }

    @Test
    public void compareTo() {
        assertTrue(unitOne.compareTo(unitTwo) < 0);
        assertTrue(unitTwo.compareTo(unitOne) > 0);
        assertEquals(0, unitOne.compareTo(unitOneCopy));
    }
}