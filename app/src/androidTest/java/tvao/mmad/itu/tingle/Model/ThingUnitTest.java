package tvao.mmad.itu.tingle.Model;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;

/**
 * Test that information related to an item is stored and retrieved correctly.
 */
public class ThingUnitTest extends TestCase {

    public void testToString_ValidItems_ReturnsCorrectFormat() throws Exception
    {
        // Arrange
        Thing thing = new Thing("Key", "Home");
        String expected = "Item: Key \nLocation: Home";

        // Act
        String actual = thing.toString();

        // Assert
        Assert.assertEquals(expected, actual);
    }

    public void testOneLine_CorrectFormat_IsTrue() throws Exception
    {
        // Arrange
        Thing thing = new Thing("Key", "Home");
        String expected = "What:Key Where:Home";

        // Act
        String actual = thing.oneLine("What:", "Where:");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    public void testGetWhat_ValidName_ReturnsName() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("Key", "Home");
        String expected = "Key";

        // Act
        String actual = thing.getWhat();

        // Assert
        Assert.assertEquals(expected,actual);
    }

    public void testGetWhat_IsEmpty_ReturnsEmptyName() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("", "Home");
        String expected = "";

        // Act
        String actual = thing.getWhat();

        // Assert
        Assert.assertEquals(expected,actual);
        Assert.assertTrue(actual.isEmpty());
    }

    public void testSetWhat_NewName_IsSet() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("Key", "Home");
        String expected = "NewKey";

        // Act
        thing.setWhat("NewKey");
        String actual = thing.getWhat();

        // Assert
        Assert.assertEquals(expected,actual);
    }

    public void testGetWhere_ValidLocation_ReturnsLocation() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("Key", "Home");
        String expected = "Home";

        // Act
        String actual = thing.getWhere();

        // Assert
        Assert.assertEquals(expected,actual);
    }

    public void testGetWhere_EmptyLocation_IsEmpty() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("Key", "");
        String expected = "";

        // Act
        String actual = thing.getWhere();

        // Assert
        Assert.assertEquals(expected,actual);
        Assert.assertTrue(actual.isEmpty());
    }

    public void testSetWhere_NewName_IsSet() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("Key", "Home");
        String expected = "NewHome";

        // Act
        thing.setWhere("NewHome");
        String actual = thing.getWhere();

        // Assert
        Assert.assertEquals(expected,actual);
    }

    public void testGetId_ValidItem_ReturnsRandomId() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("Key", "Home");

        // Act
        String id = thing.getId().toString();

        // Assert
        Assert.assertTrue(!id.isEmpty());
    }

    public void testGetBarcode_ItemWithBarCode_ReturnsBarcode() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("Key", "Home", "013000006408");
        String expected = "013000006408";

        // Act
        String actual = thing.getBarcode();

        // Assert
        Assert.assertEquals(expected,actual);
    }

    public void testGetBarcode_ItemWithoutBarCode_ReturnsEmptyBarCode() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("Key", "Home");
        String expected = "";

        // Act
        String actual = thing.getBarcode();

        // Assert
        Assert.assertEquals(expected,actual);
        Assert.assertTrue(actual.isEmpty());
    }


    public void testSetBarcode_NewBarCode_IsSet() throws Exception
    {
        // Arrange
        Thing thing = new Thing ("Key", "Home", "013000006408");
        String expected = "013000001212";

        // Act
        thing.setBarcode("013000001212");
        String actual = thing.getBarcode();

        // Assert
        Assert.assertEquals(expected,actual);
    }

    public void testGetPhotoFilename_CorrectFormat_IsReturned() throws Exception
    {
        // Arrange
        Thing thing = new Thing();
        String id = thing.getId().toString();
        String expected = "IMG_" + id + ".jpg";

        // Act
        String actual = "IMG_" + thing.getId() + ".jpg";

        // Assert
        Assert.assertEquals(expected, actual);
    }

    public void testGetDate_DefaultItem_CurrentDateReturned() throws Exception
    {
        // Arrange
        Thing thing = new Thing();
        Date expected = Calendar.getInstance().getTime(); // Current time

        // Act
        Date actual = thing.getDate();

        // Assert
        Assert.assertEquals(expected, actual);
    }

    public void testGetDate_SpecificItem_SpecificDateReturned() throws Exception
    {
        // Arrange
        Thing thing = new Thing();
        Date expected = new Date(1337, 12, 12); // Current time

        // Act
        thing.setDate(new Date(1337, 12, 12));
        Date actual = thing.getDate();

        // Assert
        Assert.assertEquals(expected, actual);
    }

    public void testSetDate_NewDate_IsSet() throws Exception
    {
        // Arrange
        Thing thing = new Thing();
        Date newDate = new Date(1337, 12, 12); // Current time

        // Act
        thing.setDate(newDate);
        Date actual = thing.getDate();

        // Assert
        Assert.assertEquals(newDate, actual);
    }
}