/**
 *  Siren Beep
 *  Credit: https://raw.githubusercontent.com/KristopherKubicki/smartapp-beep/master/smartapp-beep.groovy
 */
definition(
    name: "Siren Beep",
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
  if("open" == evt.value) {
    // contact was opened, turn on a light maybe?
    	log.debug "Contact is in ${evt.value} state"
        sirens?.siren()
      	sirens?.off()
      	runOnce(new Date(now() + 10000), checkSwitch)
    }
  if("closed" == evt.value)
    // contact was closed, turn off the light?
    log.debug "Contact is in ${evt.value} state"
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
        runOnce(new Date(now() + 10000), checkSwitch)
    }
}
