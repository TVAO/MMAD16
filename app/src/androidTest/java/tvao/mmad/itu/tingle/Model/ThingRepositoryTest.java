package tvao.mmad.itu.tingle.Model;

import android.content.Context;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test that repository performs CRUD operations correctly using SQLite.
 * Mockito framework is used to mock the context in which the repository is used and thus abstract away the dependency.
 */
public class ThingRepositoryTest extends TestCase {

    ThingRepository _sut;

    @Before
    public void setup()
    {
        Context mockContext = Mockito.mock(Context.class);
        _sut = ThingRepository.get(mockContext);
    }

    @Test
    public void testNewRepository_IsEmpty_True() throws Exception
    {
        // Arrange
        when(_sut.getThings()).thenReturn(Collections.EMPTY_LIST);

        // Act
        List<Thing> initialItems = _sut.getThings();


        // Assert
        Assert.assertTrue(initialItems.isEmpty());
    }

    @Test
    public void testAddThing_ValidItem_NotNull()
    {
        // Arrange
        Thing testData = new Thing("name", "location");

        // Act
        _sut.addThing(testData);

        // Assert
        verify(_sut, atLeastOnce()).addThing(testData);
        //Assert.assertNotNull("Valid item was not added", _sut.getThing(testData.getId()));
    }

    @Test
    public void testAddThing_InvalidItem_IsNull()
    {
        // Arrange
        Thing testData = null;

        // Act
        _sut.addThing(testData);

        // Assert
        Assert.assertNull("Valid item was not added", _sut.getThing(testData.getId()));
    }

    public void testGetThings_Returns_AllItems() throws Exception {

    }

    public void testGetThing_ItemExists_IsReturned() throws Exception {

    }

    public void testGetThing_ItemDoesNotExist_NotReturned() throws Exception {

    }

    public void testGetPhotoFile_CorrectFormat_FileReturned() throws Exception {

    }

    public void testGetPhotoFile_FileExists_IsReturned() throws Exception {

    }

    public void testGetPhotoFile_FileNotExists_IsNull() throws Exception {

    }

    public void testRemoveThing_ItemFound_RemovedIsTrue() throws Exception {

    }

    public void testRemoveThing_ItemNotFound_RemovedIsFalse() throws Exception {

    }

    public void testUpdateThing_ItemExists_IsUpdated() throws Exception {

    }

    public void testUpdateThing_ItemNotFound_ThrowsException() throws Exception {

    }

    public void testGetDateTimeString_CorrectFormat_IsTrue() throws Exception {

    }

    public void testGetDateTimeString_WrongFormat_IsFalse() throws Exception {

    }

    public void testSize() throws Exception {

    }

}