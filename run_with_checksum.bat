call SET_JAVA_HOME.bat
rem del Generate_File_Names_With_FileSize_And_Checksum.class

rem "%JAVA_HOME%\bin\javac" Generate_File_Names_With_FileSize_And_Checksum.java
rem "%JAVA_HOME%\bin\java" -cp . -Xmx512m Generate_File_Names_With_FileSize_And_Checksum > file_names_all.log
"%JAVA_HOME%\bin\java" -cp . -Xmx512m Generate_File_Names_With_FileSize_And_Checksum checksum
rem PAUSE
