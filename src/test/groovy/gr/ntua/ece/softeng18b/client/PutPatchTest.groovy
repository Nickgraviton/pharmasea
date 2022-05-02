package gr.ntua.ece.softeng18b.client


import gr.ntua.ece.softeng18b.client.model.*
import gr.ntua.ece.softeng18b.client.rest.RestCallFormat
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
class PutPatchTest extends Specification {

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

    def "login"() {
        when:
        String username = "seimen42"
        String password = "kalispera"
        api.login(username, password, RestCallFormat.JSON)

        then:
        api.isLoggedIn()
    }

    //Goto https://localhost:8765/observatory/api/products to see which products are available


    //Fully updates product with id 11
    @Unroll
    def "put product with id 11"() {
        given:
        Product updated = new Product(
            name: "Lexotanil",
            description: "Lexotanil Tablet is used for Anxiety, Insomnia and other conditions. ",
            category: "Anxiety Medication",
            tags: ["Anxiety"]
        )
        String id = "11"
        Product returned = api.putProduct(id,updated, RestCallFormat.JSON)


        expect:
        returned.name == updated.name &&
        returned.description == updated.description &&
        returned.category == updated.category &&
        returned.tags.toSorted() == updated.tags.toSorted() &&
        !returned.withdrawn

    }

    //Partly updates product with id 11
    @Unroll
    def "Patch product with id 11"() {
        given:
        String id = "11"
        Product returned = api.patchProduct(id, field, value, RestCallFormat.JSON)


        expect:
        getProductValue(returned,field) == value

        where:
        field          | value
        "name"         | "Xanax1"
        "description"  | "It is most commonly used in short term management of anxiety disorders"
        "category"     | "Anxiety Medication"
        "tags"         | ["Anxiety"]

    }


    def "logout"() {
        when:
        api.logout(RestCallFormat.JSON)

        then:
        !api.isLoggedIn()
    }

    def getProductValue(product, field) {
        switch(field) { 
          case "name":
          return product.name;

          case "description":
          return product.description;

          case "category":
          return product.category;

          case "tags":
          return product.tags;
        } 
    }



}


