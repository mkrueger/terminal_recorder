export APPLICATIONINSIGHTS_CONNECTION_STRING=InstrumentationKey=d8d95f39-1074-4e19-a6f2-4d16f997a757;IngestionEndpoint=https://westeurope-5.in.applicationinsights.azure.com/;LiveEndpoint=https://westeurope.livediagnostics.monitor.azure.com/
export MAVEN_OPTS=-javaagent:/home/azure_user/terminal_recorder/applicationinsights-agent-3.4.19.jar

#mvn compile exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="-u test_user create"
mvn compile exec:java -Dexec.mainClass="org.example.Main" -Djavaagent:applicationinsights-agent-3.4.19.jar -Dexec.args="-u test_user -f ./data/test.cast test"