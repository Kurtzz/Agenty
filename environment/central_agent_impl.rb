$LOAD_PATH.unshift('.') unless $LOAD_PATH.include?('.')
require 'agents_services_pb'
require 'mnist_loader'
require_relative 'trainer.rb'

class CentralAgentImpl < CentralAgent::Service
  def start_training(start_training_request, _call)
    agent_addresses = start_training_request.agentAddresses
    associations = start_training_request.associations
    agent_addresses.each_with_index do |address, index|
      trainer = Trainer.new(address, MnistLoader.training_set.get_data_and_labels(associations[index]))
      trainer.train
    end
    Trained.new
  end

  def classify_image(classify_request, _call)
    pixels = classify_request.pixels
    # TO DO
    ClassifyConcreteResponse.new(resultProb: resultProb, resultSoftmax: resultSoftmax)
  end
end
