# Build our project as a normal jar
./mvnw clean package

# Use maven to create a list of jars for use in classpath - output is in cp.txt
./mvnw dependency:build-classpath -Dmdep.outputFile=./target/cp.txt -Dmdep.excludeScope=test

cd target

# Run our app and merge any reflection config into the META-INF/native-image folder for pick-up by the native-image tool.
java -agentlib:native-image-agent=config-merge-dir=../src/main/resources/META-INF/native-image/ -cp sample-1.0-SNAPSHOT.jar:$(cat cp.txt) quarkus.package.main-class

# Run native-image to turn our java app into a compiled program. This will take several minutes...
native-image -H:+AddAllCharsets --no-fallback --class-path sample-1.0-SNAPSHOT.jar:$(cat cp.txt) quarkus.package.main-class

# App can now be run in target folder: ./com.example.app
