/**
 *  Foo
 *
 *  Copyright 2016 Eric Scherrer
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Foo",
    namespace: "escherrer",
    author: "Eric Scherrer",
    description: "Messin' around",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Siren"){
		input "siren", "capability.alarm", title: "Which?", required: true, multiple: false
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
    def theCommands = siren.supportedCommands
    theCommands.each {com ->
        log.debug "Supported Command: ${com.name}"
    }
    def theAtts = siren.supportedAttributes
	theAtts.each {att ->
    	log.debug "Supported Attribute: ${att.name}"
	}
}