/**
 *                           MIT License
 *
 *                 Copyright (c) 2019 Amr Elghobary
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.amr.travelmantics.data

import org.junit.After
import org.junit.Before

import org.junit.Test

class TravelsRepositoryTest {

    // Class under test
    private lateinit var travelsRepository: TravelsRepository

    private val travel_lasVegas = Deal(
        "234242",
        "Las Vegas",
        150000,
        "Las Vegas Holiday Resort",
        "www.link.com"
    )

    private val travel_bananaResort = Deal(
        "2232",
        "Banana Resort",
        500000,
        "Deal to Banana Resort",
        "www.another-link.com"
    )

    private val loadedTravels = listOf(travel_lasVegas, travel_bananaResort)
    private val error = Exception("Couldn't load data")

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getTravels_travelsAvailable() {
    }

    @Test
    fun getTravels_errorHappens() {
    }

    @Test
    fun getTravels_noTravelsAdded() {
    }
}