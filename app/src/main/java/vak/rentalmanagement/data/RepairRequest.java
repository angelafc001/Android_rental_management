package vak.rentalmanagement.data;

import androidx.annotation.NonNull;
import vak.rentalmanagement.datamanager.RepairRequestExpert;

/**
 * This class defines a Repair Request for a unit and has a toId(landlord) and fromId(tenant).
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class RepairRequest  implements Comparable<RepairRequest>{

    private String photo;
    private String requestId;
    private String room;
    private String issue;
    private String description;
    private int priority;
    private int status;
    private String fromId;
    private String toId;

    /**
     * Default constructor
     */
    public RepairRequest() {}

    /**
     * Creates a new RepairRequest.
     *
     * @param room        the room
     * @param description the description
     * @param priority    the priority of the request
     * @param fromId      the fromId
     * @param toId        the toId
     */
    public RepairRequest(String room, String issue, String description, int priority, String fromId, String toId) {
        this.room = room;
        this.issue = issue;
        this.description = description;
        this.priority = priority;
        this.fromId = fromId;
        this.toId = toId;
    }

    /**
     * Gets request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the repair ID.
     *
     * @param id the new ID
     */
    public void setRequestId(String id) {
        this.requestId = id;
    }

    /**
     * Gets room.
     *
     * @return the room
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets room.
     *
     * @param room the room
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets photo.
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Sets photo.
     *
     * @param photo the description
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Gets priority.
     *
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets priority.
     *
     * @param priority the priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the priority
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Gets fromId.
     *
     * @return the fromId
     */
    public String getFromId() {
        return fromId;
    }

    /**
     * Gets the issue
     * @return the issue
     */
    public String getIssue() {
        return issue;
    }

    /**
     * Sets the issue
     * @param issue the issue to set
     */
    public void setIssue(String issue) {
        this.issue = issue;
    }

    /**
     * Sets fromId.

     *
     * @param fromId the fromId
     */
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    /**
     * Gets toId.
     *
     * @return the toId
     */
    public String getToId() {
        return toId;
    }

    /**
     * Sets toId.
     *
     * @param toId the toId
     */
    public void setToId(String toId) {
        this.toId = toId;
    }

    /**
     * Returns true if the two requests are equal.
     *
     * @param o the other repair request
     * @return boolean - true if the requests have the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepairRequest repairRequest = (RepairRequest) o;
        return requestId.equals(repairRequest.requestId);
    }

    /**
     * Hashes the requestID.
     * @return int - the hashed requestID.
     */
    @Override
    public int hashCode() {
        return requestId.hashCode();
    }

    /**
     * Returns -1 if this priority is greater than the other requests priority, 1 if this priority
     * is less than the other requests priority, and 0 if they are the same.
     * @param o
     * @return
     */
    @Override
    public int compareTo(RepairRequest o) {
        if (priority > o.priority) return -1;
        else if (priority < o.priority) return 1;
        else return 0;
    }

    /**
     * Prints the priority of the repair request in exclamation marks. !, !! or !!!.
     * @return String - the priority of the repair request.
     */
    @NonNull
    @Override
    public String toString() {
        return RepairRequestExpert.priorityList[priority];
    }
}
