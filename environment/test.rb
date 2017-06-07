$LOAD_PATH.unshift('.') unless $LOAD_PATH.include?('.')
require 'agents_services_pb'
require_relative 'keeper.rb'
require_relative 'agent.rb'
require_relative 'mnist_loader.rb'
# require_relative 'central_agent.rb'

require 'pry'


LEARNING_AGENT_IMAGE_NAME = 'learning_agent'.freeze

keeper = Keeper.new(Agent, LEARNING_AGENT_IMAGE_NAME, 10)
keeper.up

binding.pry
stub = CentralAgent::Stub.new('localhost:6565', :this_channel_is_insecure)
# stub.start_training(StartTrainingRequest.new(agentAddresses: ["localhost:6565"], associations:[DigitAssociation.new(digits: [1])]))
# stub.classify_image(ClassifyRequest.new(pixels: MnistLoader.training_set.get_data_and_labels([1]).first[:image]))
binding.pry


# keeper.down

