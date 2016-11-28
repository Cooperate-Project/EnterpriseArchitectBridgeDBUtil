#Hibernate MySQL Trigger Generator

This utility tool takes a xml file, generated by Hibernate (*hibernate.hbm.xml*) and generates a sql file.
This sql-file contains statements to create triggers and tables to listen to changes in the database persistence.

##Usage

You can get this help dialog by executing: `java -jar HibernateTrigger.jar --help`.
Although this tool is written in Scala, the library is embedded - you'll only need the JAVA 8 JRE.

```
Hibernate XML to SQL Trigger Generator
Usage: HibernateTrigger [options] <input xml> <output sql>

  <input xml>              Specify input hibernate xml file
  <output sql>             Specify output sql file
  -p, --prefix <prefix>    Table and trigger name prefix in database
  -r, --reset              Creates statements to empty all trigger tables (with a proper mapping provided).
  -v, --verbose            Enables detailed console output
  -d, --debug <file>       Prints all parsed Tables and Columns into an debug file
  -e, --event <minutes>    Event interval when old logging entries get removed (Minutes). Set to 0 or -1 to disable removal.
  -e, --exclude <type>,<type>,...
                           Does exclude sql commands for specific hibernate-types (ID, Property, ManyToOne, Bag, CompositeID). Default: Bag
  -c, --clear              Creates statements to drop all tables and triggers of the provided mapping.
```

##Event Scheduler

By default, the generator creates events to remove old logging values on a regular basis. To work properly, the [event scheduler](https://dev.mysql.com/doc/refman/5.7/en/event-scheduler.html) must be enabled on the server. You can change the interval or disable event creation with the `event` flag.
