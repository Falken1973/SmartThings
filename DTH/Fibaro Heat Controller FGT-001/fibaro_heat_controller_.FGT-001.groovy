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
  definition (name: "Fibaro Heat Controller", namespace: "Tomas-Mrazek", author: "Tomáš Mrázek") {
    fingerprint mfr: "010F", prod: "1301", model: "1000"
        
    capability "Battery"
    capability "Thermostat"
    capability "Thermostat Mode"
    capability "Thermostat Setpoint"
    capability "Refresh"
    capability "Polling"
    
    attribute "externalSensorConnected", "string"
    attribute "openWindowDetected", "string"
    attribute "batterySensor", "number"
    
    command "setThermostatSetpointUp"
    command "setThermostatSetpointDown"
  }

  tiles(scale: 2) {

    multiAttributeTile(name:"thermostat", type:"general", width:6, height:4, canChangeIcon: false)  {  
      tileAttribute("device.thermostatMode", key: "PRIMARY_CONTROL") {
        attributeState("off", action: "thermostatMode.auto", label:"closed", backgroundColor:"#FFFFFF", nextState: "auto")
        attributeState("auto", action: "thermostatMode.heat", label:"auto", backgroundColor: "#00A0DC", nextState: "heat")
        attributeState("heat", action: "thermostatMode.off", label:"open", backgroundColor:"#E86D13", nextState: "off")
      }
      tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
        attributeState("VALUE_UP", action: "setThermostatSetpointUp")
        attributeState("VALUE_DOWN", action: "setThermostatSetpointDown")
      }
      tileAttribute("device.temperature", key: "SECONDARY_CONTROL") {
        attributeState("temperature", label:'${currentValue}°C', unit:"C")
      }
    }

    standardTile("off", "device.thermostatMode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "thermostatMode", action: "thermostatMode.off", icon: "st.vents.vent-closed"
    }

    valueTile("autoColor", "device.thermostatSetpoint", inactiveLabel: true, decoration: "flat", width: 2, height: 1) {
      state "thermostatSetpoint", label: '', 
        backgroundColors:  [
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
    
    valueTile("externalSensorConnected", "device.externalSensorConnected", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "true", label: 'sensor\n', backgroundColor: "#79b821", icon: "https://raw.githubusercontent.com/Tomas-Mrazek/SmartThings/master/DTH/icons/checkmark.png"
      state "false", label: 'sensor\n', backgroundColor: "#ff6600", icon: "https://raw.githubusercontent.com/Tomas-Mrazek/SmartThings/master/DTH/icons/xmark.png"
    }
    
    valueTile("openWindowDetected", "device.openWindowDetected", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "true", label: 'window', backgroundColor: "#ff6600", icon: "https://raw.githubusercontent.com/Tomas-Mrazek/SmartThings/master/DTH/icons/window-open.png"
      state "false", label: 'window', backgroundColor: "#79b821", icon: "https://raw.githubusercontent.com/Tomas-Mrazek/SmartThings/master/DTH/icons/window-closed.png"
    }
    
    valueTile("temperature", "device.temperature", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "temperature", label: 'temp\n${currentValue}°C', unit: "C"
    }

    valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "battery", label: 'valve battery\n${currentValue}%', unit: "%"
    }
    
    valueTile("batterySensor", "device.batterySensor", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "batterySensor", label: 'sensor battery\n${currentValue}%', unit: "%"
    }

    standardTile("refresh", "command.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "refresh", label: 'refresh', action: "refresh.refresh", icon: "st.secondary.refresh-icon"
    }

  }
  
  preferences {
    input name: "overrideScheduleDuration", title: "Override Schedule duration (minutes between 10 and 10000)", description: "", type: "number", range: "10..10000", defaultValue: "240", required: true
    input name: "openWindowDetector", title: "Open Window Detector", description: "", type: "bool", defaultValue: true
    input name: "fastOpenWindowDetector", title: "Fast Open Window Detector", description: "", type: "bool", defaultValue: false
    input name: "increaseRecieverSensitivity", title: "Increase Receiver Sensitivity (shortens battery life)", description: "shortens battery life", type: "bool", defaultValue: false
    input name: "ledWhenRemoteControll", title: "LED Indications When Controlling Remotely", description: "", type: "bool", defaultValue: false
    input name: "protectManualOnOff", title: "Protect from setting Full ON and Full OFF mode by turning the knob manually", description: "", type: "bool", defaultValue: false
  }
}



/**
 *  SMARTTHINGS UX EVENTS
 *
 */

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

def updated() {
  if ( state.lastUpdated && (now() - state.lastUpdated) < 500 ) return
  def paramsString = [settings.openWindowDetector, settings.fastOpenWindowDetector, settings.increaseRecieverSensitivity, settings.ledWhenRemoteControll, settings.protectManualOnOff]
  def params = paramsString.collect({(it == true) ? 1 : 0}) 
  def cmds = []
  cmds << [cmd: zwave.configurationV1.configurationSet(parameterNumber: 1, scaledConfigurationValue: settings.overrideScheduleDuration)]
  cmds << [cmd: zwave.configurationV1.configurationSet(parameterNumber: 2, scaledConfigurationValue: getIntegerFromParams(params))]
  for (int i = 1; i <= 2; i++) {
    cmds << [cmd: zwave.configurationV1.configurationGet(parameterNumber: i)]
  }
  state.lastUpdated = now()
  response(encapsulateSequence(cmds, 2000))
}

def refresh() {
  log.debug "refresh()"
  def cmds = []
  cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 1]
  cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 2]
  cmds << [cmd: zwave.thermostatModeV2.thermostatModeGet(), endpoint: 1]
  cmds << [cmd: zwave.sensorMultilevelV5.sensorMultilevelGet(), endpoint: 2]
  cmds << [cmd: zwave.configurationV1.configurationGet(parameterNumber: 3)]
  encapsulateSequence(cmds, 2000)
}

def poll() {
  log.debug "poll()"
  def cmds = []
  cmds << [cmd: zwave.sensorMultilevelV5.sensorMultilevelGet(), endpoint: 2]
  encapsulateSequence(cmds, 2000)
}

private encapsulate(physicalgraph.zwave.Command cmd, endpoint = null) {
  if (zwaveInfo.zw.contains("s")) { 
    if (endpoint) {
      secureEncapsulate(multichannelEncapsulate(cmd, endpoint)).format()
    } else {
      secureEncapsulate(cmd).format()
    }
  } else {
    log.warn "${device.displayName} - no encapsulation supported for command: ${cmd}"
    cmd.format()
  }
}

private encapsulateSequence(cmds, delay) {
  def commands = cmds.collect{[it.get('cmd'), it.get('endpoint')]}
  delayBetween(commands.collect{encapsulate(it)}, delay)
}

private secureEncapsulate(physicalgraph.zwave.Command cmd) {
  //log.trace "${device.displayName} - encapsulating command using Secure Encapsulation, command: ${cmd}"
  zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd)
}

private multichannelEncapsulate(physicalgraph.zwave.Command cmd, endpoint) {
  //log.trace "${device.displayName} - encapsulating command using Multi Channel Encapsulation, command: ${cmd}"
  zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: endpoint).encapsulate(cmd)
}



/**
 *  Z-WAVE DEVICE EVENTS
 *
 */

def parse(String description) {
  //log.debug "PARSE – description – ${description}"
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
  log.info "${device.displayName} - unhandled parsed event without result ${cmd}"
  return result
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
  def encapsulatedCommand = cmd.encapsulatedCommand(cmdVersions()) 
  if (encapsulatedCommand) {
    //log.debug "${device.displayName} - parsed SecurityMessageEncapsulation into: ${encapsulatedCommand}"
    zwaveEvent(encapsulatedCommand)
  } else {
    //log.warn "${device.displayName} – unable to extract secure command from $cmd"
    createEvent(descriptionText: cmd.toString())
  }
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
  def encapsulatedCommand = cmd.encapsulatedCommand(cmdVersions()) 
  if (encapsulatedCommand) {
    //log.debug "${device.displayName} - parsed MultiChannelCmdEncap into: ${encapsulatedCommand}"
    zwaveEvent(encapsulatedCommand, cmd.sourceEndPoint)
  } else {
    //log.warn "${device.displayName} – unable to extract multi channel command from $cmd"
    createEvent(descriptionText: cmd.toString())
  }
}


def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd, sourceEndPoint = null) {
  def result = createEvent(descriptionText: "${device.displayName}: ${cmd}")
  log.info "${device.displayName} - parsed event ${cmd} into: ${result}"
  return result
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd, sourceEndPoint = null) {
  def result
  def batteryLevel
  if (cmd.batteryLevel == 255) {
  	batteryLevel = 0
  } else {
  	batteryLevel = cmd.batteryLevel
  }
  switch(sourceEndPoint) {
    case 1:
      result = createEvent([name: "battery", unit: "%", value: batteryLevel])
      break
    case 2:
      result = createEvent([name: "batterySensor", unit: "%", value: batteryLevel])
      break
    default:
      result = createEvent([name: "battery", unit: "%", value: batteryLevel])
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

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd, sourceEndPoint = null) {
  def result = createEvent([name: "temperature", unit: "C", value: cmd.scaledSensorValue])
  log.info "${device.displayName} - parsed event ${cmd} into: ${result}"
  return result
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd, sourceEndPoint = null) {
  switch (cmd.parameterNumber) {
    case 1:
      settings.overrideScheduleDuration = cmd.scaledConfigurationValue
      break
    case 2:
      def params = getParamsFromInteger(cmd.scaledConfigurationValue, 5)
      settings.openWindowDetector = params.get(0)
      settings.fastOpenWindowDetector = params.get(1)
      settings.increaseRecieverSensitivity = params.get(2)
      settings.ledWhenRemoteControll = params.get(3)
      settings.protectManualOnOff = params.get(4)
      break
    case 3:
      def params = getParamsFromInteger(cmd.scaledConfigurationValue, 2)
      def result1 = createEvent([name: "externalSensorConnected", value: (params.get(0) == 1) ? "true" : "false"]) 
      def result2 = createEvent([name: "openWindowDetected", value: (params.get(1) == 1) ? "true" : "false"])
      log.info "${device.displayName} - parsed event ${cmd} into: ${result1} | ${result2}"
      return [result1, result2]
  }
  log.info "${device.displayName} - parsed event without result ${cmd}"
}



/**
 *  UTILS
 *
 */

private Map cmdVersions() {
  [0x80: 1, 0x40: 2, 0x43: 2, 0x31: 5,   0x70: 1]
}

private getParamsFromInteger(decimal, numberOfParams) {
  def bit = Math.pow(new Double(2), new Double(numberOfParams - 1))
  def params = []
  for(int i = 0; i < numberOfParams; i++) {
    if (decimal >= bit) {
      params << 1
      decimal = decimal - bit
      bit = bit / 2
    } else {
      params << 0
      if (numberOfParams != i) {
        bit = bit / 2
      }
    }
  }
  params = params.reverse()
  return params
}

private getIntegerFromParams(params) {
  def bit = 1
  def decimal = 0
  for(int i =  0; i < params.size(); i++) {
    decimal = decimal + params.get(i) * bit
    bit = bit * 2
  }
  return decimal
}