## 快速开始

首先需要部署模型到本地，这里把 data.zip 解压到 /opt/nlp_data/hanlp/ 这个目录。
否则你需要修改 application.yml 和 hanlp.properties 中相应的目录。
之后，你可以用 mvn spring-boot:run 启动，也可以用下边的 jar 方式启动。

打包
```
mvn clean package -Dmaven.test.skip=true
```

启动，根据实际情况修改 hanlp 感知机模型的路径
```
java -jar target/nlp-facede-0.0.1-SNAPSHOT.jar \
     --app.nlp.corpus.v1=/opt/nlp_data/corpus/v1 \
     --app.nlp.corpus.v2=/opt/nlp_data/corpus/v2.txt \
     --app.nlp.model.cws=/opt/nlp_data/hanlp/data/model/perceptron/200/cws.bin \
     --app.nlp.model.pos=/opt/nlp_data/hanlp/data/model/perceptron/200/pos.bin \
     --app.nlp.model.ner=/opt/nlp_data/hanlp/data/model/perceptron/200/ner.bin \
     --app.nlp.model.vec=/opt/nlp_data/hanlp/data/model/word2vec/jituangongsifawen.msr.txt
```

提取关键字
```
curl -X POST \
  'http://localhost:9090/api/keyword?title=转发信息产业部国家发展和改革委员会关于调整部分电信业务资费管理方式的通知&size=5' \
  -H 'Cache-Control: no-cache'
```

## 制作自己的语料库

在【基于感知机制作 word2vec 语料库】一节中会用到这三个模型，可以根据实际需要打开或者关闭。
如果注释掉，就会使用 hanlp 的默认模型。
如果打开，就会加载自己训练的模型。

```
PerceptronCWSModelPath=data/model/perceptron/pku2681/cws.bin
PerceptronPOSModelPath=data/model/perceptron/pku2681/pos.bin
PerceptronNERModelPath=data/model/perceptron/pku2681/ner.bin
```

基于感知机制作人民日报2014语料格式语料库
```
curl -X POST http://localhost:9090/corpus/perceptron?path=/opt/nlp_data/text
``` 

基于感知机制作 word2vec 语料库
```
curl -X POST http://localhost:9090/corpus/word2vec?path=/opt/nlp_data/text
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

 