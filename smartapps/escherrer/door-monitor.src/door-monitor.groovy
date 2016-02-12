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
	log.debug "Contact changed to ${evt.value} state"

  	if(evt.value == "open") {
    	log.debug "Beginning switch check"
        checkSwitch()
    }
}

def checkSwitch() {
	log.debug "CheckSwitch Begin"
    
	def currentState = contacts?.currentState("contact")
	def isAnyOpen = false
    
    currentState.each {
    	if (it != null) {
        	if (it.value == "open") {
                isAnyOpen = true
            }
        }
    }
    
    if (isAnyOpen) {
        log.debug "There is a contact open, beeping siren."
        BeepSiren()
        def reminderMilliseconds = getReminderMilliseconds()
        runOnce(new Date(now() + reminderMilliseconds), checkSwitch)
        log.debug "Reminder set for ${reminderMinutes} milliseconds."
    }
    
    log.debug "CheckSwitch End"
}

def getReminderMilliseconds() {
	60000 * reminder
}


def BeepSiren() {
    sirens?.siren()
    sirens?.off()
    sirens?.off()
}