package io.batizhao.nlp.model;

import com.hankcs.hanlp.model.perceptron.CWSTrainer;
import com.hankcs.hanlp.model.perceptron.NERTrainer;
import com.hankcs.hanlp.model.perceptron.POSTrainer;
import com.hankcs.hanlp.model.perceptron.PerceptronTrainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author batizhao
 * @since 2018-12-11
 */
@RestController
@RequestMapping("model")
public class PerceptronModelController {

    private static Logger LOG = LoggerFactory.getLogger(PerceptronModelController.class);

    @Value("${app.nlp.corpus.v1}")
    public String corpus_v1;

    @Value("${app.nlp.model.cws}")
    public String cws;

    @Value("${app.nlp.model.pos}")
    public String pos;

    @Value("${app.nlp.model.ner}")
    public String ner;

    @PostMapping("cws")
    public void trainCWS() throws IOException {
        PerceptronTrainer trainer = new CWSTrainer();
        PerceptronTrainer.Result result = trainer.train(corpus_v1, cws);
        LOG.info("准确率F1: {}", result.getAccuracy());
    }

    @PostMapping("pos")
    public void trainPOS() throws IOException {
        PerceptronTrainer trainer = new POSTrainer();
        PerceptronTrainer.Result result = trainer.train(corpus_v1, pos);
        LOG.info("准确率F1: {}", result.getAccuracy());
    }

    @PostMapping("ner")
    public void trainNER() throws IOException {
        PerceptronTrainer trainer = new NERTrainer();
        PerceptronTrainer.Result result = trainer.train(corpus_v1, ner);
        LOG.info("准确率F1: {}", result.getAccuracy());
    }

}
