package io.batizhao.nlp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NlpApplication {

//    private static Logger LOG = LoggerFactory.getLogger(NlpApplication.class);
//
//    @Value("${app.nlp.model.vec}")
//    public String vec;
//
//    @Bean
//    public CommandLineRunner commandLineRunner() {
//        return args -> {
//
//            LOG.info("STARTING THE APPLICATION ...");
//            WordVectorModel wordVectorModel = new WordVectorModel(vec);
//            LOG.info("APPLICATION FINISHED ...");
//
//        };
//    }

    public static void main(String[] args) {
        SpringApplication.run(NlpApplication.class, args);
    }
}
