call SET_JAVA_HOME.bat
del Generate_File_Names_With_FileSize_And_Checksum.class

"%JAVA_HOME%\bin\javac" Generate_File_Names_With_FileSize_And_Checksum.java
"%JAVA_HOME%\bin\java" -cp . -Xmx512m Generate_File_Names_With_FileSize_And_Checksum
rem PAUSE
