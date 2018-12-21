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
public class PerceptronTest extends NlpApplicationTests {

    @Autowired
    PerceptronController perceptronController;

    @Test
    public void testParse() throws IOException {
        Assert.assertTrue(perceptronController.parse("/opt/nlp_data/text"));
    }
}
