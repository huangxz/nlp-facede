package io.batizhao.nlp.model;

import io.batizhao.nlp.NlpApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author batizhao
 * @since 2018-12-21
 */
public class PerceptronTest extends NlpApplicationTests {

    @Autowired
    PerceptronModelController perceptronController;

    @Test
    public void testCWS() throws IOException {
        perceptronController.trainCWS();
    }

    @Test
    public void testPOS() throws IOException {
        perceptronController.trainPOS();
    }

    @Test
    public void testNER() throws IOException {
        perceptronController.trainNER();
    }
}
