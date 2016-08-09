package parser

class Table(val tableName: String,
            val ids: List[String],
            val properties: List[String],
            val manyToOnes: List[String],
            val bags: List[String]) {

}

object Table {

  def apply(tableName: String,
            ids: List[String],
            properties: List[String],
            manyToOnes: List[String],
            bags: List[String]): Table =
    new Table(tableName, ids, properties, manyToOnes, bags)

}

object HibernateTypes extends Enumeration {
  type HibernateTypes = Value
  val id, property, manytoone, bag = Value
}
