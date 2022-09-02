package ob.spring.ejercicios10.controllers;

import ob.spring.ejercicios10.models.Laptop;
import ob.spring.ejercicios10.models.LaptopRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LaptopController {
	private final LaptopRepository repository;
	@Value("${app.greetings}")
	private String greetings;
	
	public LaptopController(LaptopRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/")
	public ResponseEntity<String> home(){
		return ResponseEntity.ok(this.greetings);
	}
	
	@GetMapping("/api/laptops")
	public ResponseEntity<List<Laptop>> findAll(){
		return ResponseEntity.ok(this.repository.findAll());
	}

	@GetMapping("/api/laptops/{id}")
	public ResponseEntity<Laptop> findOneById(@PathVariable Long id){
		if (id == null){
			return ResponseEntity.badRequest().build();
		}
		if (this.repository.findById(id).isEmpty()){
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(this.repository.findById(id).get());
	}

	@PostMapping("/api/laptop")
	public ResponseEntity<Laptop> create(@RequestBody Laptop laptop){
		if (laptop == null){
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(this.repository.save(laptop));
	}

	@PutMapping("/api/laptops")
	public ResponseEntity update(@RequestBody Laptop laptop) {
		if (laptop == null || laptop.getId() == null){
			return ResponseEntity.badRequest().build();
		}
		if (this.repository.findById(laptop.getId()).isEmpty()){
			return ResponseEntity.notFound().build();
		}
		this.repository.save(laptop);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/api/laptops/{id}")
	public ResponseEntity delete(@PathVariable Long id) {
		if (id == null){
			return ResponseEntity.badRequest().build();
		}
		if (this.repository.findById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		this.repository.delete(this.repository.findById(id).get());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/api/laptops")
	public ResponseEntity deleteAll(){
		this.repository.deleteAll();
		return ResponseEntity.ok().build();
	}
}
