# Fibaro Heat Controller DTH

This device consists of two endpoinds. One represents radiator valve, the other is external temperature sensor. Device handler is tested on firmware 4.0 only. This is currently the newest (and only?) firmware, but I’ve heard that new firmware is in development due to bad PID regulator algoritm.

## Functionality:

* Set thermostat mode (fully open, close or automaticaly regulate valve)
* Set thermostat setpoint (regulate temperature between 16°C to 24°C)
* Both can be set via tiles or manually on the device
* Show battery levels
* Manual refresh battery and thermostat mode

## What is missing:

* Second endpoint (temperature sensor)
* Scheduling
* Notifications
* Configurable parameters
* Farenheit :-)

## Known issues:

* Manual refresh does not update thermostat setpoint, becuase the method returns zero values
* Missing whole thermostat capability, therefore Google Assistant integration (set temperature commands)

## Additional comments:
As always, if you find any bugs, request additional features I forgot or if you just want to hate suggest update on UX, everything is welcome. Specially UX changes, gotta keep my OCD satisfied. As soon as external temperature sensor will be available to buy, I’ll get it and develop it’s functionality. I also plan to develop configurable parameters. The rest of missing features aren’t planned in near future. I’m currently in negotiating process with a few people from local reseller and if they will be willing to update my Fibaro devices with future firmwares via their Fibaro hub, I’ll also update this DTH, if needed.
