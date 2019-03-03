# MakeBlock Serial Communications

This Java library enable communication between a Java application and MakeBlock robots. This is done via the serial port of any computer able to run Java and supports both the wired serial connection as well as a bleutooth serial connection.

## Getting Started

Currently there is no ready made JAR archive availlable. As such, you will need to download and build from the sources.
Since this project can be built with Maven, this should not prove to difficult.

### Prerequisites

The example uses a MakeBlock script written in mBlock 3.*.
The Java code needs Maven to build.

### Installing

Install the JDK, minimum should be Java 8.
Install Maven, minimum should be Mavan 3.*.
Install mBlock3 if you want to be able to run the example script.

Run the Maven in the root of the Java project to download all dependencies:
mvn clean install

## Running the test script

Open mBlock3, and upload the script to your robot. This script was written for the Ranger.
After uploading the script, disconnect the robot from mBlock.

In mBlock 3 verify the serial port the robot is connected to. This port is currently hardcoded into the Java code and my require changing on your system.

In your IDE of choice, open up the example class:
com.ractoc.mblock.communication.example.RunClient

Edit the serial port to the one found in mBlock in the previous step. This can be done on line 27.
```
portId.getName().equals("COM3")
```

Run the RunClient class.

### Explaining the test script

The test script starts by setting up a connection to the robot. Then, it plays a note. This can be used to test if the connection is working.
After playing the note, it reads the onboard light sensor. This is done for several seconds.
On read every 400 miliseconds. Based on the returned light entensity, the onboard LED's are turned on or of.
This can be tested by either covering up the light sensor, or shining a bright light at the light sensor.
The light entensity used for turning the light on or off can be found in the LightSensorListener, an inner class in the RunClient, between line 152 and 159.

```
if (Integer.parseInt(data[1]) < 400) {
    System.out.println("turn on");
    sender.sendCommand(setAllOnboardLEDsSingleColor((byte) 100));
}
if (Integer.parseInt(data[1]) > 900) {
    System.out.println("turn off");
    sender.sendCommand(setAllOnboardLEDsSingleColor((byte) 0));
}
``` 

By varying these numbers, you can vary when the lights are turned on, and then they turned off.

The RunClient prints various messages to the console so you can keep track of the progress.

```
response: SENSOR|LIGHT|306

SENSOR
light intensity: 306
turn on
response: SENSOR|LIGHT|969
  
SENSOR
light intensity: 969
turn off
response: COLOR|PROCESSED
```

## Built With

* [mBlock](http://www.dropwizard.io/1.0.2/docs/) - The IDE used to build the makeblock script for the ranger
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Mark Schrijver** - *Initial work* - [mblock-communication](https://github.com/ractoc/mblock-communications)

See also the list of [contributors](https://github.com/ractoc/mblock-communications/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
