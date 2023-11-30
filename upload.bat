mvn compile exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="-u test_user create"
mvn compile exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="-u test_user -f data\test.cast upload"
mvn compile exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="-u test_user -f millenium delete"
