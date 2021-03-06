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
    public void testParseDir() throws IOException {
        Assert.assertTrue(word2VecController.parseDir("/opt/nlp_data/text/jituangongsifawen"));
    }

    @Test
    public void testParse() {
        Assert.assertTrue(word2VecController.parseSingleFile("/opt/nlp_data/text/发文.txt"));
    }
}
