package parser

/**
  * A Table contains attributes and is the database representation of an model.
  *
  * @param tableName    The name of the table
  * @param ids          A List of ids (usually the primary key)
  * @param properties   A list of properties
  * @param manyToOnes   A list of many-to-one-relations
  * @param bags         A list of bags
  * @param compositeIds A list of composite ids
  */
class Table(val tableName: String,
            val ids: List[String],
            val properties: List[String],
            val manyToOnes: List[String],
            val bags: List[String],
            val compositeIds: List[String]) {

}

/**
  * Companion object used to create a table.
  */
object Table {

  /**
    * Creates an instance of a table object.
    *
    * @param tableName    The name of the table
    * @param ids          A List of ids (usually the primary key)
    * @param properties   A list of properties
    * @param manyToOnes   A list of many-to-one-relations
    * @param bags         A list of bags
    * @param compositeIds A list of composite ids
    * @return a immutable table with the provided information
    */
  def apply(tableName: String,
            ids: List[String],
            properties: List[String],
            manyToOnes: List[String],
            bags: List[String],
            compositeIds: List[String]): Table =
  new Table(tableName, ids, properties, manyToOnes, bags, compositeIds)

}

/**
  * Represents all (known) Column/Attribute-Types of an hibernate class.
  */
object HibernateTypes extends Enumeration {
  type HibernateTypes = Value
  // FIXME: Refactoring: Umgang mit HibernateTypes verletzt Lokalitäts-Prinzip
  // FIXME: Potenzielle Fehlerquelle: Gibt es noch andere Typen?
  val id, property, manytoone, bag, compositeid = Value
}
