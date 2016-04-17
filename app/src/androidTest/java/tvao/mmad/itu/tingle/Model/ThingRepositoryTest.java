package tvao.mmad.itu.tingle.Model;

import android.content.Context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test that repository performs CRUD operations correctly using SQLite.
 * Mockito framework is used to mock the context in which the repository is used and thus abstract away the dependency.
 */

public class ThingRepositoryTest {

     private ThingRepository _sut;
     private Context mockContext;

    @Before
    public void setup()
    {
        mockContext = Mockito.mock(Context.class);
        _sut = Mockito.mock(ThingRepository.get(mockContext).getClass());
    }

    @Test
    public void testGetThings_ListWithTwoItems_NotEmpty() throws Exception
    {
        // Arrange
        List<Thing> returnedList = new ArrayList<>();
        returnedList.add(new Thing("Keys", "Home"));
        returnedList.add(new Thing("Book", "School"));
        int expectedSize = 2;
        when(_sut.getThings()).thenReturn(returnedList);

        // Act
        List actualContent = _sut.getThings();
        int actualSize = _sut.getThings().size();

        // Assert
        Assert.assertFalse("The returned collection is empty", actualContent.isEmpty());
        Assert.assertTrue("Should contain two items but returned " + actualSize, actualSize == expectedSize);
    }

    @Test
    public void testGetThings_IsCalled_Once() throws Exception
    {
        // Arrange
        List<Thing> returnedList = new ArrayList<>();
        when(_sut.getThings()).thenReturn(returnedList);

        // Act
        _sut.getThings();

        // Assert
        verify(_sut, atLeastOnce()).getThings();
    }

    @Test
    public void testNewRepository_IsEmpty_True() throws Exception
    {
        // Arrange
        List<Thing> returnedList = new ArrayList<>();
        when(_sut.getThings()).thenReturn(returnedList);
        int expectedSize = 0;

        // Act
        List<Thing> actualItems = _sut.getThings();
        int actualSize = actualItems.size();

        // Assert
        Assert.assertTrue("The returned collection is not empty", actualItems.isEmpty());
        Assert.assertTrue("Should contain 0 items but returned " + actualSize, actualSize == expectedSize);
    }

    @Test
    public void testAddThing_ValidItem_NotNull()
    {
        // Arrange
        Thing validItem = new Thing("name", "location");
        when(_sut.getThing(validItem.getId())).thenReturn(validItem);

        // Act
        _sut.addThing(validItem);

        // Assert
        //verify(_sut, atLeastOnce()).addThing(validItem);
        Assert.assertNotNull("Valid item was not added", _sut.getThing(validItem.getId()));
    }

    @Test
    public void testAddThing_InvalidItem_IsNull()
    {
        // Arrange
        Thing invalidItem = null;
        when(_sut.getThing(invalidItem.getId())).thenReturn(null);

        // Act
        _sut.addThing(invalidItem);

        // Assert
        //verify(_sut, atLeastOnce()).addThing(invalidItem);
        Assert.assertNull("Invalid item was added", _sut.getThing(invalidItem.getId()));
    }

    @Test
    public void testAddThing_IsCalled_Once() throws Exception
    {
        // Arrange
        Thing thing = new Thing();

        // Act
        _sut.addThing(thing);

        // Assert
        verify(_sut, atLeastOnce()).addThing(thing);
    }

    @Test
    public void testGetThings_Returns_AllItems() throws Exception
    {
        // Arrange
        List<Thing> returnedList = new ArrayList<>();
        returnedList.add(new Thing("Keys", "Home"));
        returnedList.add(new Thing("Book", "School"));
        when(_sut.getThings()).thenReturn(returnedList);

        // Act
        List<Thing> actualItems = _sut.getThings();

        // Assert
        Assert.assertTrue("Collection did not contain all returned items ", actualItems.containsAll(returnedList));
    }

    @Test
    public void testGetThing_ItemExists_IsReturned() throws Exception
    {
        // Arrange
        Thing expectedITem = new Thing("Key", "Home");
        when(_sut.getThing(expectedITem.getId())).thenReturn(expectedITem);

        // Act
        Thing actualItem = _sut.getThing(expectedITem.getId());

        // Assert
        Assert.assertEquals("Existing item was not found ", expectedITem, actualItem);
    }

    @Test
    public void testGetThing_ItemDoesNotExist_NotReturned() throws Exception
    {
        // Arrange
        Thing expectedItem = null;
        UUID fakeId = UUID.randomUUID();
        when(_sut.getThing(fakeId)).thenReturn(null);

        // Act
        Thing actualItem = _sut.getThing(fakeId);

        // Assert
        Assert.assertEquals("Item was found without existing ", expectedItem, actualItem);
    }

    @Test
    public void testGetThing_IsCalled_Once() throws Exception
    {
        // Arrange
        Thing thing = new Thing();
        List<Thing> things = new ArrayList<>();
        things.add(thing);
        when(_sut.getThing(thing.getId())).thenReturn(thing);

        // Act
        _sut.getThing(thing.getId());

        // Assert
        verify(_sut, atLeastOnce()).getThing(thing.getId());
    }

    @Test
    public void testGetPhotoFile_FileExists_IsReturned() throws Exception
    {
        // Arrange
        Thing thing = new Thing();
        File expected = new File("Path");
        when(_sut.getPhotoFile(thing)).thenReturn(expected);

        // Act
        File actual = _sut.getPhotoFile(thing);

        // Arrange
        Assert.assertEquals("File was null even though item has photo", expected, actual);
    }

    @Test
    public void testGetPhotoFile_FileNotExists_IsNull() throws Exception
    {
        // Arrange
        Thing thing = new Thing();
        when(_sut.getPhotoFile(thing)).thenReturn(null);

        // Act
        File actual = _sut.getPhotoFile(thing);

        // Arrange
        Assert.assertNull("File was not null even though item does not have photo", actual);

    }

    @Test
    public void testRemoveThing_ItemFound_ReturnsTrue() throws Exception
    {
        // Arrange
        Thing thing = new Thing("Key", "Home");
        when(_sut.removeThing(thing.getId())).thenReturn(true);

        // Act
        boolean actual = _sut.removeThing(thing.getId());

        // Assert
        Assert.assertTrue(actual);
    }

    @Test
    public void testRemoveThing_ItemNotFound_ReturnsFalse() throws Exception
    {
        // Arrange
        Thing thing = new Thing("Key", "Home");
        when(_sut.removeThing(thing.getId())).thenReturn(false);

        // Act
        boolean actual = _sut.removeThing(thing.getId());

        // Assert
        Assert.assertFalse(actual);
    }

    @Test
    public void testRemoveThing_IsCalled_Once() throws Exception
    {
        // Arrange
        Thing thing = new Thing();
        when(_sut.removeThing(thing.getId())).thenReturn(true);

        // Act
        _sut.removeThing(thing.getId());

        // Assert
        verify(_sut, atLeastOnce()).removeThing(thing.getId());
    }

    @Test
    public void testUpdateThing_ItemExists_IsUpdated() throws Exception
    {
        // Arrange
        Thing oldThing = new Thing("Key", "Home");
        oldThing.setWhat("Book");
        oldThing.setWhere("School");
        Thing expected = new Thing("Book", "School");
        when(_sut.getThing(oldThing.getId())).thenReturn(oldThing);

        // Act
        _sut.updateThing(oldThing);
        Thing actual = _sut.getThing(oldThing.getId());

        // Assert
        Assert.assertEquals("Item was not updated", expected, actual);
        Assert.assertTrue(oldThing.getWhat() == actual.getWhat());
        Assert.assertTrue(oldThing.getWhere() == actual.getWhere());
    }

    @Test
    public void testUpdateThing_ItemNotFound_ThrowsException() throws Exception
    {
        // Arrange
        Thing thing = null;
        when(_sut.getThing(thing.getId())).thenReturn(null);
        //when(_sut.updateThing(thing)).thenThrow(new Exception());

        // Act
        _sut.updateThing(thing);
        Thing actual = _sut.getThing(thing.getId());

        // Assert
        Assert.assertNull("Empty item should not be updated or found", actual);
    }

    @Test
    public void testUpdateThing_IsCalled_Once() throws Exception
    {
        // Arrange
        Thing thing = new Thing();

        // Act
        _sut.updateThing(thing);

        // Assert
        verify(_sut, atLeastOnce()).updateThing(thing);
    }

    @Test
    public void testGetDateTimeString_CorrectFormat_IsTrue() throws Exception
    {
        // Arrange
        Date date = new Date(2000, 12, 30, 00, 00, 00);
        String expected = "2000-12-30 00:00:00";

        // Act
        String actualFormat = _sut.getDateTimeString(date);

        // Assert
        Assert.assertTrue("Format was not the same", expected == actualFormat);
    }

    @Test
    public void testGetDateTimeString_WrongFormat_IsFalse() throws Exception
    {
        // Arrange
        Date date = new Date(2000, 12, 30, 00, 00, 00);
        String expected = "2000-12-30";

        // Act
        String actualFormat = _sut.getDateTimeString(date);

        // Assert
        Assert.assertTrue("Format was the same", expected == actualFormat);
    }

    @Test
    public void testSize() throws Exception
    {
        // Arrange
        List<Thing> things = new ArrayList<>();
        things.add(new Thing());
        things.add(new Thing());
        things.add(new Thing());
        int expected = 3;
        when(_sut.size()).thenReturn(things.size());

        // Act
        int actual = _sut.size();

        // Assert
        Assert.assertEquals("Size was not correct", expected, actual);
    }

}