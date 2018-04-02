package in.test.mywebapp.model;

import static org.junit.Assert.*;

import org.junit.Test;

import in.test.mywebapp.common.measure.Dozen;
import in.test.mywebapp.common.measure.Gram;
import in.test.mywebapp.common.measure.Kilogram;
import in.test.mywebapp.common.measure.Unit;

public class QuantityTest {

	@Test
	public void test_add() {
		Kilogram unit = new Kilogram();
		
		Quantity<Kilogram> kQ10 = new Quantity<Kilogram>(10, unit);
		Quantity<Kilogram> kQ1 = new Quantity<Kilogram>(1, unit);

		Quantity<?> q = kQ10.add(kQ1);
		Quantity<?> q2 = kQ1.add(kQ10);
		double expected = 11.0d;
		assertEquals(expected, q.value(), .001);
		assertEquals("Kilogram",q.unit());
		assertEquals(expected, q2.value(), .001);
		assertEquals("Kilogram",q2.unit());
	}
	
	@Test
	public void test_add2() {
		Kilogram unit = new Kilogram();
		Gram gUnit = new Gram();
		
		Quantity<Kilogram> kQ10 = new Quantity<Kilogram>(10, unit);
		Quantity<Gram> kQ1 = new Quantity<Gram>(1, gUnit);

		Quantity<?> q = kQ10.add(kQ1);
		Quantity<?> q2 = kQ1.add(kQ10);
		double expected = 10.001d;
		assertEquals(expected, q.value(), .001);
		assertEquals("Kilogram",q.unit());
		assertEquals(10001d, q2.value(), .001);
		assertEquals("gm",q2.unit());
	}
	
	@Test
	public void test_remove() {
		Kilogram unit = new Kilogram();
		
		Quantity<Kilogram> kQ10 = new Quantity<Kilogram>(10, unit);
		Quantity<Kilogram> kQ1 = new Quantity<Kilogram>(1, unit);

		Quantity<?> q = kQ10.remove(kQ1);
		Quantity<?> q2 = kQ1.remove(kQ10);

		assertEquals(9.0d, q.value(), .001);
		assertEquals("Kilogram",q.unit());
		assertEquals(-9.0d, q2.value(), .001);
		assertEquals("Kilogram",q2.unit());
	}
	
	@Test
	public void test_remove2() {
		Kilogram unit = new Kilogram();
		Gram gUnit = new Gram();
		
		Quantity<Kilogram> kQ10 = new Quantity<Kilogram>(10, unit);
		Quantity<Gram> kQ1 = new Quantity<Gram>(1, gUnit);

		Quantity<?> q = kQ10.remove(kQ1);
		Quantity<?> q2 = kQ1.remove(kQ10);
		double expected = 9.999d;
		assertEquals(expected, q.value(), .001);
		assertEquals("Kilogram",q.unit());
		assertEquals(-9999d, q2.value(), .001);
		assertEquals("gm",q2.unit());
	}

	@Test
	public void test_add_count() {
		Dozen unit = new Dozen();
		
		Quantity<Dozen> kQ10 = new Quantity<Dozen>(0.5, unit);
		Quantity<Dozen> kQ1 = new Quantity<Dozen>(1, unit);

		Quantity<?> q = kQ10.add(kQ1);
		Quantity<?> q2 = kQ1.add(kQ10);
		double expected = 1.5d;
		assertEquals(expected, q.value(), .001);
		assertEquals("dozen",q.unit());
		assertEquals(expected, q2.value(), .001);
		assertEquals("dozen",q2.unit());
	}
}
