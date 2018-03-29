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
      capability "ThermostatMode"
      capability "ThermostatSetpoint"
      
      attribute "battery", "number"
      attribute "batterySensor", "number"
      attribute "thermostatMode", "ENUM", ["auto", "cool", "emergency heat", "heat", "off"]
      attribute "thermostatSetpoint", "number"
      
      command "auto"
      command "cool"
      command "emergencyHeat"
      command "heat"
      command "off"
      command "setThermostatMode"
      command "refresh"
      
      command "setThermostatSetpointUp"
      command "setThermostatSetpointDown"
}

tiles(scale: 2) {

  multiAttributeTile(name:"thermostat", type:"thermostat", width:6, height:4, canChangeIcon: false)  {  
    tileAttribute("device.thermostatMode", key: "PRIMARY_CONTROL") {
      attributeState("off", action: "auto", label:"closed", backgroundColor:"#FFFFFF", nextState: "auto")
      attributeState("auto", action: "heat", label:"auto", backgroundColor: "#00A0DC", nextState: "heat")
      attributeState("heat", action: "off", label:"open", backgroundColor:"#E86D13", nextState: "off")
    }
    tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
      attributeState("VALUE_UP", action: "setThermostatSetpointUp")
      attributeState("VALUE_DOWN", action: "setThermostatSetpointDown")
    }
  }

    standardTile("off", "device.thermostatMode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "thermostatMode", action: "off", icon: "st.vents.vent-closed"
    }

    valueTile("autoColor", "device.thermostatSetpoint", inactiveLabel: true, decoration: "flat", width: 2, height: 1) {
      state "thermostatSetpoint", label: '', 
        backgroundColors:    [
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
      state "thermostatMode", action: "heat", icon: "st.vents.vent-open-text"
    }

    standardTile("auto", "device.thermostatSetpoint", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
      state "thermostatSetpoint", action: "auto", label: 'SET AUTO\n${currentValue}°C', unit: "C"
    }

    valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "battery", label: 'valve battery\n${currentValue}%', unit: "%"
    }
    
    valueTile("batterySensor", "device.batterySensor", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "batterySensor", label: 'sensor battery\n${currentValue}%', unit: "%"
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
  cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 1]
  cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 2]
  cmds << [cmd: zwave.thermostatModeV2.thermostatModeGet(), endpoint: 1]
  encapsulateSequence(cmds, 2000)
}


def off() {
  setThermostatMode("off")
}

def auto() {
  setThermostatMode("auto")
}

def heat() {
  setThermostatMode("heat")
}

def setThermostatMode(String mode) {
  sendEvent(name: "thermostatMode", value: mode, isStateChange: true)
  switch (mode) {
    case "auto":
      encapsulate(zwave.thermostatModeV2.thermostatModeSet(mode: 1), 1)
      break
    case "cool":
      //
      break
    case "emergency heat":
      //
      break
    case "heat":
      encapsulate(zwave.thermostatModeV2.thermostatModeSet(mode: 31), 1)
      break
    case "off":
      encapsulate(zwave.thermostatModeV2.thermostatModeSet(mode: 0), 1)
      break
  }
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
  sendEvent(name: "thermostatSetpoint", unit: "C", value: setpoint.setScale(0, BigDecimal.ROUND_DOWN), isStateChange: true)
  encapsulate(zwave.thermostatSetpointV2.thermostatSetpointSet([precision: 1, scale: 0, scaledValue: setpoint, setpointType: 1, size: 2]), 1)
}

private encapsulate(physicalgraph.zwave.Command cmd) {
  if (zwaveInfo.zw.contains("s")) { 
    secureEncapsulate(cmd)
  } else {
    log.debug "${device.displayName} - no encapsulation supported for command: ${cmd}"
    cmd.format()
  }
}

private encapsulate(physicalgraph.zwave.Command cmd, endpoint) {
  secureEncapsulate(multichannelEncapsulate(cmd, endpoint)).format()
}

private encapsulateSequence(cmds, delay) {
  def commands = cmds.collect{[it.get('cmd'), it.get('endpoint')]}
  delayBetween(commands.collect{encapsulate(it)}, delay)
}

private secureEncapsulate(physicalgraph.zwave.Command cmd) {
  log.trace "${device.displayName} - encapsulating command using Secure Encapsulation, command: ${cmd}"
  zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd)
}

private multichannelEncapsulate(physicalgraph.zwave.Command cmd, endpoint) {
  log.trace "${device.displayName} - encapsulating command using Multi Channel Encapsulation, command: ${cmd}"
  zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: endpoint).encapsulate(cmd)
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
  } else {
    log.warn "${device.displayName} - non-parsed event: ${description}"
  }
  return result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
  createEvent(descriptionText: "${device.displayName}: ${cmd}")
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
  def encapsulatedCommand = cmd.encapsulatedCommand(cmdVersions()) 
  if (encapsulatedCommand) {
    log.trace "${device.displayName} - parsed SecurityMessageEncapsulation into: ${encapsulatedCommand}"
    zwaveEvent(encapsulatedCommand)
  } else {
    log.warn "${device.displayName} – unable to extract secure command from $cmd"
    createEvent(descriptionText: cmd.toString())
  }
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
  def encapsulatedCommand = cmd.encapsulatedCommand(cmdVersions()) 
  if (encapsulatedCommand) {
    log.trace "${device.displayName} - parsed MultiChannelCmdEncap into: ${encapsulatedCommand}"
    zwaveEvent(encapsulatedCommand, cmd.sourceEndPoint)
  } else {
    log.warn "${device.displayName} – unable to extract multi channel command from $cmd"
    createEvent(descriptionText: cmd.toString())
  }
}


def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd, sourceEndPoint = null) {
  //
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd, sourceEndPoint = null) {
  def result
  switch(sourceEndPoint) {
      case 1:
      result = createEvent([name: "battery", unit: "%", value: cmd.batteryLevel])
      break
    case 2:
      result = createEvent([name: "batterySensor", unit: "%", value: cmd.batteryLevel])
      break
    default:
      result = createEvent([name: "battery", unit: "%", value: cmd.batteryLevel])
      break
  }
  log.info "${device.displayName} - parsed event ${cmd} into: ${result}"
  return result
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport cmd, sourceEndPoint = null) {
  def result = createEvent([name: "thermostatSetpoint", unit: "C", value: cmd.scaledValue.setScale(0, BigDecimal.ROUND_DOWN)])
  log.info "${device.displayName} - parsed event ${cmd} into: ${result}"
  return result
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport cmd, sourceEndPoint = null) {
  def result
  switch (cmd.mode) {
    case 1:
      result = createEvent([name: "thermostatMode", value: "auto"])
      break
    case 31:
      result = createEvent([name: "thermostatMode", value: "heat"])
      break
    case 0:
      result = createEvent([name: "thermostatMode", value: "off"])
      break
  }
  log.info "${device.displayName} - parsed event ${cmd} into: ${result}"
  return result
}



/**
 *  CONFIGURATION
 *
 */

private Map cmdVersions() {
  [0x80: 1, 0x40: 2, 0x43: 2]
}