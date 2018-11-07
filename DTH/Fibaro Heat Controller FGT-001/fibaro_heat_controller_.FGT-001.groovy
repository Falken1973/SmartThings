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
        capability "Configuration"
        capability "Notification"
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
        input name: "pollingInMinutes", title: "How often should device be polled (minutes between 5 and 60)", description: "Default: 10", type: "number", range: "5..60", default: 10, required: true
        input name: "traceLogging", title: "Trace logging", description: "Default: Disabled", type: "enum", options: [true: "Enabled", false: "Disabled"], default: "false", required: true
        input name: "overrideScheduleDuration", title: "1. Override Schedule duration (minutes between 10 and 10000)", description: "Default: 240", type: "number", range: "10..10000", default: 240, required: true
        input name: "openWindowDetector", title: "2. Open Window Detector", description: "Default: Enabled", type: "enum", options: [true: "Enabled", false: "Disabled"], default: "true", required: true
        input name: "fastOpenWindowDetector", title: "3. Fast Open Window Detector", description: "Default: Disabled", type: "enum", options: [true: "Enabled", false: "Disabled"], default: "false", required: true
        input name: "increaseRecieverSensitivity", title: "4. Increase Receiver Sensitivity (shortens battery life)", description: "Default: Disabled", type: "enum", options: [true: "Enabled", false: "Disabled"], default: "false", required: true
        input name: "ledWhenRemoteControl", title: "5. LED Indications When Controlling Remotely", description: "Default: Disabled", type: "enum", options: [true: "Enabled", false: "Disabled"], default: "false", required: true
        input name: "protectManualOnOff", title: "6. Protect from setting Full ON and Full OFF mode by turning the knob manually", description: "Default: Disabled", type: "enum", options: [true: "Enabled", false: "Disabled"], default: "false", required: true
    }
}

/**
 *  SMARTTHINGS INTERNAL
 */
def installed() {
    log.debug "installed()"
    setPolling()
}

def updated() {
    if (state.status == null) {
        log.debug "updated() – installing"
        state.status = "INITIALIZING"
        if (device.displayName != "Fibaro Heat Controller") return
    }

    if (state.status == "INITIALIZING") {
        log.debug "updated() – initializing"
        refresh()
        return
    }

    if (state.lastUpdated && (now() - state.lastUpdated) < 6000) {
        return
    } else if (state.status != "READY") {
        log.debug "updated() – skipped"
        return
    } else {
        log.debug "updated() - ready"
        state.lastUpdated = now()
        state.status = "UPDATING"
        runIn(30, ready)
        def paramsString = [settings.openWindowDetector, settings.fastOpenWindowDetector, settings.increaseRecieverSensitivity, settings.ledWhenRemoteControl, settings.protectManualOnOff]
        def params = paramsString.collect({ (it == "true") ? 1 : 0 })
        def cmds = []
        cmds << [cmd: zwave.configurationV1.configurationSet(parameterNumber: 1, scaledConfigurationValue: settings.overrideScheduleDuration)]
        cmds << [cmd: zwave.configurationV1.configurationSet(parameterNumber: 2, scaledConfigurationValue: getIntegerFromParams(params))]
        cmds << [cmd: zwave.configurationV1.configurationGet(parameterNumber: 1)]
        cmds << [cmd: zwave.configurationV1.configurationGet(parameterNumber: 2)]
        sendHubCommand(encapsulateSequence(cmds, 2000))

        setPolling()
    }
}

def ready() {
    log.debug "ready()"
    state.status = "READY"
}

def setPolling() {
    log.debug "setPolling()"
    int minutes
    if (settings.pollingInMinutes) {
        minutes = settings.pollingInMinutes
    } else {
        minutes = 10
    }
    schedule("0 */${minutes} * * * ?", polling)
}

def polling() {
    if (state.status != "READY") {
        log.debug "polling() – skipped"
        return
    }

    log.debug "polling()"
    state.status = "POLLING"
    runIn(30, ready)
    def cmds = []
    cmds << [cmd: zwave.sensorMultilevelV5.sensorMultilevelGet(), endpoint: 2]
    cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 1]
    cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 2]
    cmds << [cmd: zwave.configurationV1.configurationGet(parameterNumber: 3)]
    sendHubCommand(encapsulateSequence(cmds, 2000))
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

def refresh() {
    if (state.status != "READY" && state.status != "INITIALIZING") {
        log.debug "refresh() – skipped"
        return
    }

    log.debug "refresh()"
    state.status = "REFRESHING"
    runIn(30, ready)
    def cmds = []
    cmds << [cmd: zwave.thermostatModeV2.thermostatModeGet(), endpoint: 1]
    cmds << [cmd: zwave.thermostatSetpointV2.thermostatSetpointGet(setpointType: 1), endpoint: 1]
    cmds << [cmd: zwave.configurationV1.configurationGet(parameterNumber: 3)]
    cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 1]
    cmds << [cmd: zwave.sensorMultilevelV5.sensorMultilevelGet(), endpoint: 2]
    cmds << [cmd: zwave.batteryV1.batteryGet(), endpoint: 2]
    sendHubCommand(encapsulateSequence(cmds, 2000))
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
        log.warn "no encapsulation supported for command: ${cmd}"
        cmd.format()
    }
}

private encapsulateSequence(cmds, delay) {
    def commands = cmds.collect { [it.get('cmd'), it.get('endpoint')] }
    delayBetween(commands.collect { encapsulate(it) }, delay)
}

private secureEncapsulate(physicalgraph.zwave.Command cmd) {
    if (settings.traceLogging == "true") {
        log.trace "encapsulating command using Secure Encapsulation, command: ${cmd}"
    }
    zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd)
}

private multichannelEncapsulate(physicalgraph.zwave.Command cmd, endpoint) {
    if (settings.traceLogging == "true") {
        log.trace "encapsulating command using Multi Channel Encapsulation, command: ${cmd}"
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
        log.warn "non-parsed message: ${description}"
    }
    if (settings.traceLogging == "true") {
        log.trace "parsed message: ${description}"
    }
    return result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    log.warn "parsed unhandled event ${cmd}"
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
    def encapsulatedCommand = cmd.encapsulatedCommand(cmdVersions())
    if (encapsulatedCommand) {
        if (settings.traceLogging == "true") {
            log.trace "parsed SecurityMessageEncapsulation into: ${encapsulatedCommand}"
        }
        zwaveEvent(encapsulatedCommand)
    } else {
        log.warn "unable to extract secure command from $cmd"
        createEvent(descriptionText: cmd.toString())
    }
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
    def encapsulatedCommand = cmd.encapsulatedCommand(cmdVersions())
    if (encapsulatedCommand) {
        if (settings.traceLogging == "true") {
            log.trace "parsed MultiChannelCmdEncap into: ${encapsulatedCommand}"
        }
        zwaveEvent(encapsulatedCommand, cmd.sourceEndPoint)
    } else {
        log.warn "unable to extract multi channel command from $cmd"
        createEvent(descriptionText: cmd.toString())
    }
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
    log.info "parsed event ${cmd}"
}

def zwaveEvent(physicalgraph.zwave.commands.firmwareupdatemdv2.FirmwareMdReport cmd) {
    log.info "parsed event ${cmd}"
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.DeviceSpecificReport cmd) {
    log.info "parsed event ${cmd}"
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
    log.info "parsed event ${cmd}"
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
    if (cmd.parameterNumber == 3) {
        def params = getParamsFromInteger(cmd.scaledConfigurationValue, 2)
        def result1 = createEvent([name: "externalSensorConnected", value: (params.get(0) == 1) ? "true" : "false"])
        def result2 = createEvent([name: "openWindowDetected", value: (params.get(1) == 1) ? "true" : "false"])
        log.info "parsed event ${cmd} into: ${result1} | ${result2}"
        return [result1, result2]
    }
    log.info "parsed event  ${cmd}"
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
    log.info "parsed event ${cmd} into: ${result}"
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport cmd, sourceEndPoint = null) {
    def result = createEvent([name: "heatingSetpoint", unit: "C", value: cmd.scaledValue.setScale(0, BigDecimal.ROUND_DOWN)])
    log.info "parsed event ${cmd} into: ${result}"
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
    log.info "parsed event ${cmd} into: ${result}"
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd, sourceEndPoint = null) {
    def result = createEvent([name: "temperature", unit: "C", value: cmd.scaledSensorValue])
    log.info "parsed event ${cmd} into: ${result}"
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