package io.batizhao.nlp.model;

import com.hankcs.hanlp.mining.word2vec.Word2VecTrainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author batizhao
 * @since 2018-12-11
 */
@RestController
@RequestMapping("model")
public class Word2VecModelController {

    @Value("${app.nlp.corpus.v2}")
    public String corpus_v2;

    @Value("${app.nlp.model.vec}")
    public String vec;

    @PostMapping("w2v")
    public void trainWord2Vec() {
        Word2VecTrainer trainerBuilder = new Word2VecTrainer();
        trainerBuilder.train(corpus_v2, vec);
    }
}
