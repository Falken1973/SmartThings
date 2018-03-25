/**
 *  Fibaro Heat Controller FGT-001
 *
 *  Copyright 2018 Tomáš Mrázek
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
  definition (name: "Fibaro Heat Controller FGT-001", namespace: "Tomas-Mrazek", author: "Tomáš Mrázek") {
    fingerprint mfr: "010F", prod: "1301", model: "1000"
        
      capability "Battery"
      capability "ThermostatSetpoint"
      capability "ThermostatMode"
      
      command "refresh"
      command "setThermostatSetpointUp"
      command "setThermostatSetpointDown"
}

tiles(scale: 2) {

  multiAttributeTile(name:"thermostat", type:"general", width:6, height:4, canChangeIcon: false)  {  
    tileAttribute("device.thermostatMode", key: "PRIMARY_CONTROL") {
      attributeState("0", action: "thermostatMode.auto", label:"closed", backgroundColor:"#FFFFFF")
      attributeState("1", action: "thermostatMode.heat", label:"auto", backgroundColor: "#00A0DC")
      attributeState("31", action: "thermostatMode.cool", label:"open", backgroundColor:"#E86D13")
    }
    tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
      attributeState("VALUE_UP", action: "setThermostatSetpointUp")
      attributeState("VALUE_DOWN", action: "setThermostatSetpointDown")
    }
  }

    standardTile("cool", "device.thermostatMode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "thermostatMode", action: "thermostatMode.cool", icon: "st.vents.vent-closed"
    }

    valueTile("autoColor", "device.thermostatSetpoint", inactiveLabel: true, decoration: "flat", width: 2, height: 1) {
      state "thermostatSetpoint", label: '', 
        backgroundColors:	[
          [value: 16, color: "#007fff"],
          [value: 17, color: "#00b6ff"],
          [value: 18, color: "#00faff"],
          [value: 19, color: "#00ffb2"],
          [value: 20, color: "#00FF00"],
          [value: 21, color: "#bfff00"],
          [value: 22, color: "#ffff00"],
          [value: 23, color: "#ffa500"],
          [value: 24, color: "#ff0000"]
        ]
    }

    standardTile("heat", "device.thermostatMode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "thermostatMode", action: "thermostatMode.heat", icon: "st.vents.vent-open-text"
    }

    standardTile("auto", "device.thermostatSetpoint", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
      state "thermostatSetpoint", action: "thermostatMode.auto", label: 'SET AUTO\n${currentValue}°C', unit: "C"
    }

    valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "battery", label: '${currentValue}%', unit: "%"
    }

    standardTile("refresh", "command.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "default", label: 'refresh', action: "refresh", icon: "st.secondary.refresh"
    }

  }
}



/**
 *  SMARTTHINGS UX EVENTS
 *
 */

def refresh() {
  def cmds = []
  cmds << zwave.batteryV1.batteryGet()
  cmds << zwave.thermostatModeV2.thermostatModeGet()
  encapsulateSequence(cmds, 2000)
}

def cool() {
  setThermostatMode(0)	
}

def auto() {
  setThermostatMode(1)
}

def heat() {
  setThermostatMode(31)
}

def setThermostatMode(mode) {
  sendEvent(name: "thermostatMode", value: mode, isStateChange: true)
  secureEncapsulate(zwave.thermostatModeV2.thermostatModeSet(mode: mode))
}

def setThermostatSetpointUp() {
  def setpoint = device.latestValue("thermostatSetpoint")
  if (setpoint < 24) {
    setpoint = setpoint + 1
  }
  setThermostatSetpoint(setpoint)
}

def setThermostatSetpointDown() {
  def setpoint = device.latestValue("thermostatSetpoint")
  if (setpoint > 16) {
    setpoint = setpoint - 1
  }
  setThermostatSetpoint(setpoint)
}

def setThermostatSetpoint(setpoint) {
  sendEvent(name: "thermostatSetpoint", value: setpoint.setScale(0, BigDecimal.ROUND_DOWN), isStateChange: true)
  secureEncapsulate(zwave.thermostatSetpointV2.thermostatSetpointSet([precision: 1, scale: 0, scaledValue: setpoint, setpointType: 1, size: 2]))
}

private encapsulate(physicalgraph.zwave.Command cmd) {
  if (zwaveInfo.zw.contains("s")) { 
    secureEncapsulate(cmd)
  } else {
    log.debug "${device.displayName} - no encapsulation supported for command: ${cmd}"
    cmd.format()
  }
}

private secureEncapsulate(physicalgraph.zwave.Command cmd) {
  log.debug "${device.displayName} - encapsulating command using Secure Encapsulation, command: ${cmd}"
  zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}

private encapsulateSequence(cmds, delay) {
  delayBetween(cmds.collect{ encapsulate(it) }, delay)
}



/**
 *  Z-WAVE DEVICE EVENTS
 *
 */

def parse(String description) {
  def result = null
  def cmd = zwave.parse(description)
  if (cmd) {
    result = zwaveEvent(cmd)
    log.debug "${device.displayName} - parsed ${cmd} to ${result.inspect()}"
  } else {
    log.debug "${device.displayName} - non-parsed event: ${description}"
  }
  return result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
  createEvent(descriptionText: "${device.displayName}: ${cmd}")
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
  def encapsulatedCommand = cmd.encapsulatedCommand(cmdVersions()) 
  if (encapsulatedCommand) {
    log.debug "${device.displayName} - parsed SecurityMessageEncapsulation into: ${encapsulatedCommand}"
    zwaveEvent(encapsulatedCommand)
  } else {
    log.warn "${device.displayName} – unable to extract secure cmd from $cmd"
    createEvent(descriptionText: cmd.toString())
  }
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
  //
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
  createEvent([name: "battery", unit: "%", value: cmd.batteryLevel])
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport cmd) {
  createEvent([name: "thermostatSetpoint", unit: "C", value: cmd.scaledValue.setScale(0, BigDecimal.ROUND_DOWN)])
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport cmd) {
  createEvent([name: "thermostatMode", value: cmd.mode])
}



/**
 *  CONFIGURATION
 *
 */

private Map cmdVersions() {
  [0x80: 1, 0x40: 2, 0x43: 2]
}