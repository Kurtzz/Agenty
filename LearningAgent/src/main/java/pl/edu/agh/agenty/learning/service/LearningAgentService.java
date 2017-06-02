package pl.edu.agh.agenty.learning.service;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.agh.agenty.learning.exception.WrongNumberOfPixelsException;
import pl.edu.agh.agenty.learning.grpc.ClassifyConcreteResponse;
import pl.edu.agh.agenty.learning.grpc.ClassifyProbResponse;
import pl.edu.agh.agenty.learning.grpc.ClassifyRequest;
import pl.edu.agh.agenty.learning.grpc.LearningAgentGrpc;
import pl.edu.agh.agenty.learning.grpc.ResetRequest;
import pl.edu.agh.agenty.learning.grpc.ResetResponse;
import pl.edu.agh.agenty.learning.grpc.TrainBatchRequest;
import pl.edu.agh.agenty.learning.grpc.TrainResponse;
import pl.edu.agh.agenty.learning.neural.NeuralNetworkManager;
import pl.edu.agh.agenty.learning.utils.LearningAgentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Kurtzz on 05.05.2017.
 */
@GRpcService
public class LearningAgentService extends LearningAgentGrpc.LearningAgentImplBase {
    private static final Logger log = LoggerFactory.getLogger(LearningAgentService.class);

    private NeuralNetworkManager networkManager;

    @Autowired
    public LearningAgentService(NeuralNetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public void trainBatch(TrainBatchRequest request, StreamObserver<TrainResponse> responseObserver) {
        DataSetIterator iterator = LearningAgentUtils.createDataSetIterator(request.getCount(), request.getPixels(), request.getLabels());
        networkManager.trainNetwork(iterator);
        responseObserver.onNext(TrainResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void classifyImageProb(ClassifyRequest request, StreamObserver<ClassifyProbResponse> responseObserver) {
        INDArray input = LearningAgentUtils.createINDArray(request.getPixels());
        INDArray result = networkManager.classifyDigit(input);

        if (result == null) {
            responseObserver.onError(new WrongNumberOfPixelsException());
            return;
        }

        List<Float> probabilities = new ArrayList<>();

        IntStream.range(0, result.length()).forEach(i -> probabilities.add(result.getFloat(i)));

        ClassifyProbResponse response = ClassifyProbResponse.newBuilder().addAllResults(probabilities).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void classifyImageSoftmax(ClassifyRequest request, StreamObserver<ClassifyConcreteResponse> responseObserver) {
        INDArray input = LearningAgentUtils.createINDArray(request.getPixels());
        INDArray output = networkManager.classifyDigit(input);

        int result = getDigitWithHighestProbability(output);

        if (result < 0) {
            log.error("Max probability is not a positive number.");
        }

        ClassifyConcreteResponse response = ClassifyConcreteResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reset(ResetRequest request, StreamObserver<ResetResponse> responseObserver) {
        networkManager.resetNetwork();

        responseObserver.onNext(ResetResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    private int getDigitWithHighestProbability(INDArray output) {
        int maxIndex = -1;
        float maxProbability = 0f;
        for (int i = 0; i < output.length(); i++) {
            if (output.getFloat(i) > maxProbability) {
                maxIndex = i;
                maxProbability = output.getFloat(i);
            }
        }

        return maxIndex;
    }
}
