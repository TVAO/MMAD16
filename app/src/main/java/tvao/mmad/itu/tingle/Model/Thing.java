package tvao.mmad.itu.tingle.Model;

import java.util.UUID;

/**
 * The Thing class represents an item stored in the application.
 */
public class Thing {

    private String mWhat, mWhere = null;
    private UUID mId;

    public Thing(String what, String where)
    {
        mWhat = what;
        mWhere = where;
        mId = UUID.randomUUID(); // Generate unique identifier
    }

    // Return Thing with appropriate id in query
    public Thing(UUID id)
    {
        mId = id;
    }

    /**
     * Uses oneLine helper function to build description of item.
     * @return - description of item and location
     */
    @Override
    public String toString() { return oneLine("Item: ", "\nLocation: "); }

    /**
     * Helper function used to describe what the item is and where it is located.
     * @param pre - item
     * @param post - location
     * @return description of item
     */
    public String oneLine(String pre, String post)
    {
        return pre + mWhat + " " + post + mWhere;
    }

    // Getters and setters for fields

    public String getWhat() { return mWhat; }

    public void setWhat(String what) { mWhat = what; }

    public String getWhere() { return mWhere; }

    public void setWhere(String where) { mWhere = where; }

    public UUID getId() { return mId; }
}