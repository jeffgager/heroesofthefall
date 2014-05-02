package com.hotf.model.val;

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestNameValidator {

	private static PatternValidator nameValidator;

	@BeforeClass
	public static void beforeClass() {
		ResourceBundle res = ResourceBundle.getBundle("validation");
		nameValidator = new PatternValidator(res.getString("validation.Role.name.pattern"), res.getString("validation.Role.name.message"));
	}
	
	@Test
	public void testValidName() throws ValidationException {
		assertEquals("aaa", nameValidator.validate("aaa"));
		assertEquals("ValidName", nameValidator.validate("ValidName"));
		assertEquals("Vaa1", nameValidator.validate("Vaa1"));
		assertEquals("Vaa1a", nameValidator.validate("Vaa1a"));
		assertEquals("Vaa345678901234567890123456789", nameValidator.validate("Vaa345678901234567890123456789"));
		assertEquals("VaaAAAAAAAAAAAAAAAAAAAAAAAAAAA", nameValidator.validate("VaaAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
	}

	@Test (expected=ValidationException.class)
	public void testFirstPartNotNumbers() throws ValidationException {
		assertEquals("InvalidName", nameValidator.validate("1nvalidName"));
	}

	@Test (expected=ValidationException.class)
	public void testFirstPartNotSpecial() throws ValidationException {
		assertEquals("InvalidName", nameValidator.validate("%nvalidName"));
	}

	@Test (expected=ValidationException.class)
	public void testLastPartNotSpecial() throws ValidationException {
		assertEquals("InvalidName", nameValidator.validate("InvalidNa&e"));
	}

	@Test (expected=ValidationException.class)
	public void testTooSmall() throws ValidationException {
		assertEquals("Ia", nameValidator.validate("Ia"));
	}

	@Test (expected=ValidationException.class)
	public void testTooLong() throws ValidationException {
		assertEquals("InvalidName", nameValidator.validate("Va23456789012345678901234567890"));
	}

}
