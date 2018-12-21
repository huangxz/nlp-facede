package io.batizhao.nlp.model;

import io.batizhao.nlp.NlpApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author batizhao
 * @since 2018-12-21
 */
public class Word2VecTest extends NlpApplicationTests {

    @Autowired
    Word2VecModelController word2VecModelController;

    @Test
    public void testCWS() {
        word2VecModelController.trainWord2Vec();
    }

}
