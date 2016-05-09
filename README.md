# EventStream
# Velostream 2.0

Velostream provides fast stream processing in memory in Java.  Velostream was initially developed in 2006.

    Stream quotestream = newStream(streamDefinition("quote").setEventTTL(1).build());
    Event event = eventBuilder("quote").addFieldValue("symbol", "JRD")
        .addFieldValue("price", 20.0D).build();
    quotestream.put(event, false);
    
    ....
    quotestream.query().getAverage("price");

This new version of Velostream adds support for Java Lambda and stream processing

Velostream supports approximately 1.5 million events per second to be processed per stream

The StreamAPI class provides all methods required to build and query a stream.

To run the benchmark run the test class

TestQuoteStream

With suggested Java parameters -ea  -Xrs -Xss12m -Xms150m -Xmx500m

Projected roadmap to be completed in 2016<BR>
1. Re-add dynamic where4j inbound filtering<BR>
2. Re-add event builder<BR>
3. Re-add Stream API aggreagate operations and add new Web API (allows posting and querying of events)<BR>
4. Add JMS API (for posting events as event source to stream processing)<BR>
5. Add Stream (output) query store persistence<BR>
6. Re-add stream join processing<BR>




