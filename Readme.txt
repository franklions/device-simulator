mvn install:install-file -Dfile=lib/DJNativeSwing-SWT.jar -DgroupId=com.djnativeswing.swt -DartifactId=DJNativeSwing-SWT -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/DJNativeSwing.jar -DgroupId=com.djnativeswing -DartifactId=DJNativeSwing -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/swt.jar -DgroupId=org.eclipse.swt -DartifactId=swt -Dversion=4.4.2 -Dpackaging=jar

mvn clean package -Dmaven.javadoc.skip=true
