$LOAD_PATH.unshift('.') unless $LOAD_PATH.include?('.')
require 'agents_services_pb'
require 'mnist_loader'
require 'concurrent'
require_relative 'trainer.rb'
require 'pry'

class Singleton
  attr_accessor :agent_addresses
  attr_accessor :associations

  def agent_address=(new_value)
    super(new_value)
    self.stubs = nil
  end

  def stubs
    @stubs ||= agent_addresses.map do |address|
      LearningAgent::Stub.new(address, :this_channel_is_insecure)
    end
  end
end

$singleton = Singleton.new

class CentralAgentImpl < CentralAgent::Service
  def start_training(start_training_request, _call)
    $singleton.agent_addresses = start_training_request.agentAddresses
    $singleton.associations = start_training_request.associations
    promises = $singleton.stubs.each_with_index.first($singleton.associations.count).map do |stub, index|
      trainer = Trainer.new(stub, MnistLoader.training_set.get_data_and_labels($singleton.associations[index].digits))
      Concurrent::Promise.execute { trainer.train }
    end
    promises.each(&:wait)
    Trained.new
  end

  def classify_image(classify_request, _call)
    promises = $singleton.stubs.first($singleton.associations.count).map do |stub|
      Concurrent::Promise.execute { [stub.classify_image_prob(classify_request), stub.classify_image_softmax(classify_request)] }
    end
    responses = promises.map(&:value)
    sumProb = Array.new(10, 0)
    sumSoftmax = Array.new(10, 0)
    responses.each do |response|
      begin
        sumProb = [sumProb, response[0].results].transpose.map {|x| x.reduce(:+)}
        sumSoftmax[response[1].result] += 1
      rescue
        puts "problem with response #{response}"
      end
    end
    resultProb = sumProb.each_with_index.max[1]
    resultSoftmax = sumSoftmax.each_with_index.max[1]
    ClassifyResponse.new(resultProb: resultProb, resultSoftmax: resultSoftmax)
  rescue Exception => e
    # binding.pry
    # raise e
    p e
  end
end

def main
  s = GRPC::RpcServer.new
  s.add_http2_port('0.0.0.0:6566', :this_port_is_insecure)
  s.handle(CentralAgentImpl)
  s.run_till_terminated
end

main