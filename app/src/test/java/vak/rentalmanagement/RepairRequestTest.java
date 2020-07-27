package vak.rentalmanagement;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.datamanager.RepairRequestExpert;

public class RepairRequestTest {

    private RepairRequest request;
    private RepairRequest requestCopy;
    private RepairRequest other;

    @Before
    public void setUp() {
        request = new RepairRequest();
        request.setRequestId("1");
        request.setStatus(RepairRequestExpert.STATUS_VIEWED);
        request.setRoom(RepairRequestExpert.getRoomList()[0]);
        request.setPriority(2);
        request.setIssue(RepairRequestExpert.getIssueList(0)[0]);
        request.setDescription("Request Description");
        request.setFromId("tenantId");
        request.setToId("landlordId");

        requestCopy = new RepairRequest();
        requestCopy.setRequestId("1");
        requestCopy.setStatus(RepairRequestExpert.STATUS_VIEWED);
        requestCopy.setRoom(RepairRequestExpert.getRoomList()[0]);
        requestCopy.setPriority(2);
        requestCopy.setIssue(RepairRequestExpert.getIssueList(0)[0]);
        requestCopy.setDescription("Request Description");
        requestCopy.setFromId("tenantId");
        requestCopy.setToId("landlordId");

        other = new RepairRequest();
        other.setRequestId("2");
        other.setStatus(RepairRequestExpert.STATUS_FINISHED);
        other.setRoom(RepairRequestExpert.getRoomList()[2]);
        other.setPriority(1);
        other.setIssue(RepairRequestExpert.getIssueList(0)[0]);
        other.setDescription("Request 2 Description");
        other.setFromId("tenantId");
        other.setToId("landlordId");

    }

    @Test
    public void equals() {
        assertNotEquals(request, other);
        assertEquals(request, requestCopy);
    }

    @Test
    public void compareTo() {
        assertTrue(request.compareTo(other) < 0);
        assertTrue(other.compareTo(request) > 0);
        assertEquals(0, request.compareTo(requestCopy));
    }

    @Test
    public void testToString() {
        assertEquals("!!", other.toString());
        assertEquals("!!!", request.toString());

    }
}