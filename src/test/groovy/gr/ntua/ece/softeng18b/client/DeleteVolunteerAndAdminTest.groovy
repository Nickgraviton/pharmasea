package gr.ntua.ece.softeng18b.client


import gr.ntua.ece.softeng18b.client.model.*
import gr.ntua.ece.softeng18b.client.rest.RestCallFormat
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
class DeleteVolunteerAndAdminTest extends Specification {

    @Shared RestAPI api = null
    @Shared def testData = null
    @Shared Product returned = null


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
        String username = "tom"
        String password = "kalinixta"
        api.login(username, password, RestCallFormat.JSON)

        then:
        api.isLoggedIn()
    }

    @Unroll
    def "Post product"() {
        given:
        Product posted = new Product(
            name: "Test product",
            description: "This product is used for testing purposes ",
            category: "Test products",
            tags: ["Tests"]
        )
        returned = api.postProduct(posted, RestCallFormat.JSON)


        expect:
        returned.name == posted.name &&
        returned.description == posted.description &&
        returned.category == posted.category &&
        returned.tags.toSorted() == posted.tags.toSorted() &&
        !returned.withdrawn

    }

    def "delete product as volunteer"(){
        when:
        String id = returned.id
        api.deleteProduct(id,  RestCallFormat.JSON);
        Product withdrawnProduct = api.getProduct(id, RestCallFormat.JSON)

        then:
        noExceptionThrown() 

        expect:
        withdrawnProduct.withdrawn == true &&
        withdrawnProduct.id == id
    }
             
       


    def "logout"() {
        when:
        api.logout(RestCallFormat.JSON)

        then:
        !api.isLoggedIn()
    }

    def "login"() {
        when:
        String username = "seimen42"
        String password = "kalispera"
        api.login(username, password, RestCallFormat.JSON)

        then:
        api.isLoggedIn()
    }

     def "delete product as admin"(){
        when:
        String id = returned.id
        api.deleteProduct(id,  RestCallFormat.JSON);

        then:
        noExceptionThrown() 

    }

    def "is product deleted"() {
        when:
        Product deletedProduct = api.getProduct(returned.id, RestCallFormat.JSON)
        //checkProduct(deletedProduct)        
 
        then:
        def error = thrown(RuntimeException)
        def response = error.message
        response.contains("404")


    }

    def "logout"() {
        when:
        api.logout(RestCallFormat.JSON)

        then:
        !api.isLoggedIn()
    }



}




