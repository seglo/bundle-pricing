package seglo

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class OrderApiSpecs extends org.specs2.mutable.Specification {
  // TODO:
  // add a beforeEach to instantiate Api
  // put common test data somewhere else?
  val apple = CatalogItem("Apple", BigDecimal(1.50))
  val banana = CatalogItem("Banana", BigDecimal(1.00))
  val grapefruit = CatalogItem("Grapefruit", BigDecimal(3.00))

  val catalog = Set(apple, banana, grapefruit)

  val doubleAppleBundle = Bundle("Double Apple Bundle",
    Seq(apple, apple), BigDecimal(2.00))
  val appleBananaBundle = Bundle("Apple & Banana Bundle",
    Seq(apple, banana), BigDecimal(2.00))
  val threeGrapefruitBundle = Bundle("Three Grapefruit Bundle",
    Seq(grapefruit, grapefruit, grapefruit), BigDecimal(6.00))

  val bundles = Set(doubleAppleBundle, appleBananaBundle, threeGrapefruitBundle)

  val appleDto = CatalogItemDto("Apple")
  val bananaDto = CatalogItemDto("Banana")
  val grapefruitDto = CatalogItemDto("Grapefruit")

  val orderDto = OrderDto(Seq(appleDto, bananaDto, grapefruitDto))

  "Checkout an order with an applicable bundle and return its total successfully" >> {
    val orderApi = new OrderApi(catalog, bundles)

    val checkoutFuture = orderApi.checkout(orderDto)

    Await.result(checkoutFuture, Duration.Inf) mustEqual BigDecimal(5.00)
  }

  "Checkout an order with no catalog items" >> {
    val orderApi = new OrderApi(catalog, bundles)

    val checkoutFuture = orderApi.checkout(OrderDto(Nil))

    Await.result(checkoutFuture.failed, Duration.Inf) mustEqual
      InvalidOrderException("Your order contains no items.")
  }

  "Checkout an order with invalid catalog items: Guava & Pomplamouse" >> {
    val orderApi = new OrderApi(catalog, bundles)

    val checkoutFuture = orderApi.checkout(OrderDto(Seq(
      CatalogItemDto("Guava"),
      CatalogItemDto("Pamplemousse"))))

    Await.result(checkoutFuture.failed, Duration.Inf) mustEqual
      InvalidOrderException("These catalog items do not exist: Guava, Pamplemousse.")
  }
}
