$LOAD_PATH.unshift('.') unless $LOAD_PATH.include?('.')
require 'agents_services_pb'
require_relative 'keeper.rb'
require_relative 'agent.rb'
require_relative 'mnist_loader.rb'
# require_relative 'central_agent.rb'
require_relative 'learning_sets.rb'

require 'pry'


LEARNING_AGENT_IMAGE_NAME = 'learning_agent'.freeze

# keeper = Keeper.new(Agent, LEARNING_AGENT_IMAGE_NAME, 10)
# keeper.up
# agents = keeper.agents
agents = (7001..7006).map {|p| Agent.new(nil, p)}
central_agent = CentralAgent::Stub.new('localhost:6566', :this_channel_is_insecure)
binding.pry

LearningSets.get.each do |set|
  central_agent.start_training(StartTrainingRequest.new(
    agentAddresses: agents.map(&:endpoint),
    associations: set.set.map { |ds| DigitAssociation.new(digits: ds)}
  ))
  (0..9).each do |digit|
    correctProb = 0
    correctSoftmax = 0
    overall = 0
    MnistLoader.test_set.get_data_and_labels([digit]).sample(200).each do |example|
      response = central_agent.classify_image(ClassifyRequest.new(pixels: example[:image]))
      correctSoftmax += 1 if response.resultSoftmax == example[:targetInt]
      correctProb += 1 if response.resultProb == example[:targetInt]
      overall += 1
    end
    p [set, digit, correctProb, correctSoftmax, overall, correctProb.to_f / overall]
  end
end

# keeper.down

