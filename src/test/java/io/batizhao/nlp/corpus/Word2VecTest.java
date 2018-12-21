package io.batizhao.nlp.corpus;

import io.batizhao.nlp.NlpApplicationTests;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author batizhao
 * @since 2018-12-21
 */
public class Word2VecTest extends NlpApplicationTests {

    @Autowired
    Word2VecController word2VecController;

    @Test
    public void testParse() throws IOException {
        Assert.assertTrue(word2VecController.parse("/opt/nlp_data/text"));
    }
}
