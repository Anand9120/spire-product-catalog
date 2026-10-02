package com.spirelab.productcatalog

import com.spirelab.productcatalog.data.mapper.toCartEntity
import com.spirelab.productcatalog.data.mapper.toDomain
import com.spirelab.productcatalog.data.model.ProductDto
import com.spirelab.productcatalog.domain.model.CartItem
import com.spirelab.productcatalog.domain.model.CartSummary
import com.spirelab.productcatalog.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductMapperAndDomainTest {

    @Test
    fun testProductDtoToDomainMapping() {
        val dto = ProductDto(
            id = 1,
            title = "Essence Mascara Lash Princess",
            description = "The Essence Mascara Lash Princess is a popular mascara.",
            category = "beauty",
            price = 9.99,
            discountPercentage = 7.17,
            rating = 4.94,
            stock = 5,
            brand = "Essence",
            thumbnail = "https://cdn.dummyjson.com/products/images/beauty/Essence%20Mascara%20Lash%20Princess/thumbnail.png"
        )

        val domain = dto.toDomain()

        assertEquals(1, domain.id)
        assertEquals("Essence Mascara Lash Princess", domain.title)
        assertEquals(9.99, domain.price, 0.001)
        assertEquals("$9.99", domain.formattedPrice)
        assertEquals("4.9", domain.formattedRating)
        assertTrue(domain.isInStock)
    }

    @Test
    fun testCartItemCalculation() {
        val item = CartItem(
            productId = 1,
            title = "Sample Item",
            price = 25.50,
            thumbnail = "http://example.com/item.png",
            quantity = 3,
            stock = 10
        )

        assertEquals(76.50, item.totalPrice, 0.001)
        assertEquals("$25.50", item.formattedPrice)
        assertEquals("$76.50", item.formattedTotalPrice)
    }

    @Test
    fun testCartSummaryTotals() {
        val item1 = CartItem(productId = 1, title = "A", price = 10.0, thumbnail = "", quantity = 2)
        val item2 = CartItem(productId = 2, title = "B", price = 15.0, thumbnail = "", quantity = 1)

        val summary = CartSummary(
            totalItems = item1.quantity + item2.quantity,
            totalPrice = item1.totalPrice + item2.totalPrice,
            items = listOf(item1, item2)
        )

        assertEquals(3, summary.totalItems)
        assertEquals(35.0, summary.totalPrice, 0.001)
        assertEquals("$35.00", summary.formattedTotalPrice)
        assertFalse(summary.isEmpty)
    }

    @Test
    fun testEmptyCartSummary() {
        val emptySummary = CartSummary()
        assertTrue(emptySummary.isEmpty)
        assertEquals(0, emptySummary.totalItems)
        assertEquals(0.0, emptySummary.totalPrice, 0.001)
    }
}
