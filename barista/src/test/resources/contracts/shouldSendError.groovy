package contracts

import org.springframework.cloud.contract.spec.Contract

/**
 * @author Olga Maciaszek-Sharma
 */

Contract.make {
	label("error")
	input {
		triggeredBy("triggerError()")
	}
	outputMessage {
		sentTo("errors")
		body([
				message: "We currently do not have the following coffee in our menu: expresso"
		])
	}
}