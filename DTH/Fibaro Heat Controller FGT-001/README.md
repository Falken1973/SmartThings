# Fibaro Heat Controller DTH

## Update (7.11.2018) – v0.2.1

* Added tepomrary solution and possible fix to [#3](https://github.com/Tomas-Mrazek/SmartThings/issues/3)

## Update (7.11.2018) – v0.2.0

ATTENTION – this release is not compatible with the previous ones. Please remove (exclude) and again add (include) devices to SmartThings.

* Fixed a bug, where a setpoint was limited to 16-24°C range instead of 10-30°C (turning the knob manually is still limited by device itself)
* Google Home and Amazon Alexa are now supported – with a limited functionality (read temperature, change setpoint). NOTE: I do not recommend using it, both SmartApps are obsolete. Google Home for example increments setpoint by half  a degree, which is automatically rounded. Alexa on the other hand updates both cooling and heating setpoint. For example if you set 22°C in Alexa, it actually sets heating setpoint as 20°C and cooling setpoint as 24°C. Device Handler uses only heating setpointc, so value displayed in Alexa is 2°C higher than actual. 
* Polling – re-added battery, added settings for custom interval between automatic polls
* Settings – added trace logging, added default value and required validation
* Added automatic refresh during first setup (inclusion) in SmartThings
* Added protection against device command overflow while multi-command method is already running (refresh, polling, etc...)
* Licence changed to GNU v3.0 

## Update (16.9.2018) – v0.1.0 

* Removed battery query from automatic polling – it heavily drained Valve battery
* Fixed depleted battery value – 255% to 0%

## Update (1.4.2018)

There is already indeed new firmware 4.3. I have the ability to update my device, but it would costs 25$ and over an hour of my time, so I’m going to wait for more stable firmware. The new firmware shoudn’t break any of DTH functionality.

* Added support for external sensor
* Added ability to change device additional parameters
* Added automatic polling
* Added new tiles showing if external sensor is connected and if open window is detected

## Description:

This device consists of two endpoinds. One represents radiator valve, the other is external temperature sensor. Device handler is tested on firmware 4.0 only. This is currently the newest (and only?) firmware, but I’ve heard that new firmware is in development due to bad PID regulator algoritm.

## Functionality:

* Set thermostat mode (fully open, close or automaticaly regulate valve)
* Set thermostat setpoint (regulate temperature between 16°C to 24°C)
* Both can be set via tiles or manually on the device
* Show battery levels
* Manual refresh battery and thermostat mode

## What is missing:

* <s>Second endpoint (temperature sensor)</s>
* Scheduling
* Notifications
* <s>Configurable parameters</s>
* Farenheit :-)

## Known issues:

* Manual refresh does not update thermostat setpoint, becuase the method returns zero values
* Missing whole thermostat capability, therefore Google Assistant integration (set temperature commands)

## Additional comments:
As always, if you find any bugs, request additional features I forgot or if you just want to hate suggest update on UX, everything is welcome. Specially UX changes, gotta keep my OCD satisfied. As soon as external temperature sensor will be available to buy, I’ll get it and develop it’s functionality. I also plan to develop configurable parameters. The rest of missing features aren’t planned in near future. I’m currently in negotiating process with a few people from local reseller and if they will be willing to update my Fibaro devices with future firmwares via their Fibaro hub, I’ll also update this DTH, if needed.
