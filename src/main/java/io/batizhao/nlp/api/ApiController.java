package io.batizhao.nlp.api;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author batizhao
 * @since 2018-12-24
 */
@RestController
@RequestMapping("api")
public class ApiController {

    private static Logger LOG = LoggerFactory.getLogger(ApiController.class);

    @Value("${app.nlp.model.vec}")
    public String vec;

    @PostMapping("similar")
    public void lookupSimilarDocuments() throws IOException {
        WordVectorModel wordVectorModel = new WordVectorModel(vec);
        DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);

        File file_ = new File("/opt/nlp_data/text");
        List<File> files = (List<File>) FileUtils.listFiles(file_, EmptyFileFilter.NOT_EMPTY, DirectoryFileFilter.INSTANCE);

        String filePath, data;
        int i = 0;
        for (File f : files) {
            i++;
            filePath = f.getPath();
            data = FileUtils.readFileToString(f, UTF_8);
            docVectorModel.addDocument(i, data);
        }

    }

    /**
     * 提取会议通知内容
     * @param path 文件路径
     * @return
     * @throws IOException
     */
    @PostMapping("meeting")
    public Map<String, String> extractMeetingNotice(@RequestParam String path) throws IOException {

        File file = new File(path);
        String text = FileUtils.readFileToString(file, UTF_8);

        List<Term> terms = HanLP.newSegment().enableCustomDictionaryForcing(true).seg(text);

        //在词前增加分隔符用来 split
        Nature pcNature = Nature.fromString("HYTZ");
        for (Term term : terms) {
            if (term.nature == pcNature) {
                term.word = "|" + term.word;
            }
        }

        //去除首尾中括号
        String data = terms.toString().replaceAll("[\\[\\]]", "");
        LOG.debug("s1: {}", data);

        //必须用 StringUtils 才能过滤开头的空格
        String[] strs = StringUtils.split(data, "|");
        LOG.debug("s2: {}", Arrays.toString(strs));

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

        return m;
    }
}
