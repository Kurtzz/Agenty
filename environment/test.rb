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
# agents = ['10.132.0.3', '10.132.0.4', '10.132.0.5', '10.132.0.6', '10.132.0.7', '10.132.0.9'].map {|ip| Agent.new(ip, "6565")}
# agents = [Agent.new("127.0.0.1", "7001")]
agents = File.readlines('learning-agents-ips.txt').map do |ip|
  Agent.new(ip.strip.chomp, 6565)
end
# central_agent = CentralAgent::Stub.new('10.132.0.8:6566', :this_channel_is_insecure)
central_agent_ip = File.read('central-agent-ip.txt').strip.chomp
central_agent = CentralAgent::Stub.new("#{central_agent_ip}:6566", :this_channel_is_insecure)
# binding.pry

LearningSets.get.each do |set|
  File.open("#{set.slug}.dat", 'w+') do |file|
    central_agent.start_training(StartTrainingRequest.new(
      agentAddresses: agents.map(&:endpoint),
      associations: set.set.map { |ds| DigitAssociation.new(digits: ds)}
    ))
    (0..9).each do |digit|
      correctProb = 0
      correctSoftmax = 0
      overall = 0
      MnistLoader.test_set.get_data_and_labels([digit]).each do |example|
        response = central_agent.classify_image(ClassifyRequest.new(pixels: example[:image]))
        correctSoftmax += 1 if response.resultSoftmax == example[:targetInt]
        correctProb += 1 if response.resultProb == example[:targetInt]
        overall += 1
      end
      p [set, digit, correctProb, correctSoftmax, overall, correctProb.to_f / overall, correctSoftmax.to_f / overall]
      file.write([digit, correctProb.to_f / overall, correctSoftmax.to_f / overall].join("\t"))
      file.write("\n")
    end
  end
  `gnuplot -e "filename='#{set.slug}'" plot.p`
end

# keeper.down

