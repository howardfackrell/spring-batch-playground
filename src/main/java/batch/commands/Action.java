/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package batch.commands;
@org.apache.avro.specific.AvroGenerated
public enum Action implements org.apache.avro.generic.GenericEnumSymbol<Action> {
  RUN, RERUN, STOP, DELETE, SHUTDOWN  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"Action\",\"namespace\":\"batch.commands\",\"symbols\":[\"RUN\",\"RERUN\",\"STOP\",\"DELETE\",\"SHUTDOWN\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
