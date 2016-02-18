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
    state.reminderCounter = 0
    subscribe(contacts, "contact", contactHandler)
}

def contactHandler(evt) {
	log.debug "Contact changed to ${evt.value} state"
    checkSwitch()
}

def checkSwitch() {
    log.debug "CheckSwitch Begin - ${state.reminderCounter} iteration"
    
    def currentState = contacts?.currentState("contact")
    def isAnyOpen = false
    
    currentState.each {
    	if (it != null) {
        	if (it.value == "open") {
                isAnyOpen = true
            }
        }
    }
    
    if (isAnyOpen && state.reminderCounter < 120) {
        log.debug "There is a contact open, beeping siren."
        
        BeepSiren()
        
        if (state.reminderCounter > 0) {
            pause 1000
            BeepSiren()
        }
        
        def reminderMilliseconds = getReminderMilliseconds()
        def nextRunDate = new Date(now() + reminderMilliseconds)
        state.reminderCounter = state.reminderCounter + 1
        runOnce(nextRunDate, checkSwitch)
        log.debug "Checkswitch requeued to run at ${nextRunDate}"
    }
    else
    {
        log.debug "No contacts open."
        state.reminderCounter = 0
    }
    
    log.debug "CheckSwitch End"
}

def getReminderMilliseconds() {
    def reminderMilliseconds = 60000 * reminder
    log.debug "Reminder milliseconds is ${reminderMilliseconds}"
    return reminderMilliseconds
}

def BeepSiren() {
    Short duration = 0
    sirens?.chime(duration)
}