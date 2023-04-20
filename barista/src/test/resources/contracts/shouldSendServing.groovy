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
		body([beverages:
					  [[uuid  : $(anyUuid()),
						coffee: [
								name         : "V60",
								coffeeContent: "500",
								device       : "V60"
						]],
					   [uuid  : $(anyUuid()),
						coffee: [
								name              : "Latte",
								coffeeContent     : "60",
								steamedMilkContent: "180",
								milkFoamContent   : "5"
						]]
		]])
	}

}