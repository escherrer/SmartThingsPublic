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
        input "sirens", "capability.alarm", title: "Which?", required: false, multiple: true
    }
	
    section("Virtual Switch"){
        input "contacts", "capability.contactSensor", title: "Which?", required: true, multiple: true
    }
        
    section("Reminder Interval"){
        input "beepReminder", "number", title: "Enter Beep Reminder Minutes", defaultValue: 1, required: true, multiple: false
        input "beepOnOpen", "bool", title: "Beep when opened?", defaultValue: true, required: true, multiple: false
        input "notifyReminder", "number", title: "Send Notification After X Reminders", defaultValue: 120, required: true, multiple: false
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
    def openDoor
    
    currentState.each {
    	if (it != null) {
        	if (it.value == "open") {
                isAnyOpen = true
                openDoor = it
            }
        }
    }
    
    if (isAnyOpen && state.reminderCounter < 120) {
        log.debug "There is a contact open, beeping siren."
        
        if (beepOnOpen) {
            BeepSiren()
        }
        
        if (state.reminderCounter > 0) {
            pause 1000
            BeepSiren()
            
            if (state.reminderCounter == notifyReminder) {
            	sendPush("The ${openDoor.displayName} is open!")
            }
        }
        
        def reminderMilliseconds = getBeepReminderMilliseconds()
        def nextRunDate = new Date(now() + reminderMilliseconds)
        state.reminderCounter = state.reminderCounter + 1
        runOnce(nextRunDate, checkSwitch)
        log.debug "checkSwitch requeued to run at ${nextRunDate}"
    }
    else
    {
        log.debug "No contacts open."
        state.reminderCounter = 0
    }
    
    log.debug "CheckSwitch End"
}

def getBeepReminderMilliseconds() {
    def reminderMilliseconds = 60000 * beepReminder
    log.debug "Beep Reminder milliseconds is ${reminderMilliseconds}"
    return reminderMilliseconds
}

def BeepSiren() {
    Short duration = 0
    sirens?.chime(duration)
}