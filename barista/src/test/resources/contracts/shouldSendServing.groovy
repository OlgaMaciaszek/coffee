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
					  ]
		])
//		)
////		body("test")
//		body(
//				"""
//"uuid":"$(anyAlphaUnicode())","coffee":{"name":"V60","coffeeContent":500,"device":"V60"}}
//"""
//		)
//
//		"""
//{"beverages":[{,{"uuid":"351e0d8e-a849-4d6a-8538-62fb509a945e","coffee":{"name":"Latte","coffeeContent":60,"steamedMilkContent":180,"milkFoamContent":5}},{"uuid":"f49b8cb4-1be1-4a9f-8a3f-f240b62c272d","coffee":{"name":"V60","coffeeContent":500,"device":"V60"}}]}
//
//
//"""
//		] )

//		body($(consumer('"coffees":{"8c6fd028-7d45-42e3-b9b8-f97898471a66":{"name":"V60","coffeeContent":500,"device":"V60"}, "36c5b590-711b-4ab3-9b26-d3994ad79c94":{"name":"Latte","coffeeContent":60,"steamedMilkContent":180,"milkFoamContent":5}'),
//				producer(
//						regex('"coffees":{".*":{"name":"V60","coffeeContent":500,"device":"V60"},".*":{"name":"Latte","coffeeContent":60,"steamedMilkContent":180,"milkFoamContent":5}'))))

//		bodyMatchers {
//			jsonPath('$.beverages.uuid', byRegex(RegexPatterns.uuid()))
//		}
	}

}