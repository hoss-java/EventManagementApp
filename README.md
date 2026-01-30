# Event Management App
[**Deck Board**](https://github.com/hoss-java/EventManagementApp/blob/main/DECK.md)

## An introduction and overview

### A summery all have been done until now
* Java
> * Things that have been done
>> * Investigation about some Java frameworks to have a better image about the area that is working on [Java frameworks](https://github.com/hoss-java/lessons/blob/main/w48-java1/java_frameworks_spike.md)
>> * Code and develop java standalone classes, and run them via cli [Raw java](https://github.com/hoss-java/calapp-workshop/tree/main/rawjava)
>> * Create maven projects and coding classes as a part of a maven project [Maven project](https://github.com/hoss-java/calapp-workshop/tree/main/CalApp)
>> * Create libraries and helper classes [Librery and helper class](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/main/java/com/CalApp/lib/ExpressionParser.java)
>> * Using libraries and helper classes [Using library](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/main/java/com/CalApp/CalApp.java)
>> * Develop unit tests for methods using classic Junit [Using classic Junit](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/test/java/com/CalApp/ExpressionParserTestJ3.java)
>> * Develop unit tests for methods using Jupiter [using Jupiter](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/test/java/com/CalApp/ExpressionParserTestJ5.java)
>> * Mock and stubbing on both Junit and Jupiter by invoking [Stubbing](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/test/java/com/CalApp/CalAppTest.java)
>> * Touch coding block tests (only Junit) [Block test](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/src/test/java/com/CalApp/CalAppTest.java)
>> * Command and instruction to work with Maven projects [README](https://github.com/hoss-java/calapp-workshop/blob/main/README.md)
> * Findings
>> * Simple Maven project can be created via cli or pre-defined project on IDEs but an advanced project needs to work/edit (with)  with pom files directly. [Here](https://github.com/hoss-java/calapp-workshop/blob/main/CalApp/pom.xml) an example that uses both Junit and Jupiter on a maven project, and it was not possible to add both of them through cli or IDEs.
>> * Java has some powerful frameworks to develop tests but not good as other languages to mock and stubbing.
>> * It is very time-consuming to develop block-tests and advanced unit-tests by using Jupiter. Jupiter focues mostly on unit-tests that no needs to advanced mock and stub. However classic Junit works better in the case of block-tests.
>> * Maven cli has many pre-define instructions and option that can make easy to automate running tests and code on an CI/DI environment, specially through GitHub workflows' actions. Some maven commands are documented [here](https://github.com/hoss-java/calapp-workshop/blob/main/README.md)

* Working environment and tools