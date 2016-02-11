/**
 *  Door Monitor
 *  Based off Siren Beep, Credit: https://raw.githubusercontent.com/KristopherKubicki/smartapp-beep/master/smartapp-beep.groovy
 */
definition(
    name: "Door Monitor",
    namespace: "escherrer",
    author: "escherrer@gmail.com",
    description: "Quickly Pulse a Siren",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan@2x.png")

preferences {
	section("Sirens"){
		input "sirens", "capability.alarm", title: "Which?", required: true, multiple: true
	}
	
    section("Virtual Switch"){
		input "contacts", "capability.contactSensor", title: "Which?", required: true, multiple: true
    }
        
	section("Reminder Interval"){
		input "reminder", "number", title: "Enter Reminder Minutes", defaultValue: 1, required: true, multiple: false
	}
}

def installed() {
   initialized()
}

def updated() {
	unsubscribe()
    initialized()
}

def initialized() {
    subscribe(contacts, "contact", contactHandler)
}

def contactHandler(evt) {

	log.debug "Contact is in ${evt.value} state"

  	if("open" == evt.value) {
        sirens?.siren()
      	sirens?.off()
      	runOnce(new Date(now() + (60000 * reminder)), checkSwitch)
    }
}

def checkSwitch() {
	def currentState = contacts?.currentState("contact")
	def isAnyOpen = false
    
    currentState.each {
    	if (it != null) {
        	if (it.value == "open") {
    			log.debug "Open!"
                isAnyOpen = true
            }
		}
    }
    
    if (isAnyOpen) {
        sirens?.siren()
        sirens?.off()
        runOnce(new Date(now() + (60000 * reminder)), checkSwitch)
    }
}
