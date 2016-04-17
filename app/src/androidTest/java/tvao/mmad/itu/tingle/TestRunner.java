package tvao.mmad.itu.tingle;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import tvao.mmad.itu.tingle.Model.ThingUnitTest;

/**
 * Execute Test case(s) which import the JUnitCore class and uses the runClasses() method which take the test class name as parameter.
 */
public class TestRunner {

    public static void main(String[] args)
    {
        Result result = JUnitCore.runClasses(ThingUnitTest.class);

        for (Failure failure : result.getFailures())
        {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }

}