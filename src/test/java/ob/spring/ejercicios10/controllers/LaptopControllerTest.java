package ob.spring.ejercicios10.controllers;

import ob.spring.ejercicios10.models.Laptop;
import ob.spring.ejercicios10.models.LaptopRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LaptopControllerTest {

    private static final int ZERO_ELEMENTS = 0;
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    private TestRestTemplate testRestTemplate;
    @LocalServerPort
    private int port;
    @Autowired
    private LaptopRepository repository;

    @BeforeEach
    void setUp() {
        restTemplateBuilder = restTemplateBuilder.rootUri("http://localhost:" + port);
        testRestTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    @Test
    void findAllWithEmptyDB() {
        ResponseEntity<Laptop[]> response = testRestTemplate.getForEntity("/api/laptops", Laptop[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(LaptopControllerTest.ZERO_ELEMENTS, response.getBody().length);
    }

    @Test
    void findAllWithNotEmptyDB(){
        Laptop laptop = new Laptop(10,"HP");
        Laptop lap = repository.save(laptop);
        ResponseEntity<Laptop[]> response = testRestTemplate.getForEntity("/api/laptops", Laptop[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().length);
    }

    @Test
    void findNotExistentOneById() {
        ResponseEntity<Laptop> response = testRestTemplate.getForEntity("/api/laptops/1", Laptop.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void findOneByIdWithNullId() {
        ResponseEntity<Laptop> response = testRestTemplate.getForEntity("/api/laptops/null", Laptop.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void findOneById() {
        Laptop laptop = new Laptop(10,"HP");
        Laptop lap = repository.save(laptop);
        long id = lap.getId();
        ResponseEntity<Laptop> response = testRestTemplate.getForEntity("/api/laptops/" + id, Laptop.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lap.getId(), response.getBody().getId());
    }

    @Test
    void create() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        String laptop = """
                {
                  "autonomy": 20,
                  "trademark": "HP"
                }
                """;
        HttpEntity<String> request = new HttpEntity<>(laptop, headers);
        ResponseEntity<Laptop> response = testRestTemplate.exchange("/api/laptop",HttpMethod.POST,request, Laptop.class);
        Laptop result = response.getBody();
        assertEquals(1, response.getBody().getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createANullLaptop(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<Laptop> response = testRestTemplate.exchange("/api/laptop",HttpMethod.POST,request, Laptop.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateWithNullId() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        String laptop = """
                {
                  "autonomy": 20,
                  "trademark": "HP"
                }
                """;
        HttpEntity<String> request = new HttpEntity<>(laptop, headers);
        ResponseEntity<Laptop> response = testRestTemplate.exchange("/api/laptops", HttpMethod.PUT, request, Laptop.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateWithNullLaptop() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<Laptop> response = testRestTemplate.exchange("/api/laptops", HttpMethod.PUT, request, Laptop.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateNotExistentElement() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        String laptop = """
                {
                  "id":1,
                  "autonomy": 20,
                  "trademark": "HP"
                }
                """;
        HttpEntity<String> request = new HttpEntity<>(laptop, headers);
        ResponseEntity<Laptop> response = testRestTemplate.exchange("/api/laptops", HttpMethod.PUT, request, Laptop.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void update() {
        Laptop lap = repository.save(new Laptop(10,"HP"));
        long id = lap.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        String laptop = """
                {
                  "id": %s,
                  "autonomy": 20,
                  "trademark": "Lenovo"
                }
                """;
        System.out.println(String.format(laptop, id));
        HttpEntity<String> request = new HttpEntity<>(String.format(laptop, id), headers);
        ResponseEntity<Laptop> response = testRestTemplate.exchange("/api/laptops", HttpMethod.PUT, request, Laptop.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(20, repository.findById(id).get().getAutonomy());
    }

    @Test
    void delete() {
        Laptop laptop = repository.save(new Laptop(10,"HP"));
        long id = laptop.getId();
        testRestTemplate.delete("/api/laptops/" + id);
        assertTrue(repository.findById(id).isEmpty());

    }

    @Test
    void deleteWithNullId() {
        Laptop laptop = repository.save(new Laptop(10,"HP"));
        long id = laptop.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>(laptop.toString(), headers);
        ResponseEntity response = testRestTemplate.exchange("/api/laptops/null", HttpMethod.DELETE, request, Laptop.class);

        assertFalse(repository.findById(id).isEmpty());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deleteNotExistentElement() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity response = testRestTemplate.exchange("/api/laptops/1", HttpMethod.DELETE, request, Laptop.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteAll() {
        repository.save(new Laptop(10,"HP"));
        repository.save(new Laptop(10,"HP"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> request = new HttpEntity<>("", headers);
        ResponseEntity response = testRestTemplate.exchange("/api/laptops", HttpMethod.DELETE, request, Laptop.class);

        assertTrue(repository.findAll().isEmpty());
    }

    @AfterEach
    void tearDown() {
        this.repository.deleteAll();
    }
}