package pl.edu.agh.agenty.learning.neural;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.agenty.learning.utils.NetworkProperties;

import static pl.edu.agh.agenty.learning.utils.Consts.NUM_PIXELS;
import static pl.edu.agh.agenty.learning.utils.Consts.OUTPUT_NUM;

/**
 * Created by Kurtzz on 05.05.2017.
 */
@Service
public class NeuralNetworkManager {
    private static final Logger log = LoggerFactory.getLogger(NeuralNetworkManager.class);
    private MultiLayerNetwork network;
    private static NetworkProperties properties;

    @Autowired
    public NeuralNetworkManager(NetworkProperties properties) {
        NeuralNetworkManager.properties = properties;
        this.network = buildNetwork();
    }

    private static MultiLayerNetwork buildNetwork() {
        log.info("Building multilayer network...");
        MultiLayerConfiguration configuration = new NeuralNetConfiguration.Builder()
                .seed(properties.getRandomSeed()) //include a random seed for reproducibility
                // use stochastic gradient descent as an optimization algorithm
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .iterations(1)
                .learningRate(0.006) //specify the learning rate
                .updater(Updater.NESTEROVS).momentum(0.9) //specify the rate of change of the learning rate.
                .regularization(true).l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder() //create the first, input layer with xavier initialization
                        .nIn(NUM_PIXELS)
                        .nOut(1000)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
                        .nIn(1000)
                        .nOut(OUTPUT_NUM)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false).backprop(true) //use backpropagation to adjust weights
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(configuration);
        model.setListeners(new ScoreIterationListener(10));
        model.init();

        return model;
    }

    public void trainNetwork(DataSetIterator iterator) {
        for (int i = 0; i < properties.getNumEpochs(); i++) {
            log.info("Training network (epoch: {}) ...", i + 1);
            network.fit(iterator);
        }
    }

    public INDArray classifyDigit(INDArray input) {
        log.info("Classifying digit...");
        if (input.length() != NUM_PIXELS) {
            log.error("Wrong number of pixels: {}", input.length());
            return null;
        }
        INDArray result = network.output(input);
        log.info("Digit classification: " + result);
        return result;
    }
}
