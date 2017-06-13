package pl.edu.agh.agenty.learning.utils;

import com.google.protobuf.ByteString;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static pl.edu.agh.agenty.learning.utils.Consts.NUM_PIXELS;

/**
 * Created by Kurtzz on 05.05.2017.
 */
@Component
public class LearningAgentUtils {
    private static NetworkProperties properties;

    @Autowired
    public LearningAgentUtils(NetworkProperties properties) {
        LearningAgentUtils.properties = properties;
    }

    public static DataSetIterator createDataSetIterator(int count, ByteString pixels, ByteString labels) {
        List<Pair<INDArray, INDArray>> pairs = new LinkedList<>();
        byte[] image;
        byte label;
        Pair<INDArray, INDArray> pair;

        for (int i = 0; i < count; i++) {
            image = pixels.substring(i*(NUM_PIXELS), (i+1)*NUM_PIXELS).toByteArray();
            label = labels.byteAt(i);

            pair = new Pair<>(createINDArray(image), createINDArrayLabel(label));
            pairs.add(pair);
        }

        return new INDArrayDataSetIterator(pairs, properties.getBatchSize());
    }

    public static DataSet createDataSet(int count, ByteString pixels, ByteString labels) {
        byte[] image;
        byte label;
        INDArray first = Nd4j.create(count, NUM_PIXELS);
        INDArray second = Nd4j.create(count, 10);

        for (int i = 0; i < count; i++) {
            image = pixels.substring(i*(NUM_PIXELS), (i+1)*NUM_PIXELS).toByteArray();
            label = labels.byteAt(i);
            first.putRow(i, createINDArray(image));
            second.putRow(i, createINDArrayLabel(label));
        }

        return new DataSet(first, second);
    }

    public static INDArray createINDArray(ByteString pixels) {
        return createINDArray(pixels.toByteArray());
    }

    public static INDArray createINDArray(byte[] pixels) {
        INDArray indArray = Nd4j.create(NUM_PIXELS);

        for (int i = 0; i < NUM_PIXELS; i++) { //.length .size ?
            indArray = indArray.put(0, i, pixels[i]);
        }

        return indArray;
    }

    private static INDArray createINDArrayLabel(byte i) {
        return Nd4j.create(10).put(0, i, 1);
    }
}
