package io.batizhao.nlp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.CompoundWord;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.model.perceptron.PerceptronSegmenter;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hankcs.hanlp.tokenizer.pipe.LexicalAnalyzerPipeline;
import io.batizhao.nlp.api.ApiController;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author batizhao
 * @since 2018-12-21
 */
public class DemoTest extends NlpApplicationTests {

    @Value("${app.nlp.model.vec}")
    public String vec;

    @Value("${app.nlp.corpus.v2}")
    public String corpus_v2;

    @Autowired
    private ApiController apiController;

    @Test
    public void testSeg() throws IOException {
        PerceptronSegmenter segmenter = new PerceptronSegmenter();
        System.out.println(segmenter.segment("商品和服务"));

        System.out.println(segmenter.segment("转发最高人民法院关于审理破坏公用电信设施刑事案件具体应用法律若干问题解释的通知"));

        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer();
        System.out.println(analyzer.analyze("转发最高人民法院关于审理破坏公用电信设施刑事案件具体应用法律若干问题解释的通知"));
    }

    @Test
    public void testWord2Vec() throws IOException {
        WordVectorModel wordVectorModel = new WordVectorModel(vec);
        System.out.println(wordVectorModel.similarity("杨杰", "王晓初"));

        System.out.println(wordVectorModel.nearest("信息产业部"));
        System.out.println(wordVectorModel.nearest("杨杰"));
        System.out.println(wordVectorModel.nearest("上海市"));

        DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);
        System.out.println(docVectorModel.similarity("关于2012年第二批客户端适配费用的批复", "中国电信浙江公司关于申请翼聊2012年第二批终端适配开发费用的请示"));
        System.out.println(docVectorModel.similarity("关于2012年第二批客户端适配费用的批复", "关于申请2012年第二批天翼空间客户端适配费用的请示"));
        System.out.println(docVectorModel.similarity("关于2012年第二批客户端适配费用的批复", "关于2012年度第二批爱动漫产品终端适配费用的请示"));
        System.out.println(docVectorModel.similarity("关于2012年第二批客户端适配费用的批复", "中国电信浙江公司关于申请2012年天翼阅读客户端软件第二批终端适配项目费用的请示"));

    }

    @Test
    public void testSimilar() {
//        List<Document> documents1 = new ArrayList<>();
//        documents1.add(new Document(1, "转发住房城乡建设部与工业和信息化部关于贯彻落实光纤到户国家标准的通知"));
//        documents1.add(new Document(2, "转发中央宣传部、司法部、全国普法办关于开展“学习宪法 尊法守法”主题活动的通知"));
//        documents1.add(new Document(3, "中国电信集团公司关于中国电信（香港）国际有限公司更名的请示"));
//        documents1.add(new Document(4, "中国电信集团公司关于中朝边境频率干扰问题的报告"));
//        documents1.add(new Document(5, "中国电信集团公司关于增配移动转售专用码号资源的请示"));
//        documents1.add(new Document(6, "中国电信集团公司关于王晓初董事长申办赴港签注的请示"));

        try(Stream<String> lines = Files.lines(Paths.get("/opt/nlp/data/documents/发文标题.txt"), UTF_8)) {
            List<Document> documents1 = new ArrayList<>();

            lines.forEachOrdered(new Consumer<String>() {
                int i = 0;
                @Override
                public void accept(String line) {
                    i ++;
                    documents1.add(new Document(i, line));
                }
            });

            List<Document> list = apiController.lookupSimilarDocuments("关于2012年第二批客户端适配费用的批复", documents1);
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Test
    public void testExtractMeetingNotice() throws IOException {
//        HanLP.Config.enableDebug();
//        CustomDictionary.reload();
        Map m = apiController.extractMeetingNotice("/opt/nlp/data/text/会议通知〔2011〕305号.txt");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(m));
    }

    @Test
    public void testExtractKeyword() throws IOException {
        List<String> s;
        String[] testCase = new String[]
                {
                        "转发信息产业部国家发展和改革委员会关于调整部分电信业务资费管理方式的通知",
                        "关于表彰2016年度中国电信客户服务职业技能竞赛优秀单位和个人的决定",
                        "转发中央宣传部司法部全国普法办关于组织开展“六五”普法中期检查督导的通知",
                        "转发中央企业境外国有资产监督管理暂行办办法和中央企业境外国有产权管理暂行办法的通知"
                };

        for (String data : testCase)
        {
            s = apiController.extractKeyword(data, 5);
            System.out.println(s);
        }

    }

    @Test
    public void testNumberAndQuantifierRecognition() {
        StandardTokenizer.SEGMENT.enableNumberQuantifierRecognize(true);
        List<Term> terms = StandardTokenizer.segment("同意以货币资金方式增加对你公司投资2.5亿元人民币，增资后公司注册资本达到3.9亿元人民币。");

        for (Term term : terms)
        {
            if (term.nature == Nature.fromString("mq"))
                System.out.printf("找到了 [%s] : %s\n", "资金", term.word);
        }

        terms = StandardTokenizer.segment("同意福富软件公司以自有货币资金投资4000万元在厦门软件园设立福富（厦门）信息技术有限公司");

        for (Term term : terms)
        {
            if (term.nature == Nature.fromString("mq"))
                System.out.printf("找到了 [%s] : %s\n", "资金", term.word);
        }
    }

    @Test
    public void testDateExtractor() throws IOException {
        LexicalAnalyzerPipeline analyzer = new LexicalAnalyzerPipeline(new PerceptronLexicalAnalyzer());

        String[] testCase = new String[]
                {
                        "中国电信股份有限公司综合部2008年1月8日印发",
                        "二○○八年一月八日拟文部门",
                        "请你公司于2008年1月30日之前，将以上问题的整改情况书面报告股份公司",
                        "自2007年11月19日至30日，对你公司的西安、汉中分公司的内控缺陷整改情况及重点业务流程的内部控制执行情况进行了评估",
                        "汉中分公司重点领域内部控制截至2007年10月30日执行情况的评估",
                        "关于表彰2016年度中国电信客户服务职业技能竞赛优秀单位和个人的决定"
                };

        for (String data : testCase)
        {
            System.out.println(analyzer.analyze(data));

            Sentence sentence = analyzer.analyze(data);

            for (IWord words : sentence.wordList)
            {
                if (words.getLabel().equals("t"))
                    System.out.printf("找到了 [%s] : %s\n", "时间", words.getValue());
            }

            System.out.println("-------------");
        }

    }
}
