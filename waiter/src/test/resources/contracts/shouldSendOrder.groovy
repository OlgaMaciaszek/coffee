package contracts

import org.springframework.cloud.contract.spec.Contract

/**
 * @author Olga Maciaszek-Sharma
 */

Contract.make {
	label("order")
	input {
		triggeredBy("triggerOrder()")
	}
	outputMessage {
		sentTo("orders")
		body([entries:
					  [[beverageName: "v60",
						count       : 8
					   ],
					   [beverageName: "latte",
						count       : 6
					   ]
					  ]])
	}

}