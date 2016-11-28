package de.cooperateproject.incrementalsync.dbutils.statement

/**
  * A de.cooperateproject.incrementalsync.dbutils.statement to insert, alter or delete data in a SQL Database system.
  * The only common method of all statements is toString() to get the textual representation.
  */
trait Statement {

  override def toString: String = super.toString

}
