# EventStream
Velostream 2.0

Velostream provides fast stream processing in memory in Java.  Velostream was initially developed in 2004. 

This new version of Velostream adds support for Java Lambda and stream processing

Velostream supports approximately 1.5 million events per second to be processed per stream

The StreamAPI class provides all methods required to build and query a stream.

To run the benchmark run the test class

TestBenchmarkQuoteStream 

With suggested Java parameters -ea  -Xrs -Xss12m -Xms150m -Xmx500m

Projected roadmap to be completed in 2016
1. Re-add dynamic where4j inbound filtering
2. Re-add event builder
3. Re-add Stream API aggreagate operations and add new Web API (allows posting and querying of events)
4. Add JMS API (for posting events as event source to stream processing)
5. Add Stream (output) query store persistence
6. Re-add stream join processing




