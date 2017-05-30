$LOAD_PATH.unshift('.') unless $LOAD_PATH.include?('.')
require 'agents_services_pb'
require 'mnist_loader'
require 'pry'

stub = LearningAgent::Stub.new('localhost:6565', :this_channel_is_insecure)

a = MnistLoader.training_set.get_data_and_labels([1])

binding.pry

imgs = a.map { |r| r[:image] }.join
labels = a.map {|r| r[:label]}.pack('C*')

stub.train_batch(TrainBatchRequest.new(count: a.count, labels: labels, pixels: imgs))
