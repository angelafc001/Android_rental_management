package vak.rentalmanagement.datamanager;

/**
 * Helps manage room and issue data for repair requests.
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class RepairRequestExpert {

    // Request Status'

    /**
     * Viewed request
     */
    public static final int STATUS_SENT = 0;
    /**
     * Viewed request
     */
    public static final int STATUS_VIEWED = 1;
    /**
     * Approved request
     */
    public static final int STATUS_APPROVED = 2;
    /**
     * In progress status
     */
    public static final int STATUS_IN_PROGRESS = 3;
    /**
     * Finished status
     */
    public static final int STATUS_FINISHED = 4;
    public static final String[] statusList = {"Sent", "Viewed", "Approved", "In Progress", "Finished"};

    // Request priorities
    /**
     * The Level one.
     */
    public static final int PRIORITY_ONE = 0;
    /**
     * The Level two.
     */
    public static final int PRIORITY_TWO = 1;
    /**
     * The Level three.
     */
    public static final int PRIORITY_THREE = 2;
    public static final String[] priorityList = {"!", "!!", "!!!"};

    // Rooms
    public static final int ROOM_KITCHEN = 0;
    public static final int ROOM_LIVING_ROOM = 1;
    public static final int ROOM_BEDROOM = 2;
    public static final int ROOM_BATHROOM = 3;
    public static final int ROOM_OTHER = 4;
    private static final String[] roomList = {"Kitchen",
            "Living Room",
            "Bedroom",
            "Bathroom",
            "Other"};

    // Issues

    // Kitchen Issues
    private static final String[] ISSUES_KITCHEN = {"Leaky Faucet ",
            "Clogged sink drain",
            "Other"};

    private static final String[] ISSUES_BATHROOM = {"Leak ",
            "Clogged shower drain",
            "Clogged bathroom sink drain",
            "Other"};

    private static final String[] ISSUES_BEDROOM = {"Broken AC/Heater",
            "Other"};

    private static final String[] ISSUES_LIVING_ROOM = {"Broken AC/Heater",
            "Other"};

    private static final String[] ISSUES_OTHER = {"Other"};

    /**
     * Returns an array of all rooms for repair requests.
     * @return String[] - an array of all rooms.
     */
    public static String[] getRoomList() {
        return roomList;
    }

    /**
     * Returns an array of all issues possible for a given room number.
     *
     * @param room the type of room the issue is for.
     * @return String[] the possible issues for that room.
     */
    public static String[] getIssueList(int room) {
        switch (room) {
            case 0: {
                return ISSUES_KITCHEN;
            }
            case 1: {
                return ISSUES_LIVING_ROOM;
            }
            case 2: {
                return ISSUES_BEDROOM;
            }
            case 3: {
                return ISSUES_BATHROOM;
            }
            default: {
                return ISSUES_OTHER;
            }
        }
    }
}
