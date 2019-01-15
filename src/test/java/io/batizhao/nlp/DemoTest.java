package io.batizhao.nlp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.model.perceptron.PerceptronSegmenter;
import io.batizhao.nlp.api.ApiController;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author batizhao
 * @since 2018-12-21
 */
public class DemoTest extends NlpApplicationTests {

    @Value("${app.nlp.model.vec}")
    public String vec;

    @Autowired
    private ApiController apiController;

    @Test
    public void testSeg() throws IOException {
        PerceptronSegmenter segmenter = new PerceptronSegmenter();
        System.out.println(segmenter.segment("商品和服务"));

        System.out.println(segmenter.segment("中国电信股份[2005]94号关于印发《中国电信股份有限公司固定资产投资计划管理暂行办法》的通知各省级电信有限公司"));
    }

    @Test
    public void testWord2Vec() throws IOException {
        WordVectorModel wordVectorModel = new WordVectorModel(vec);
        System.out.println(wordVectorModel.similarity("中国电信", "中电信"));
        System.out.println(wordVectorModel.similarity("政企部", "企信部"));

        System.out.println(wordVectorModel.nearest("中国电信"));
        System.out.println(wordVectorModel.nearest("办公室"));

        DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);

        File file_ = new File("/opt/nlp_data/text");
        List<File> files = (List<File>) FileUtils.listFiles(file_, EmptyFileFilter.NOT_EMPTY, DirectoryFileFilter.INSTANCE);

        String data;
        int i = 0;
        for (File f : files) {
            i++;
            data = FileUtils.readFileToString(f, UTF_8);
            docVectorModel.addDocument(i, data);
        }

        System.out.println("============通知=============");
        List<Map.Entry<Integer, Float>> entryList = docVectorModel.nearest("中国电信");
        for (Map.Entry<Integer, Float> entry : entryList) {
            System.out.printf("%d %.2f\n", entry.getKey(), entry.getValue());
        }

        System.out.println(docVectorModel.similarity("中国电信培训〔2013〕20号通知", "中国移动纪要〔2010〕198号"));
        System.out.println(docVectorModel.similarity("软件使用权许可及保密协议", "中国电信培训〔2013〕20号通知"));

    }

    @Test
    public void testKeyword() throws IOException {
        List<Document> documents1 = new ArrayList<>();
        documents1.add(new Document(1, "上海研究院MSS支撑中心领导参会"));
        documents1.add(new Document(2, "奥运会中国女排夺冠"));
        documents1.add(new Document(3, "博格巴让曼联更强大 但他确实不尊重穆帅"));
        documents1.add(new Document(4, "希腊政府外交与国防委员会去年12月中旬批准从美国购买30架F-16战机的计划"));
        documents1.add(new Document(5, "全新宝马4系内饰首曝"));

        List<Document> list = apiController.lookupSimilarDocuments("中国电信培训〔2013〕20号通知", documents1);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list));
    }


    @Test
    public void testExtractMeetingNotice() throws IOException {
//        HanLP.Config.enableDebug();
//        CustomDictionary.reload();
        Map m = apiController.extractMeetingNotice("/opt/nlp_data/text/会议通知〔2011〕305号.txt");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(m));
    }
}
