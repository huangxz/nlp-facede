package io.batizhao.nlp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.BaseSearcher;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.model.crf.CRFSegmenter;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.model.perceptron.PerceptronSegmenter;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hankcs.hanlp.tokenizer.pipe.LexicalAnalyzerPipeline;
import com.hankcs.hanlp.tokenizer.pipe.Pipe;
import com.hankcs.hanlp.tokenizer.pipe.RegexRecognizePipe;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author batizhao
 * @since 2018-12-21
 */
public class DemoTest extends NlpApplicationTests {

    @Value("${app.nlp.model.vec}")
    public String vec;

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
        WordVectorModel wordVectorModel = new WordVectorModel(vec);
        DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);

        File file_ = new File("/opt/nlp_data/text/资产管理专项工作组会议通知（201009）.txt");
//        List<File> files = (List<File>) FileUtils.listFiles(file_, EmptyFileFilter.NOT_EMPTY, DirectoryFileFilter.INSTANCE);

        String filePath;
        List<String> data = FileUtils.readLines(file_, UTF_8);
        int i = 0;
        for (String s : data) {
            i++;
            docVectorModel.addDocument(i, s);
        }

        System.out.println("============会议时间=============");
        List<Map.Entry<Integer, Float>> entryList = docVectorModel.nearest("会议时间");
        for (Map.Entry<Integer, Float> entry : entryList) {
            System.out.printf("%d %.2f\n", entry.getKey(), entry.getValue());
        }
    }


    @Test
    public void testCustomDictionary() throws IOException {

//        HanLP.Config.enableDebug();
        CustomDictionary.reload();

        File file = new File("/opt/nlp_data/text/会议通知〔2011〕305号.txt");
        String text = FileUtils.readFileToString(file, UTF_8);

        List<Term> terms = HanLP.newSegment().enableCustomDictionaryForcing(true).seg(text);

        //在词前增加分隔符用来 split
        Nature pcNature = Nature.fromString("HYTZ");
        for (Term term : terms) {
            if (term.nature == pcNature) term.word = "|" + term.word;
        }

        //去除首尾中括号
        String data = terms.toString().replaceAll("[\\[\\]]", "");
        System.out.println("s1" + data);

        //必须用 StringUtils 才能过滤开头的空格
        String[] strs = StringUtils.split(data, "|");
        System.out.println("s2" + Arrays.toString(strs));

        Map<String, String> m = new HashMap<>();
        String ms0, ms1;
        for (String s : strs) {
            String[] ms = StringUtils.splitByWholeSeparator(s,"/HYTZ, ");
            System.out.println("s3" + Arrays.toString(ms));

            ms0 = ms[0];
            ms1 = ms[1];
            ms1 = ms1.replaceAll("/\\w+,*\\s*", "");

            m.put(ms0, ms1);
        }

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(m));
    }
}
