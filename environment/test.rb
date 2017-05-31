$LOAD_PATH.unshift('.') unless $LOAD_PATH.include?('.')
require 'agents_services_pb'
require 'mnist_loader'
require_relative 'keeper.rb'
require_relative 'trainer.rb'
require 'pry'

# stub = LearningAgent::Stub.new('localhost:6565', :this_channel_is_insecure)

# a = MnistLoader.training_set.get_data_and_labels([1])

# binding.pry

# imgs = a.map { |r| r[:image] }.join
# labels = a.map {|r| r[:label]}.pack('C*')

# stub.train_batch(TrainBatchRequest.new(count: a.count, labels: labels, pixels: imgs))

keeper = Keeper.new(Keeper::IMAGE_NAME, 1)
keeper.up

binding.pry

trainer = Trainer.new(keeper.agents.first, MnistLoader.training_set.get_data_and_labels([1]))
trainer.train

binding.pry
keeper.down