# homework

Homework is a simple command line based monitoring engine.

It allows to listen a log file of w3c common logfile format and display basic traffic stats and alerts. 

Displayed stats are the number of hits, of which errors (http status >= 400), the total bytes transfered and the sections the most served. Alerts are  displayed if the average hit per second is superior to a given threshold for a given amout of time called window. 

A message is also displayed when the average drops again under the threshold.

Most of the parameters are configurable through command line options.

It also comes with a log generator to help testing, further develompents and demos. It generates up to 100MB of log lines, truncates the file and goes again !

# build

To build datadog homework you need
- java 8+
- maven 3+

To build it please use command:
```
mvn package
```
# run

To run it please use command:
```
java -jar homework-jar-with-dependencies.jar
```
Commons options are:
- -h to display help
- -g to run as generator
- -f to specify the file

# examples

Start the generator and generate up to 20 lines per second
```
java -jar homework-jar-with-dependencies.jar -g -f /tmp/test.log -t 20
```
Start the monitor and display stats every 5s and alerts if the average hits per second exceeds 10 for 30 consecutive seconds
```
java -jar homework-jar-with-dependencies.jar -f /tmp/test.log -d 5 -w 30 -t 10
```

# further improvements

First of all, the AlerterTest is cheap, ugly and not reliable as it will fail randomly. The ZDT.now() should be moved to a protected method and mocked in the test. Plus, reading System.err is crap, Alerter (as well as Display) should send messages that could be properly read and checked.

Parsing lines could be delegated to a parser component that would match different formats and dispatch different entries.

Sharing segments between workers the way it has been done may not be the best implementation. Maybe the tailer could dispatch entries on-the-go to workers that would have local collections of their own.

While generating on average 50k lines per seconds, with default parameters, 50% of the CPU is used in LogEntry.of(), 30% in Alerter.run() and 15% in LogSegment.get(), this should be taken into consideration when thinking about improvements.

On larger scale the parsing and the various computings would be done on different instances all communcating through a stream-processing software platform for example.

