package vak.rentalmanagement;

import org.junit.Before;
import org.junit.Test;

import vak.rentalmanagement.data.Tenant;

import static org.junit.Assert.*;

public class TenantTest {

    private Tenant tenant1;
    private Tenant tenant2;
    private Tenant tenant3;
    private Tenant tenantCopy;
    @Before
    public void setUp() {
        tenant1 = new Tenant();
        tenant1.setTenantId("1231231231");
        tenant1.setFirstName("Tenant1");
        tenant1.setLastName("Last Name");

        tenant2 = new Tenant();
        tenant2.setTenantId("1231231232");
        tenant2.setFirstName("Tenant2");
        tenant2.setLastName("Last Name");

        tenant3 = new Tenant();
        tenant3.setTenantId("1231231233");
        tenant3.setFirstName("Tenant2");
        tenant3.setLastName("Different Last Name");

        tenantCopy = new Tenant();
        tenantCopy.setTenantId("1231231231");
        tenantCopy.setFirstName("Tenant1");
        tenantCopy.setLastName("Last Name");

    }

    @Test
    public void equals() {
        assertNotEquals(tenant3, tenant2);
        assertEquals(tenant1, tenantCopy);
    }

    @Test
    public void compareTo() {
        assertTrue( tenant1.compareTo(tenant2)  < 0);
        assertTrue(tenant2.compareTo(tenant3)>0);
        assertEquals(0, tenant1.compareTo(tenantCopy));
    }

    @Test
    public void testToString() {

        assertEquals("Tenant1 Last Name", tenant1.toString());
        assertNotEquals("Tenant2 Last Name", tenantCopy);
    }
}