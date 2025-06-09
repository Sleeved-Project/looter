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
  public static final String CARD_DTO_TO_LINK_CARD_RELATIONS_ENTITIES_PROCESSOR_CONTEXT = "CARD DTO TO LINK CARD RELATIONS ENTITIES PROCESSOR";
  public static final String LINK_CARD_RELATIONS_ENTITIES_WRITER_CONTEXT = "LINK CARD RELATIONS ENTITIES WRITER";
  public static final String STAGING_CARD_PRICE_READER_CONTEXT = "STAGING CARD PRICE READER";
  public static final String CARD_DTO_TO_REPORTING_PRICE_ENTITY_PROCESSOR_CONTEXT = "CARD DTO TO REPORTING PRICE ENTITY PROCESSOR";
  public static final String REPORTING_PRICE_ENTITIES_WRITER_CONTEXT = "REPORTING PRICE ENTITIES WRITER";
  public static final String CARD_DTO_TO_TCG_PLAYER_PRICE_ENTITY_PROCESSOR_CONTEXT = "CARD DTO TO TCG PLAYER PRICE ENTITY PROCESSOR";
  public static final String TCG_PLAYER_PRICE_CARD_ENTITIES_WRITER_CONTEXT = "TCGP PLAYER PRICE CARD ENTITIES WRITER";
  public static final String CARD_IMAGE_READER_CONTEXT = "CARD IMAGE READER";
  public static final String CARD_IMAGE_TO_HASH_IMAGE_PROCESSOR_CONTEXT = "CARD IMAGE TO HASH IMAGE PROCESSOR";
  public static final String HASH_SERVICE_CONTEXT = "HASH SERVICE";
  public static final String HASH_IMAGE_FETCH_CONTEXT = "HASH IMAGE FETCH";
  public static final String HASH_IMAGE_WRITER_CONTEXT = "HASH IMAGE WRITER";
  // Error action
  public static final String CREATE_ACTION = "CREATE";
  public static final String UPDATE_ACTION = "UPDATE";
  public static final String DELETE_ACTION = "DELETE";
  public static final String FETCH_DATA_ACTION = "FETCH API DATA";
  public static final String EXECUTE_ACTION = "EXECUTE";
  public static final String READER_ACTION = "READ";
  public static final String PROCESSOR_ACTION = "PROCESS";
  public static final String WRITE_ACTION = "WRITE";
  public static final String HASH_CALCULATION_ACTION = "CALCULATE HASH";
  // Error item
  public static final String STAGING_CARD_ITEM = "STAGING CARD";
  public static final String TCGAPI_CARD_PAGINATE_ITEM = "TCGAPI CARD PAGINATE";
  public static final String CARD_DTO_ITEM = "CARD DTO";
  public static final String BASE_CARD_ENTITIES_ITEM = "BASE CARD ENTITIES";
  public static final String SETS_WEAKNESS_RESISTANCE_CARD_ENTITIES_ITEM = "SETS WEAKNESS RESISTANCE CARD ENTITIES";
  public static final String COST_ATTACK_CARD_ENTITIES_ITEM = "COST ATTACK CARD ENTITIES";
  public static final String CARD_ENTITIES_ITEM = "CARD ENTITIES";
  public static final String LINK_CARD_RELATIONS_ENTITIES_ITEM = "LINK_CARD_RELATIONS ENTITIES";
  public static final String STAGING_CARD_PRICE_ITEM = "STAGING CARD PRICE";
  public static final String CARD_PRICE_DTO_ITEM = "CARD PRICE DTO";
  public static final String REPORTING_PRICE_ITEM = "REPORTING PRICE";
  public static final String TCG_PLAYER_PRICE_CARD_ENTITIES_ITEM = "TCGP PLAYER PRICE CARD";
  public static final String CARD_IMAGE_ITEM = "CARD IMAGE";
  public static final String HASH_IMAGE_ITEM = "HASH IMAGE";
  // Logger formating
  public static final String LINE_BREAK = "\n";
  public static final String SPACE = "";
  // String formating
  public static final String ERROR_MESSAGE_FORMAT = "%s - An error occured where processing %s on %s";
  public static final String ERROR_ITEM_FORMAT = "%s : %s";
  public static final String API_URL_FORMAT = "%s://%s";
  // TcgPlayer API
  public static final String TCG_API_URL_BASE_FORMAT = "%s/%s/%s";
  // Iris API
  public static final String IRIS_API_URL_BASE_FORMAT = "%s/%s/%s";
  // Date formatting
  public static final String NOMALIZE_YEARS_ERROR_PATTERN = "^(\\d{1,3})/(\\d{1,2})/(\\d{1,2})\\s+(.*)$";
  public static final String STAGING_CARD_DATE_FORMAT = "yyyy/MM/dd";
  public static final String STAGING_CARD_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

}
