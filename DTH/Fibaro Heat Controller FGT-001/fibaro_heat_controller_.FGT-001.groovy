/**
 * Fibaro Heat Controller FGT-001
 *
 * Copyright (C) 2018 Tomáš Mrázek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
metadata {
    definition(name: "Fibaro Heat Controller", namespace: "Tomas-Mrazek", author: "Tomáš Mrázek", ocfDeviceType: "oic.d.thermostat") {
        fingerprint mfr: "010F", prod: "1301", model: "1000"

        capability "Battery"
        capability "Thermostat Mode"
        capability "Thermostat Heating Setpoint"
        capability "Temperature Measurement"
        capability "Notification"
        capability "Configuration"
        capability "Refresh"

        attribute "batterySensor", "number"
        attribute "externalSensorConnected", "string"
        attribute "openWindowDetected", "string"

        command "setHeatingSetpointUp"
        command "setHeatingSetpointDown"
        command "firmware"
        command "device"

    }

    tiles(scale: 2) {

        multiAttributeTile(name: "thermostat", type: "general", width: 6, height: 4, canChangeIcon: false) {
            tileAttribute("device.thermostatMode", key: "PRIMARY_CONTROL") {
                attributeState("off", action: "thermostatMode.auto", label: "closed", backgroundColor: "#FFFFFF", nextState: "auto")
                attributeState("auto", action: "thermostatMode.heat", label: "auto", backgroundColor: "#00A0DC", nextState: "heat")
                attributeState("heat", action: "thermostatMode.off", label: "open", backgroundColor: "#E86D13", nextState: "off")
            }
            tileAttribute("device.heatingSetpoint", key: "VALUE_CONTROL") {
                attributeState("VALUE_UP", action: "setHeatingSetpointUp")
                attributeState("VALUE_DOWN", action: "setHeatingSetpointDown")
            }
            tileAttribute("device.temperature", key: "SECONDARY_CONTROL") {
                attributeState("temperature", label: '${currentValue}°C', unit: "C")
            }
        }

        standardTile("off", "device.thermostatMode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "thermostatMode", action: "thermostatMode.off", icon: "st.vents.vent-closed"
        }

        valueTile("autoColor", "device.heatingSetpoint", inactiveLabel: true, decoration: "flat", width: 2, height: 1) {
            state "heatingSetpoint", label: '',
                    backgroundColors: [
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

        standardTile("auto", "device.heatingSetpoint", inactiveLabel: false, decoration: "flat", width: 2, height: 1) {
            state "heatingSetpoint", action: "thermostatMode.auto", label: 'SET AUTO\n${currentValue}°C', unit: "C"
        }

        valueTile("externalSensorConnected", "device.externalSensorConnected", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "true", label: 'sensor', backgroundColor: "#79b821", icon: "https://raw.githubusercontent.com/Tomas-Mrazek/SmartThings/master/DTH/icons/checkmark.png"
            state "false", label: 'sensor', backgroundColor: "#ff6600", icon: "https://raw.githubusercontent.com/Tomas-Mrazek/SmartThings/master/DTH/icons/xmark.png"
        }

        valueTile("openWindowDetected", "device.openWindowDetected", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "false", label: 'window', backgroundColor: "#79b821", icon: "https://raw.githubusercontent.com/Tomas-Mrazek/SmartThings/master/DTH/icons/window-closed.png"
            state "true", label: 'window', backgroundColor: "#ff6600", icon: "https://raw.githubusercontent.com/Tomas-Mrazek/SmartThings/master/DTH/icons/window-open.png"
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

        standardTile("refresh", "refresh.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "refresh", label: 'refresh', action: "refresh.refresh", icon: "st.secondary.refresh-icon"
        }

        standardTile("firmware", "firmware", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "firmware", label: 'firmware', action: "firmware"
        }

        standardTile("device", "device", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "device", label: 'device', action: "device"
        }

    }

    preferences {
        input name: "pollingInMinutes", title: "How often should device be polled (minutes between 5 and 60)", description: "", type: "number", range: "5..60", required: true, displayDuringSetup: true
        input name: "traceLogging", title: "Trace logging", description: "", type: "bool"
        input name: "overrideScheduleDuration", title: "1. Override Schedule duration (minutes between 10 and 10000)", description: "", type: "number", range: "10..10000"
        input name: "openWindowDetector", title: "2. Open Window Detector", description: "", type: "bool"
        input name: "fastOpenWindowDetector", title: "3. Fast Open Window Detector", description: "", type: "bool"
        input name: "increaseRecieverSensitivity", title: "4. Increase Receiver Sensitivity (shortens battery life)", description: "shortens battery life", type: "bool"
        input name: "ledWhenRemoteControll", title: "5. LED Indications When Controlling Remotely", description: "", type: "bool"
        input name: "protectManualOnOff", title: "6. Protect from setting Full ON and Full OFF mode by turning the knob manually", description: "", type: "bool"
    }
}

/**
 *  SMARTTHINGS INTERNAL
 */
def installed() {
    log.debug "installed()"
    refresh()
}

def updated() {
    if (state.lastUpdated && (now() - state.lastUpdated) < 10000) return
    log.debug "updated()"

    setPolling()

    def paramsString = [settings.openWindowDetector, settings.fastOpenWindowDetector, settings.increaseRecieverSensitivity, settings.ledWhenRemoteControll, settings.protectManualOnOff]
    def params = paramsString.collect({ (it == true) ? 1 : 0 })
    def cmds = []
    cmds << [cmd: zwave.configurationV1.configurationSet(parameterNumber: 1, scaledConfigurationValue: settings.overrideScheduleDuration)]
    cmds << [cmd: zwave.configurationV1.configurationSet(parameterNumber: 2, scaledConfigurationValue: getIntegerFromParams(params))]
    for (int i = 1; i <= 2; i++) {
        cmds << [cmd: zwave.configurationV1.configurationGet(parameterNumber: i)]
    }
    sendHubCommand(encapsulateSequence(cmds, 2000))

    state.lastUpdated = now()
}

def setPolling() {
    log.debug "setPolling()"
    schedule("0 */${settings.pollingInMinutes} * * * ?", polling)
}

def polling() {
    if (state.lastPolling && (now() - state.lastPolling) < 10000) return
    log.debug "polling()"

    def cmds = []
    cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 1]
    cmds << [cmd: zwave.sensorMultilevelV5.sensorMultilevelGet(), endpoint: 2]
    cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 2]
    cmds << [cmd: zwave.configurationV1.configurationGet(parameterNumber: 3)]
    sendHubCommand(encapsulateSequence(cmds, 2000))

    state.lastPolling = now()
}

/**
 *  SMARTTHINGS UX EVENTS
 */
def setHeatingSetpointUp() {
    def setpoint = device.latestValue("heatingSetpoint")
    if (setpoint < 30) {
        setpoint = setpoint + 1
    }
    setHeatingSetpoint(setpoint)
}

def setHeatingSetpointDown() {
    def setpoint = device.latestValue("heatingSetpoint")
    if (setpoint > 10) {
        setpoint = setpoint - 1
    }
    setHeatingSetpoint(setpoint)
}

def firmware() {
    log.debug "firmware()"
    encapsulate(zwave.firmwareUpdateMdV2.firmwareMdGet())
}

def device() {
    log.debug "device()"
    def cmds = []
    cmds << [cmd: zwave.manufacturerSpecificV2.deviceSpecificGet()]
    cmds << [cmd: zwave.manufacturerSpecificV2.manufacturerSpecificGet()]
    encapsulateSequence(cmds, 2000)
}

/**
 *  SMARTTHINGS CAPABILITIES
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
    sendEvent(name: "thermostatMode", value: mode)
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

def setHeatingSetpoint(setpoint) {
    def value
    if (setpoint in BigDecimal) {
        value = setpoint.setScale(0, BigDecimal.ROUND_DOWN)
    } else if (setpoint in Float) {
        value = setpoint.round()
    } else {
        value = setpoint
    }
    sendEvent(name: "heatingSetpoint", unit: "C", value: value)
    encapsulate(zwave.thermostatSetpointV2.thermostatSetpointSet([precision: 1, scale: 0, scaledValue: value, setpointType: 1, size: 2]), 1)
}

def deviceNotification(notification) {
    //TODO
}

def configure() {
    log.debug "configure()"
    def cmds = []
    for (int i = 1; i <= 3; i++) {
        cmds << [cmd: zwave.configurationV1.configurationGet(parameterNumber: i)]
    }
    encapsulateSequence(cmds, 2000)
}

def refresh() {
    log.debug "refresh()"
    def cmds = []
    cmds << [cmd: zwave.thermostatModeV2.thermostatModeGet(), endpoint: 1]
    cmds << [cmd: zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 1), endpoint: 1]
    cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 1]
    cmds << [cmd: zwave.sensorMultilevelV5.sensorMultilevelGet(), endpoint: 2]
    cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 2]
    encapsulateSequence(cmds, 2000)
}

/**
 *  ENCAPSULATION
 */
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
    def commands = cmds.collect { [it.get('cmd'), it.get('endpoint')] }
    delayBetween(commands.collect { encapsulate(it) }, delay)
}

private secureEncapsulate(physicalgraph.zwave.Command cmd) {
    if (settings.traceLogging) {
        log.trace "${device.displayName} - encapsulating command using Secure Encapsulation, command: ${cmd}"
    }
    zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd)
}

private multichannelEncapsulate(physicalgraph.zwave.Command cmd, endpoint) {
    if (settings.traceLogging) {
        log.trace "${device.displayName} - encapsulating command using Multi Channel Encapsulation, command: ${cmd}"
    }
    zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: endpoint).encapsulate(cmd)
}

/**
 *  Z-WAVE DEVICE EVENTS
 */
def parse(String description) {
    def result = null
    def cmd = zwave.parse(description)
    if (cmd) {
        result = zwaveEvent(cmd)
    } else {
        log.warn "${device.displayName} - non-parsed event: ${description}"
    }
    if (settings.traceLogging) {
        log.trace "${device.displayName} – parsed event: ${description}"
    }
    return result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    log.warn "${device.displayName} - parsed unhandled event ${cmd}"
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
    def result = createEvent(descriptionText: "${device.displayName}: ${cmd}")
    log.info "${device.displayName} - parsed event ${cmd} into: ${result}"
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
    def encapsulatedCommand = cmd.encapsulatedCommand(cmdVersions())
    if (encapsulatedCommand) {
        if (settings.traceLogging) {
            log.trace "${device.displayName} - parsed SecurityMessageEncapsulation into: ${encapsulatedCommand}"
        }
        zwaveEvent(encapsulatedCommand)
    } else {
        log.warn "${device.displayName} – unable to extract secure command from $cmd"
        createEvent(descriptionText: cmd.toString())
    }
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
    def encapsulatedCommand = cmd.encapsulatedCommand(cmdVersions())
    if (encapsulatedCommand) {
        if (settings.traceLogging) {
            log.trace "${device.displayName} - parsed MultiChannelCmdEncap into: ${encapsulatedCommand}"
        }
        zwaveEvent(encapsulatedCommand, cmd.sourceEndPoint)
    } else {
        log.warn "${device.displayName} – unable to extract multi channel command from $cmd"
        createEvent(descriptionText: cmd.toString())
    }
}

def zwaveEvent(physicalgraph.zwave.commands.firmwareupdatemdv2.FirmwareMdReport cmd) {
    log.info "${device.displayName} - parsed event ${cmd}"
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.DeviceSpecificReport cmd) {
    log.info "${device.displayName} - parsed event ${cmd}"
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
    log.info "${device.displayName} - parsed event ${cmd}"
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
    log.info "${device.displayName} - parsed event  ${cmd}"
    switch (cmd.parameterNumber) {
        case 1:
            settings.overrideScheduleDuration = cmd.scaledConfigurationValue
            break
        case 2:
            def params = getParamsFromInteger(cmd.scaledConfigurationValue, 5)
            settings.openWindowDetector = params.get(0)
            settings.fastOpenWindowDetector = params.get(1)
            settings.increaseRecieverSensitivity = params.get(2)
            settings.ledWhenRemoteControl = params.get(3)
            settings.protectManualOnOff = params.get(4)
            break
        case 3:
            def params = getParamsFromInteger(cmd.scaledConfigurationValue, 2)
            def result1 = createEvent([name: "settings.externalSensorConnected", value: (params.get(0) == 1) ? "true" : "false"])
            def result2 = createEvent([name: "settings.openWindowDetected", value: (params.get(1) == 1) ? "true" : "false"])
            log.info "${device.displayName} - parsed event ${cmd} into: ${result1} | ${result2}"
            return [result1, result2]
    }
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd, sourceEndPoint = null) {
    def result
    def batteryLevel
    if (cmd.batteryLevel == 255) {
        batteryLevel = 0
    } else {
        batteryLevel = cmd.batteryLevel
    }
    switch (sourceEndPoint) {
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
    def result = createEvent([name: "heatingSetpoint", unit: "C", value: cmd.scaledValue.setScale(0, BigDecimal.ROUND_DOWN)])
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

/**
 *  UTILS
 */
private Map cmdVersions() {
    [
            0x22: 1, //APPLICATION_STATUS
            0x7A: 2, //FIRMWARE_UPDATE_MD
            0x98: 1, //SECURITY 
            0x72: 2, //MANUFACTURER_SPECIFIC
            0x70: 1, //CONFIGURATION
            0x80: 1, //BATTERY
            0x40: 2, //THERMOSTAT_MODE
            0x43: 2, //THERMOSTAT_SETPOINT
            0x31: 5  //SENSOR_MULTILEVEL
    ]
}

private getParamsFromInteger(decimal, numberOfParams) {
    def bit = Math.pow(new Double(2), new Double(numberOfParams - 1))
    def params = []
    for (int i = 0; i < numberOfParams; i++) {
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
    for (int i = 0; i < params.size(); i++) {
        decimal = decimal + params.get(i) * bit
        bit = bit * 2
    }
    return decimal
}