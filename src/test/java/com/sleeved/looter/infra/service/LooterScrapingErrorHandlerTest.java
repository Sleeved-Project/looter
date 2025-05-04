package com.sleeved.looter.infra.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sleeved.looter.common.exception.LooterScrapingException;
import com.sleeved.looter.common.util.Constantes;

class LooterScrapingErrorHandlerTest {

  private LooterScrapingErrorHandler errorHandler;

  @BeforeEach
  void setUp() {
    errorHandler = new LooterScrapingErrorHandler();
  }

  @Test
  void testFormatErrorMessage() {
    // Test data
    String context = "PROCESSOR_CONTEXT";
    String action = "UPDATE_ACTION";
    String item = "Person(firstName=John, lastName=Doe, age=30)";

    // Execute the method to test
    String result = errorHandler.formatErrorMessage(context, action, item);

    // Verify that the result matches the expected format
    String expected = String.format(Constantes.ERROR_MESSAGE_FORMAT, context, action, item);
    assertEquals(expected, result, "Formatted error message does not match expected format");

    // Verify that the message contains the expected elements
    assertTrue(result.contains(context), "Message should contain the context");
    assertTrue(result.contains(action), "Message should contain the action");
    assertTrue(result.contains(item), "Message should contain the item");
  }

  @Test
  void testHandle() {
    // Test data
    String context = "PROCESSOR_CONTEXT";
    String action = "UPDATE_ACTION";
    String item = "Person(firstName=John, lastName=Doe, age=30)";
    Exception originalException = new RuntimeException("Test exception");

    // Verify that the method throws the expected exception
    LooterScrapingException exception = assertThrows(LooterScrapingException.class, () -> {
      errorHandler.handle(originalException, context, action, item);
    }, "The handle method should throw a LooterScrapingException");

    // Verify the content of the exception
    String expectedMessage = errorHandler.formatErrorMessage(context, action, item);
    assertEquals(expectedMessage, exception.getMessage(), "Exception message does not match expected format");

    // Verify that the original exception is preserved as the cause
    assertEquals(originalException, exception.getCause(), "Original exception should be preserved as the cause");
  }
}