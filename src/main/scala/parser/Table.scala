package parser

class Table(val tableName: String,
            val ids: List[String],
            val properties: List[String],
            val manyToOnes: List[String],
            val bags: List[String],
            val compositeIds: List[String]) {

}

object Table {

  def apply(tableName: String,
            ids: List[String],
            properties: List[String],
            manyToOnes: List[String],
            bags: List[String],
            compositeIds: List[String]): Table =
    new Table(tableName, ids, properties, manyToOnes, bags, compositeIds)

}


object HibernateTypes extends Enumeration {
  type HibernateTypes = Value
  // FIXME: Refactoring: Umgang mit HibernateTypes verletzt Lokalitäts-Prinzip
  // FIXME: Potenzielle Fehlerquelle: Gibt es noch andere Typen?
  val id, property, manytoone, bag, compositeid = Value
}
