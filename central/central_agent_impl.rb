$LOAD_PATH.unshift('.') unless $LOAD_PATH.include?('.')
require 'agents_services_pb'
require 'mnist_loader'
require_relative 'trainer.rb'
require 'pry'

class Singleton
  attr_accessor :agent_addresses
end

$singleton = Singleton.new

class CentralAgentImpl < CentralAgent::Service
  def start_training(start_training_request, _call)
    $singleton.agent_addresses = start_training_request.agentAddresses
    # binding.pry
    associations = start_training_request.associations
    $singleton.agent_addresses.each_with_index do |address, index|
      trainer = Trainer.new(address, MnistLoader.training_set.get_data_and_labels(associations[index].digits))
      trainer.train
    end
    Trained.new
  # rescue Exception => e
  #   print e.backtrace
  end

  def classify_image(classify_request, _call)
    responses = $singleton.agent_addresses.map do |address|
      stub = LearningAgent::Stub.new(address, :this_channel_is_insecure)
      [stub.classify_image_prob(classify_request), stub.classify_image_softmax(classify_request)]
    end
    # binding.pry
    sumProb = Array.new(10, 0)
    sumSoftmax = Array.new(10, 0)
    responses.each do |response|
      sumProb = [sumProb, response[0].result].transpose.map {|x| x.reduce(:+)}
      sumSoftmax[response[1].result] += 1
    end
    resultProb = sumProb.each_with_index.max[1]
    resultSoftmax = sumSoftmax.each_with_index.max[1]
    # binding.pry
    ClassifyResponse.new(resultProb: resultProb, resultSoftmax: resultSoftmax)
  end
end

def main
  s = GRPC::RpcServer.new
  s.add_http2_port('0.0.0.0:6566', :this_port_is_insecure)
  s.handle(CentralAgentImpl)
  s.run_till_terminated
end

main