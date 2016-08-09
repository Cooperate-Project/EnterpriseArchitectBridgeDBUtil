package statement

class DropStatement(val tableName: String) {

  override def toString: String = "DROP TABLE IF EXISTS `" + tableName + "`;"

}
