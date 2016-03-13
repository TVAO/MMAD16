package tvao.mmad.itu.tingle.Database;

import android.database.Cursor;
import android.database.CursorWrapper;
import java.util.UUID;
import tvao.mmad.itu.tingle.Database.ThingDbSchema.ThingTable;
import tvao.mmad.itu.tingle.Model.Thing;

/**
 * This class is used to wrap a Cursor (raw columns from query) from another place and add new methods on top of it.
 * The class creates a wrapper around a Cursor with the same methods as Cursor allowing us to extend methods further on raw columns.
 */
public class ThingCursorWrapper extends CursorWrapper {

    public ThingCursorWrapper(Cursor cursor)
    {
    super(cursor);
    }

    public Thing getThing()
    {
        // Get columns
        String uuidString = getString(getColumnIndex(ThingTable.Cols.UUID));
        String what = getString(getColumnIndex(ThingTable.Cols.WHAT));
        String where = getString(getColumnIndex(ThingTable.Cols.WHERE));

        // Find item and set data
        Thing thing = new Thing(UUID.fromString(uuidString));
        thing.setWhat(what);
        thing.setWhere(where);

        return thing;
    }

}