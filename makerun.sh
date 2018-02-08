export JAVA_INC=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.151-1.b12.35.amzn1.x86_64/include/

# step 1: compile the .class file with invocation to a native method
javac src/main/java/com/glfrc/jni/JavaHello.java src/main/java/com/glfrc/jni/HelloTester.java
# step 2: auto-generate a .h header file from said Java source
javah -d src/main/c++ -classpath src/main/java com.glfrc.jni.JavaHello

# step 3: make the shared library with the name linked in said Java source, and implementing said native method
g++ -std=c++11 -shared -fPIC -I$JAVA_INC -I$JAVA_INC/linux src/main/c++/NativeHelloImpl.cpp -o src/main/resources/libhello.so

# step 4: run JVM with java.library.path set to include said shared library
java -cp src/main/java -Djava.library.path=src/main/resources com.glfrc.jni.HelloTester
