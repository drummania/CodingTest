Implementation of VWAP algorithm with simulation tool

It includes:
(1) Divide trade horizon into trade intervals with pre-defined length.
    Interval has target filled quantity which is calculated based on given volume profile
(2) Algo send passive slice in the beginning of each trade interval (price improvement feature)
(3) Algo peg passive slice to passive touch.
(4) Algo amend passive slice to cross the spread when crossing time is reached
(5) VWAP logic active between start time and end time only
(6) Support three target curve (low, desire, upper) which is used based on Urgent setting
(7) Build simulation tool to run vwap logic in second interval
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

