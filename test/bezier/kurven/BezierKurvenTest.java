/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bezier.kurven;

import java.awt.Point;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Elias
 */
public class BezierKurvenTest {
    
    public BezierKurvenTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class BezierKurven.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        BezierKurven instance = new BezierKurven();
        instance.init();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setPunkt method, of class BezierKurven.
     */
    @Test
    public void testSetPunkt() {
        System.out.println("setPunkt");
        Point p = null;
        BezierKurven instance = new BezierKurven();
        instance.setPunkt(p);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Punkterechnen method, of class BezierKurven.
     */
    @Test
    public void testPunkterechnen() {
        System.out.println("Punkterechnen");
        BezierKurven instance = new BezierKurven();
        instance.Punkterechnen();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
