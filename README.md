## 制作自己的语料库

在【基于感知机制作 word2vec 语料库】一节中会用到这三个模型，可以根据实际需要打开或者关闭。
如果注释掉，就会使用 hanlp 的默认模型。
如果打开，就会加载自己训练的模型。

```
PerceptronCWSModelPath=data/model/perceptron/pku2681/cws.bin
PerceptronPOSModelPath=data/model/perceptron/pku2681/pos.bin
PerceptronNERModelPath=data/model/perceptron/pku2681/ner.bin
```

打包
```
mvn clean package -Dmaven.test.skip=true
```

启动，根据实际情况修改 hanlp 感知机模型的路径
```
java -jar nlp-facede-0.0.1-SNAPSHOT.jar \
     --app.nlp.corpus.v1=/opt/nlp_data/corpus/v1 \
     --app.nlp.corpus.v2=/opt/nlp_data/corpus/v2.txt \
     --app.nlp.model.cws=/opt/nlp_data/hanlp/data/model/perceptron/large/cws.bin \
     --app.nlp.model.pos=/opt/nlp_data/hanlp/data/model/perceptron/pku199801/pos.bin \
     --app.nlp.model.ner=/opt/nlp_data/hanlp/data/model/perceptron/pku199801/ner.bin \
     --app.nlp.model.vec=/opt/nlp_data/hanlp/data/model/word2vec/msr.txt
```

基于感知机制作人民日报2014语料格式语料库
```
curl -X POST http://localhost:8888/corpus/perceptron?path=/opt/nlp_data/text
``` 

基于感知机制作 word2vec 语料库
```
curl -X POST http://localhost:8888/corpus/word2vec?path=/opt/nlp_data/text
```

## 根据自己的语料库训练行业模型

在训练模型之前，要先确保模型生成的目录 pku2681、word2vec 已经存在。

停止之前的 App，重新使用下边的命令 run
```
java -jar nlp-facede-0.0.1-SNAPSHOT.jar \
     --app.nlp.corpus.v1=/opt/nlp_data/corpus/v1 \
     --app.nlp.corpus.v2=/opt/nlp_data/corpus/v2.txt \
     --app.nlp.model.cws=/opt/nlp_data/hanlp/data/model/perceptron/pku2681/cws.bin \
     --app.nlp.model.pos=/opt/nlp_data/hanlp/data/model/perceptron/pku2681/pos.bin \
     --app.nlp.model.ner=/opt/nlp_data/hanlp/data/model/perceptron/pku2681/ner.bin \
     --app.nlp.model.vec=/opt/nlp_data/hanlp/data/model/word2vec/msr.txt
```

### 基于 v1 语料库

训练 cws 分词模型
```
curl -X POST http://localhost:9090/model/cws
```

训练 pos 词性标注模型
```
curl -X POST http://localhost:9090/model/pos
```

训练 ner 实体识别模型
```
curl -X POST http://localhost:9090/model/ner
```

### 基于 v2 语料库

训练 word2vec 向量模型
```
curl -X POST http://localhost:9090/model/w2v
```

 