package io.batizhao.nlp.api;

import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
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
}
