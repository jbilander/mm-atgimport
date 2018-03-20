<h4>Technologies used in this project:</h4>
* Java 9 with Modules
* Webservice client calls (polling with cronjobs) using JAX-WS in built-in SDK (java.xml.ws)
* MiniConnectionPoolManager (using MariaDbPoolDataSource) mariadb jdbc-driver for MySQL
* Fast MySQL-connection through socket-connection (app and db on same machine!)
* Thread-safe jobs
* Cronjobs in java using CronThreadPoolExecutor that extends ScheduledThreadPoolExecutor
* CronExpression read from properties-file (easily changed)
* Package with Maven 3 "package"-fetaure as a jar (mm-atgimport-1.0-SNAPSHOT.jar)
* Pure JDBC with Prepared Statements and transaction-handling/rollbacks
* Clean graceful shutdown-hook letting the ongoing jobs finish before exit
* Run as a standalone application with:
<pre>
mm-atgimport\target\release>java --add-modules=java.xml.ws -jar mm-atgimport-1.0-SNAPSHOT.jar
mars 20, 2018 2:18:54 EM com.creang.JobScheduler run
INFO: Running JobScheduler
CorePoolSize5
CorePoolSize6
CorePoolSize7
CorePoolSize8
CorePoolSize9
CorePoolSize10
mars 20, 2018 2:21:00 EM com.creang.task.UpdateRaceAndRaceCardTask run
INFO: UpdateRaceAndRaceCardTask: Start
...
</pre>