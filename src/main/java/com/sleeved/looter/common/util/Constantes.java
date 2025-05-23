package com.sleeved.looter.common.util;

public class Constantes {
  // Error context
  public static final String WRITER_CONTEXT = "WRITER";
  public static final String PROCESSOR_CONTEXT = "PROCESSOR";
  public static final String READER_CONTEXT = "READER";
  public static final String SERVICE_CONTEXT = "SERVICE";
  public static final String STAGING_CARD_READER_CONTEXT = "STAGING CARD READER";
  public static final String CARD_DTO_TO_BASE_ENTITY_PROCESSOR_CONTEXT = "CARD DTO TO BASE ENTITY PROCESSOR";
  public static final String BASE_ENTITY_WRITER_CONTEXT = "BASE ENTITY WRITER";
  public static final String STAGE_CARD_TASKLET_CONTEXT = "STAGE CARD TASKLET";
  // Error action
  public static final String CREATE_ACTION = "CREATE";
  public static final String UPDATE_ACTION = "UPDATE";
  public static final String DELETE_ACTION = "DELETE";
  public static final String FETCH_DATA_ACTION = "FETCH API DATA";
  public static final String EXECUTE_ACTION = "EXECUTE";
  public static final String READER_ACTION = "READ";
  public static final String PROCESSOR_ACTION = "PROCESS";
  public static final String WRITE_ACTION = "WRITE";
  // Error item
  public static final String STAGING_CARD_ITEM = "STAGING CARD";
  public static final String TCGAPI_CARD_PAGINATE_ITEM = "TCGAPI CARD PAGINATE";
  public static final String CARD_DTO_ITEM = "CARD DTO";
  public static final String BASE_CARD_ENTITIES_ITEM = "BASE CARD ENTITIES";
  // Logger formating
  public static final String LINE_BREAK = "\n";
  public static final String SPACE = "";
  // String formating
  public static final String ERROR_MESSAGE_FORMAT = "%s - An error occured where processing %s on %s";
  public static final String API_URL_FORMAT = "%s://%s";
  // TcgPlayer API
  public static final String TCG_API_URL_BASE_FORMAT = "%s/%s/%s";
}
