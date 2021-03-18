package com.github.lukelinkwalker.orchestrator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.github.lukelinkwalker.orchestrator.transformer.BoundingBox;
//import com.github.lukelinkwalker.orchestrator.transformer.BoundingBox.Intersect;

public class BoundingBoxTest {
	//@Test
    //public void Vertical_Merge_Expected_A()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(2, 3);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //	
    //	if(result.size() > 0) {
    //		if(result.get(0).equals(Intersect.Vertical)) {
    //			assertTrue(true);
    //		} else {
    //			assertTrue(false);
    //		}
    //	} else {
    //		assertTrue(false);
    //	}
    //}
	//
	//@Test
    //public void Vertical_Merge_Expected_B()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(2, 5);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //	
    //	if(result.size() > 0) {
    //		if(result.get(0).equals(Intersect.Vertical)) {
    //			assertTrue(true);
    //		} else {
    //			assertTrue(false);
    //		}
    //	} else {
    //		assertTrue(false);
    //	}
    //}
    //
	//@Test
    //public void Vertical_No_Merge_A()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(2, 6);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //
    //	if(result.size() > 0) {
    //		assertFalse(true);
    //	} else {
    //		assertFalse(false);
    //	}
    //}
	//
	//@Test
    //public void Vertical_No_Merge_B()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(2, 2);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //	
    //	if(result.size() > 0) {
    //		assertFalse(true);
    //	} else {
    //		assertFalse(false);
    //	}
    //}
	//
	//@Test
    //public void Horizontal_Merge_Expected_A()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(3, 4);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //	
    //	if(result.size() > 0) {
    //		if(result.get(0).equals(Intersect.Horizontal)) {
    //			assertTrue(true);
    //		} else {
    //			assertTrue(false);
    //		}
    //	} else {
    //		assertTrue(false);
    //	}
    //}
	//
	//@Test
    //public void Horizontal_Merge_Expected_B()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(1, 4);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //	
    //	if(result.size() > 0) {
    //		if(result.get(0).equals(Intersect.Horizontal)) {
    //			assertTrue(true);
    //		} else {
    //			assertTrue(false);
    //		}
    //	} else {
    //		assertTrue(false);
    //	}
    //}
    //
	//@Test
    //public void Horizontal_No_Merge_A()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(0, 4);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //
    //	if(result.size() > 0) {
    //		assertFalse(true);
    //	} else {
    //		assertFalse(false);
    //	}
    //}
	//
	//@Test
    //public void Horizontal_No_Merge_B()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(4, 4);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //
    //	if(result.size() > 0) {
    //		assertFalse(true);
    //	} else {
    //		assertFalse(false);
    //	}
    //}
	//
    //
	//@Test
    //public void Diagonal_No_Merge_A()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(3, 3);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //
    //	if(result.size() > 0) {
    //		assertFalse(true);
    //	} else {
    //		assertFalse(false);
    //	}
    //}
	//
	//@Test
    //public void Diagonal_No_Merge_B()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(3, 5);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //
    //	if(result.size() > 0) {
    //		assertFalse(true);
    //	} else {
    //		assertFalse(false);
    //	}
    //}
	//
	//@Test
    //public void Diagonal_No_Merge_C()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(1, 5);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //
    //	if(result.size() > 0) {
    //		assertFalse(true);
    //	} else {
    //		assertFalse(false);
    //	}
    //}
    //
	//@Test
    //public void Diagonal_No_Merge_D()
    //{
	//	BoundingBox a = new BoundingBox(2, 4);
    //	BoundingBox b = new BoundingBox(1, 3);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //
    //	if(result.size() > 0) {
    //		assertFalse(true);
    //	} else {
    //		assertFalse(false);
    //	}
    //}
	//
	//@Test
    //public void Contained()
    //{
	//	BoundingBox a = new BoundingBox(1, 1);
	//	a.setWidth(2);
	//	a.setHeight(2);
    //	BoundingBox b = new BoundingBox(2, 2);
    //	ArrayList<Intersect> result = BoundingBox.mergeCheck(a, b);
    //
    //	if(result.size() > 0) {
    //		if(result.get(0).equals(Intersect.ContainedA) || result.get(0).equals(Intersect.ContainedB)) {
    //			assertTrue(true);
    //		} else {
    //			assertTrue(false);
    //		}
    //	} else {
    //		assertTrue(false);
    //	}
    //}
}
