package com.sleeved.looter.common.util;

public class Constantes {
  // Error context
  public static final String WRITER_CONTEXT = "WRITER";
  public static final String PROCESSOR_CONTEXT = "PROCESSOR";
  public static final String READER_CONTEXT = "READER";
  public static final String SERVICE_CONTEXT = "SERVICE";
  public static final String STAGE_CARD_TASKLET_CONTEXT = "STAGE CARD TASKLET";
  public static final String STAGING_CARD_READER_CONTEXT = "STAGING CARD READER";
  public static final String CARD_DTO_TO_BASE_ENTITY_PROCESSOR_CONTEXT = "CARD DTO TO BASE ENTITY PROCESSOR";
  public static final String BASE_ENTITY_WRITER_CONTEXT = "BASE ENTITY WRITER";
  public static final String CARD_DTO_TO_SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_PROCESSOR_CONTEXT = "CARD DTO TO SETS WEAKNESS RESISTANCE CARD ENTITIES PROCESSOR";
  public static final String CARD_DTO_TO_COST_ATTACK_CARD_ENTITIES_PROCESSOR_CONTEXT = "CARD DTO TO COST ATTACK CARD ENTITIES PROCESSOR";
  public static final String SETS_WEAKNESS_RESISTANCE_ENTITIES_WRITER_CONTEXT = "SETS WEAKNESS RESISTANCE ENTITIES WRITER";
  public static final String CARD_DTO_TO_CARD_ENTITIES_PROCESSOR_CONTEXT = "CARD DTO TO CARD ENTITIES PROCESSOR";
  public static final String CARD_ENTITIES_WRITER_CONTEXT = "CARD ENTITIES WRITER";
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
  public static final String SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_ITEM = "SETS WEAKNESS RESISTANCE CARD ENTITIES";
  public static final String COST_ATTACK_CARD_ENTITIES_ITEM = "COST ATTACK CARD ENTITIES";
  public static final String CARD_ENTITIES_ITEM = "CARD ENTITIES";
  // Logger formating
  public static final String LINE_BREAK = "\n";
  public static final String SPACE = "";
  // String formating
  public static final String ERROR_MESSAGE_FORMAT = "%s - An error occured where processing %s on %s";
  public static final String ERROR_ITEM_FORMAT = "%s : %s";
  public static final String API_URL_FORMAT = "%s://%s";
  // TcgPlayer API
  public static final String TCG_API_URL_BASE_FORMAT = "%s/%s/%s";
  // Date formatting
  public static final String NOMALIZE_YEARS_ERROR_PATTERN = "^(\\d{1,3})/(\\d{1,2})/(\\d{1,2})\\s+(.*)$";
  public static final String STAGING_CARD_DATE_FORMAT = "yyyy/MM/dd";
  public static final String STAGING_CARD_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

}
