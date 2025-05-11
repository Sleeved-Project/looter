package com.sleeved.looter.common.util;

public class Constantes {
  // Error context
  public static final String WRITER_CONTEXT = "WRITER";
  public static final String PROCESSOR_CONTEXT = "PROCESSOR";
  public static final String READER_CONTEXT = "READER";
  public static final String SERVICE_CONTEXT = "SERVICE";
  // Error action
  public static final String CREATE_ACTION = "CREATE";
  public static final String UPDATE_ACTION = "UPDATE";
  public static final String DELETE_ACTION = "DELETE";
  public static final String FETCH_DATA_ACTION = "FETCH API DATA";
  // Error item
  public static final String TCGAPI_CARD_PAGINATE_ITEM = "TCGAPI_CARD_PAGINATE";
  // String formating
  public static final String ERROR_MESSAGE_FORMAT = "%s - An error occured where processing %s on %s";
  public static final String API_URL_FORMAT = "%s://%s";
  // TcgPlayer API
  public static final String TCG_API_URL_BASE_FORMAT = "%s/%s/%s";
}
