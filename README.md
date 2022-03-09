* git clone git@github.com:OttVilson/testing-framework.git
* cd testing-framework
* mvn package
* java -jar target/testing-framework-1.0-SNAPSHOT.jar

optional command line argment for running jar
\"{\\\"url\\\":\\\"myUrl\\\",\\\"headless\\\":false/true}\"
where either of the fields can be omitted, so all
* \"{\\\"ulr\\\":\\\"someUrl\\\"}\"
* \"{\\\"headless\\\":true}\"
* \"{\\\"url\\\":\\\"myUrl\\\",\\\"headless\\\":false}\"

are valid.
