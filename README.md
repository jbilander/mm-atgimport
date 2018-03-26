<h4> Technologies used in this project: </h4>

<ul>
    <li>Java 9 with Modules</li>
    <li>Webservice client calls (polling with cronjobs) using JAX-WS in built-in SDK (java.xml.ws)</li>
    <li>Using MariaDbPoolDataSource in MariaDB Connector/J 2.2.3 jdbc-driver for MySQL</li>
    <li>Fast MySQL-connection through localhost Unix-socket/Pipe-connection (Linux/Windows) (app and db on same machine!)</li>
    <li>Thread-safe jobs</li>
    <li>Cronjobs in java using CronThreadPoolExecutor that extends ScheduledThreadPoolExecutor</li>
    <li>CronExpressions read from properties-file (easily changed)</li>
    <li>Package with Maven 3 "package"-feature as a jar (mm-atgimport-1.0-SNAPSHOT.jar)</li>
    <li>Pure JDBC with Prepared Statements and transaction-handling/rollbacks</li>
    <li>Clean graceful shutdown-hook letting the ongoing jobs finish before exit</li>
    <li>Simple logging to file with java.util.logging</li>
    <li>Run as a standalone application with:</li>
</ul> 
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