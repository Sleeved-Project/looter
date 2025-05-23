package com.sleeved.looter.infra.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sleeved.looter.common.exception.LooterScrapingException;
import com.sleeved.looter.common.util.Constantes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LooterScrapingErrorHandlerTest {

  private LooterScrapingErrorHandler errorHandler;

  @BeforeEach
  void setUp() {
    errorHandler = new LooterScrapingErrorHandler();
  }

  @Test
  void formatErrorMessage_shouldReturnCorrectlyFormattedMessage() {
    // Test data
    String context = "PROCESSOR_CONTEXT";
    String action = "UPDATE_ACTION";
    String item = "Person(firstName=John, lastName=Doe, age=30)";

    // Execute the method to test
    String result = errorHandler.formatErrorMessage(context, action, item);

    // Verify that the result matches the expected format
    String expected = String.format(Constantes.ERROR_MESSAGE_FORMAT, context, action, item);
    // Avec AssertJ:
    assertThat(result)
        .as("Formatted error message")
        .isEqualTo(expected)
        .contains(context, action, item);
  }

  @Test
  void handle_shouldThrowLooterScrapingExceptionWithFormattedMessage() {
    // Test data
    String context = "PROCESSOR_CONTEXT";
    String action = "UPDATE_ACTION";
    String item = "Person(firstName=John, lastName=Doe, age=30)";
    Exception originalException = new RuntimeException("Test exception");

    // Préparation du message attendu
    String expectedMessage = errorHandler.formatErrorMessage(context, action, item);

    // Verify that the method throws the expected exception with AssertJ
    assertThatThrownBy(() -> {
      errorHandler.handle(originalException, context, action, item);
    })
        .as("The handle method should throw a LooterScrapingException")
        .isInstanceOf(LooterScrapingException.class)
        .hasMessage(expectedMessage)
        .hasCause(originalException);
  }
}