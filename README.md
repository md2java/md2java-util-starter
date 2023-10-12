##m2java-util-starter
---------------------------
	
	This is util starter with logging request and response along with time taken by method also
	base on the annotation on method.
	
	How to use it
	-------------------
	pre-requisite:
	---------------------
	1) consumer must be spring boot application
	2) consumer must use minimum jdk8
	 
	 3 step to  get benefit of it.
	---------------------------------
	1) add dependency latest one
	2) use @EnableM2JavaUtil
	3) use @LogMethodInfo on business method 
	
	
    example:
    -------------
    @SpringBootApplication
	@Slf4j
	@EnableM2JavaUtil
	@RestController
	public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@GetMapping("/hello")
	@LogMethodInfo()
	public String hello() {
		log.info("hello Rest....");
		return "Hello";
	}
	
}	
	


