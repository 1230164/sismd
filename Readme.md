Compile specific solution and common folder

javac -d out src/common/*.java src/sequential/*.java

Run with default GC

java -Xms2g -Xmx4g -cp out sequential.WordCount > logs/sequential/machine1/default-output.log

Run with G1GC and logging

java -Xms2g -Xmx4g -XX:+UseG1GC -Xlog:gc*:file=logs/sequential/machine1/gc-g1.log -cp out sequential.WordCount

Run with Parallel GC and logging

java -Xms2g -Xmx4g -XX:+UseParallelGC -Xlog:gc*:file=logs/sequential/machine1/gc-parallel.log -cp out sequential.WordCount
