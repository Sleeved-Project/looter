package com.sleeved.looter.infra.service;

import org.springframework.stereotype.Service;

import com.sleeved.looter.common.exception.LooterScrapingException;
import com.sleeved.looter.common.util.Constantes;

@Service
public class LooterScrapingErrorHandler {
  public void handle(Exception e, String context, String action, String item) {
    String message = formatErrorMessage(context, action, item);
    throw new LooterScrapingException(message, e);
  }

  public String formatErrorMessage(String context, String action, String formatedItem) {
    return String.format(
        Constantes.ERROR_MESSAGE_FORMAT,
        context,
        action,
        formatedItem);
  }

  public String formatErrorItem(String item, String details) {
    return String.format(Constantes.ERROR_ITEM_FORMAT, item, details);
  }
}
