package io.batizhao.nlp;

import com.hankcs.hanlp.HanLP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class NlpApplication {

//    private static Logger LOG = LoggerFactory.getLogger(NlpApplication.class);
//
//    @Value("${app.parser.source}")
//    public String source;
//
//    @Value("${app.parser.target}")
//    public String target;
//
//    @Bean
//    public CommandLineRunner commandLineRunner() {
//        return args -> {
//
//            LOG.info("STARTING THE APPLICATION ...");
//            SegmentController segment = new SegmentController();
//            segment.parse(source, target);
//            LOG.info("APPLICATION FINISHED ...");
//
//        };
//    }

    public static void main(String[] args) {
        SpringApplication.run(NlpApplication.class, args);
//        HanLP.Config.enableDebug();
    }
}
