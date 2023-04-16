import org.springframework.cloud.contract.spec.Contract

/**
 * @author Olga Maciaszek-Sharma
 */

Contract.make {
	label("serving")
	input {
		triggeredBy("triggerServing()")
	}
	outputMessage {
		sentTo("servings")
		body('{"coffees":[{"name":"V60","coffeeContent":500,"device":"V60"},{"name":"Latte","coffeeContent":60,"steamedMilkContent":180,"milkFoamContent":5}]}')
	}
}