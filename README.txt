Implementation of VWAP algorithm with simulation tool

It includes:
(1) divide trade horizon into predefined length interval
    and target to fill target quantity indicated on the volume profile
(2) send passive slice in the beginning of each interval
(3) Passive slice peg to passive touch
(4) Cross passive slice when crossing time is reached
(5) VWAP logic run between start time and end time only
(6) Support three target curve ( low, desire, upper) use by different urgent setting
(7) build simulation tool to run vwap logic in second interval
(8) Log algo activities when simulation is running


Requires 
========
Jre 1.8+
Junit 4.12+
Grade 5.2.1+
IntelliJ

Step to import gradle project into IntelliJ
===========================================
Downlaod zip file and unzip it to CodingTest directory
Run IntelliJ
Click on "Import Project" on "Welcome to IntelliJ IDEA" page
Select the location of directory CodingTest , Click "OK"
Select "Import project from external model" and select Gradle and click "Finish"
IntelliJ would start a build automatically and download the build dependencies if necessary

Step to start Simulation
========================
Open Gradle View by clicking View > Tool Windows > Gradle
Open Tasks > verfiication > and double click "test" to start the test simulation
Or
Run test VwapTest.testVwap()

