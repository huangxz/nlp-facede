package io.batizhao.nlp;

import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 向量模型的加载比较花时间，这里在 App 启动后先初始化。
 * @author batizhao
 * @since 2019-01-08
 */
@Component
public class WordVectorModelInitiator implements ApplicationRunner {

    private static Logger LOG = LoggerFactory.getLogger(WordVectorModelInitiator.class);

    private WordVectorModel wordVectorModel;

    @Value("${app.nlp.model.vec}")
    public String vec;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LOG.info("666");
        wordVectorModel = new WordVectorModel(vec);
        LOG.info(wordVectorModel.toString());
    }

    public WordVectorModel getWordVectorModel() {
        return wordVectorModel;
    }
}
