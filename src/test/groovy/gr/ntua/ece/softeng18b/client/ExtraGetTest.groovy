package gr.ntua.ece.softeng18b.client


import gr.ntua.ece.softeng18b.client.model.*
import gr.ntua.ece.softeng18b.client.rest.RestCallFormat
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll


class ExtraGetTest extends Specification {

    @Shared RestAPI api = null
    @Shared def testData = null


    def "initialize api client"(){
        when:
        String host     = "localhost"
        String port     = "8765"
        String protocol = "https"
        api = new RestAPI(host, port as Integer, protocol == 'https')

        then:
        noExceptionThrown()
    }



    @Unroll
    def "Check that product attributes are of the correct size"() {
        given:
        ProductList list = api.getProducts(
            0,
            10,
            "ACTIVE",
            "name|DESC",
            RestCallFormat.JSON
        )

        expect:
        list.start == 0 &&
        list.products.size() <= 10
        


    }

    @Unroll
    def "Get a product with a specific id"() {
        given:
        Product product = api.getProduct(id , RestCallFormat.JSON)

        expect:
        product.id == id 

        where:
        id  | _
        "1" | _
        "2" | _
        "3" | _
        "4" | _        

    }


    @Unroll
    def "Check that products are sorted correctly when sorted ascending"() {
        given:
        ProductList list = api.getProducts(
            0,
            10,
            "ACTIVE",
            sort,
            RestCallFormat.JSON
        )

        expect:
        list.start == 0 &&
        isAscSorted(getProductValues(list, value),value)        

        where:
        sort        | value 
        "name|ASC"  | "name"
        "id|ASC"    | "id"

    }

    @Unroll
    def "Check that products are sorted correctly when sorted descending"() {
        given:
        ProductList list = api.getProducts(
            0,
            10,
            "ACTIVE",
            sort,
            RestCallFormat.JSON
        )

        expect:
        list.start == 0 &&
        isDescSorted(getProductValues(list, value),value)        

        where:
        sort        | value
        "name|DESC"  | "name"
        "id|DESC"    | "id"

    }

    def getProductValues(list, value) {
        if(value == "name"){
            return list.products.name
        }
        if(value == "id"){
            return list.products.id
        }
    }

    
    def isAscSorted(list, value) {
        if(value == "id"){
           (1..< list.size()).every { list[it - 1].toInteger() <= list[it].toInteger()  }
        }
        else if(value == "name"){
           (1..< list.size()).every { list[it - 1] <= list[it]  }
        }
    }


    def isDescSorted(list, value) {
         if(value == "id"){
           (1..< list.size()).every { list[it - 1].toInteger() >= list[it].toInteger()  }
        }
        else if(value == "name"){
           (1..< list.size()).every { list[it - 1] >= list[it]  }
        }
    }
}


