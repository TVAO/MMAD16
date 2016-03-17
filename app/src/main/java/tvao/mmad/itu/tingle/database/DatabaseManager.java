package tvao.mmad.itu.tingle.Database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This singleton class holds and returns a single SQLiteOpenHelper object.
 * This allows to use database with multiple threads on one database connection.
 * Counter is used to track opening and closing of database connection to avoid using same SQLiteDatabase object for different threads.
 * See href: http://www.dmytrodanylyk.com/concurrent-database-access/ for inspiration...
 * The overall aim with this class is to ensure that communication with the database is thread safe.
 */
public class DatabaseManager {

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static DatabaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    /**
     * This method initializes the DatabaseManager and SQLiteOpenHelper.
     * Should be called before using the database.
     * @param helper - helper class to manage creation of database.
     */
    public static synchronized void initializeInstance(SQLiteOpenHelper helper)
    {
        if (instance == null)
        {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    /**
     * This method gives allows to communicate with the database through a DatabaseManager using different threads.
     * @return - DatabaseManager object.
     */
    public static synchronized DatabaseManager getInstance()
    {
        if (instance == null)
        {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    /**
     * This method opens the connection to the database if the amount of times the database has been opened is equal to one (counter).
     * If equal to one, a new database connection is created.
     * @return
     */
    public synchronized SQLiteDatabase openDatabase()
    {
        if(mOpenCounter.incrementAndGet() == 1)
        {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    /**
     * Every time we call this method, counter is decreased, whenever it goes to zero, we are closing database connection.
     */
    public synchronized void closeDatabase()
    {
        if(mOpenCounter.decrementAndGet() == 0)
        {
            // Closing database
            mDatabase.close();

        }
    }

}
